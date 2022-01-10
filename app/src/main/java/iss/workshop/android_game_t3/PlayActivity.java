package iss.workshop.android_game_t3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private final ArrayList<ImageDTO> selectedImages = new ArrayList<>();
    private final ArrayList<ImageDTO> gameImages = new ArrayList<>();
    private Activity mActivity;

    //---This variables are used in the run-up timer
    private Chronometer stopWatch;
    private long stopTime;
    private boolean isStopWatchRunning;

    //--- This variables are used in the method onItemClick
    private ImageView image1 = null;
    private ImageView image2 = null;
    private int countMatchedPairs = 0;
    private int previousPosition = -1;
    private int numOfSelectedImage = 0;
    private final ArrayList<Integer> matchedImagePositions = new ArrayList<>();
    TextView matchText;
    private long clickedStartTime;
    private long mLastClickTime = 0;

    //-- Variables to be used for threads
    Handler handler;
    Runnable runnable;

    //Variables to be used in onClick (submitBtn and okBtn)
    int score = 0;
    EditText inputName;
    AlertDialog myPopUpWinDialog;
    AlertDialog myPopUpResetDialog;
    Button resetBtn;

    //Variables for media player
    MediaPlayer mediaPlayer;
    private final float max = 1.0f;

    //This function is just a helper method -- to be deleted
    public void getSelectedImages(List<String> filePaths) {
        if (filePaths == null || filePaths.size() == 0) {
            // FIXME: fallback images. remove it later.
            selectedImages.add(new ImageDTO(R.drawable.laugh, BitmapFactory.decodeResource(this.getResources(), R.drawable.laugh)));
            selectedImages.add(new ImageDTO(R.drawable.peep, BitmapFactory.decodeResource(this.getResources(), R.drawable.peep)));

            selectedImages.add(new ImageDTO(R.drawable.snore, BitmapFactory.decodeResource(this.getResources(), R.drawable.snore)));
            selectedImages.add(new ImageDTO(R.drawable.what, BitmapFactory.decodeResource(this.getResources(), R.drawable.what)));

            selectedImages.add(new ImageDTO(R.drawable.tired, BitmapFactory.decodeResource(this.getResources(), R.drawable.tired)));
            selectedImages.add(new ImageDTO(R.drawable.stop, BitmapFactory.decodeResource(this.getResources(), R.drawable.stop)));
        } else {
            for (int i = 0; i < filePaths.size(); i++) {
                Bitmap bitmap = BitmapFactory.decodeFile(filePaths.get(i));
                selectedImages.add(new ImageDTO(i, bitmap));
            }
        }
    }

    public void duplicateSelectedImages() {
        for (ImageDTO imageDTO : selectedImages) {
            gameImages.add(imageDTO);
            gameImages.add(imageDTO);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play);

        mActivity = PlayActivity.this;
        Intent intent = getIntent();
        List<String> imagePaths = intent.getStringArrayListExtra("image_paths");
        getSelectedImages(imagePaths);

        //This is all to be deleted
        duplicateSelectedImages();

        //Shuffle the images so that the grid view has no adjacent same image
        Collections.shuffle(gameImages);

        //This method is used to display the start of the game (dummy images)
        initGridView();
        startStopWatch();
        initMatchView();
        initResetBtn();


        //This is a thread to change displayed images back to dummy when there is no match
        handler = new Handler();
        runnable = () -> {
            image1.setImageBitmap(BitmapFactory.decodeResource(PlayActivity.this.getResources(), R.drawable.dummy));
            image2.setImageBitmap(BitmapFactory.decodeResource(PlayActivity.this.getResources(), R.drawable.dummy));

            image1.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            image2.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        };

        Intent musicIntent = new Intent(this, MyMusicService.class);
        musicIntent.setAction("play_game");
        startService(musicIntent);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Game")
                .setMessage("Are you sure you want to exit the game?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> {
                    Intent intent = new Intent(PlayActivity.this, MainActivity.class);
                    startActivity(intent);
                }).create().show();
    }

    protected void initGridView() {
        GridView gridView = findViewById(R.id.gameGridView);
        ImageAdapter imageAdapter = new ImageAdapter(this);

        if (gridView != null) {
            gridView.setAdapter(imageAdapter);
            gridView.setOnItemClickListener(this);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        //Prevent the user to click on another image before the animation ends.
        if (SystemClock.elapsedRealtime() - mLastClickTime < 600) return;


        GridView gridView = findViewById(R.id.gameGridView);
        ViewGroup gridElement = (ViewGroup) gridView.getChildAt(position);
        TextView scoreView = findViewById(R.id.score);

        //If no images are selected, set all dependent variables to null
        if (numOfSelectedImage == 0) {
            previousPosition = -1;
            image1 = null;
            image2 = null;
        }

        //if the same image is selected twice or an already matched image is selected then return
        if (position == previousPosition || matchedImagePositions.contains(position)) return;

        //This code handles the first image click
        if (previousPosition < 0 && image1 == null) {
            numOfSelectedImage++;
            previousPosition = position;
            image1 = (ImageView) gridElement.getChildAt(0);
            image1.setRotationY(0f);
            image1.animate().rotationY(90f).setDuration(150).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    image1.setImageBitmap(gameImages.get(position).getBitmap());
                    image1.setRotationY(270f);
                    image1.animate().rotationY(360f).setListener(null);
                }
            });
            clickedStartTime = System.currentTimeMillis();
        }
        //This code handles the second image click
        else if (image1 != null && image2 == null) {
            image2 = (ImageView) gridElement.getChildAt(0);

            //If the image 1 and image 2 are same
            if (gameImages.get(previousPosition).getBitmap() == gameImages.get(position).getBitmap()) {
                matchedImagePositions.add(previousPosition);
                matchedImagePositions.add(position);
                image2.setRotationY(0f);
                image2.animate().rotationY(90f).setDuration(150).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        image2.setImageBitmap(gameImages.get(position).getBitmap());
                        image2.setRotationY(270f);
                        image2.animate().rotationY(360f).setListener(null);
                        image1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                        image2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    }
                });

                countMatchedPairs++;
                matchText.setText(countMatchedPairs + " of " + selectedImages.size() + " images");
                //Calculating the score
                long timeTakenToClickBothImage = System.currentTimeMillis() - clickedStartTime;
                if ((timeTakenToClickBothImage) <= 3000)
                    score += 9; //Clicked the correct paired within 3 seconds
                else if ((timeTakenToClickBothImage) <= 5000)
                    score += 7;//Clicked the correct paired within 5 seconds
                else score += 5;

                //Play music
                playMatch();
            } else {

                image2.setRotationY(0f);
                image2.animate().rotationY(90f).setDuration(150).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        image2.setImageBitmap(gameImages.get(position).getBitmap());
                        image2.setRotationY(270f);
                        image2.animate().rotationY(360f).setListener(null);
                        image1.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        image2.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                    }
                });

                handler.postDelayed(runnable, 599);
                mLastClickTime = SystemClock.elapsedRealtime();
                score %= 19;

                //Play music
                playUnMatch();
            }

            //If the number of matched images is same as selected image display winner
            if (countMatchedPairs == selectedImages.size()) {
                stopStopWatch();
                //Calculating the final score
                if (stopTime <= 15000) score *= 54; //finish before 15s
                else if (stopTime <= 20000) score *= 45;
                else if (stopTime <= 25000) score *= 36;
                else if (stopTime <= 30000) score *= 27;
                else if (stopTime <= 40000) score *= 18;
                else if (stopTime <= 55000) score *= 9;
                else score *= 3;
                Toast.makeText(getApplicationContext(), "You win!", Toast.LENGTH_SHORT).show();
                PopUpWin();
            }
            numOfSelectedImage = 0;
        }
        scoreView.setText("Score: " + score);
    }

    public void PopUpWin() {
        //inflate popUpWin layout into a view
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View myPopUpWin = inflater.inflate(R.layout.pop_up_win, null, false);

        //putting the view into a pop up
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(myPopUpWin);
        builder.setCancelable(false);
        myPopUpWinDialog = builder.create();

        //unpack all the view(congrats, score, EditName, SubmitBtn)
        TextView congratulation = myPopUpWin.findViewById(R.id.congratulations);
        congratulation.setText(R.string.congratulation);

        TextView scoreTextView = myPopUpWin.findViewById(R.id.score);
        scoreTextView.setText(String.format("Your score is %s", score));//need to get score from getScore()?

        Button submitBtn = myPopUpWin.findViewById(R.id.submitBtn);
        if (submitBtn != null) {
            submitBtn.setOnClickListener(this);
            submitBtn.setEnabled(false); //only set to true when the inputName is filled in
        }

        inputName = myPopUpWin.findViewById(R.id.inputName);
        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //empty method
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (inputName.getText().toString().length() == 0) {
                    if (submitBtn != null)
                        submitBtn.setEnabled(false); //disable submit button when inputName empty
                } else if (submitBtn != null)
                    submitBtn.setEnabled(true);
            }

            @Override //repeat implementation on TextChanged
            public void afterTextChanged(Editable editable) {
                if (inputName.getText().toString().length() == 0) {
                    if (submitBtn != null)
                        submitBtn.setEnabled(false); //disable submit button when inputName empty
                } else if (submitBtn != null)
                    submitBtn.setEnabled(true);
            }
        });
        myPopUpWinDialog.show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.submitBtn) {
            //edit the share pre
            final SharedPreferences pref = getSharedPreferences("Leaderboard", MODE_PRIVATE);//initialize players.xml

            int i = 0;
            //to detect last player inside player.xml
            while (pref.contains("player" + i)) {
                i++;
            }

            SharedPreferences.Editor editor = pref.edit();
            editor.putString("player" + i, inputName.getText().toString());//add nth player
            editor.putInt("score" + i, score); //add nth player's score
            editor.putLong("time" + i, stopTime); //add nth player's time
            editor.apply();
            //data/data/iss.workshop.android_game_t3/shared_prefs/Leaderboard.xml -->check shared pref here

            //dismiss the pop up
            myPopUpWinDialog.dismiss();
            //intent send to leaderboard Activity
            Intent intent = new Intent(this, LeaderboardActivity.class);
            startActivity(intent);
        }
        if (id == R.id.resetGameBtn) {
            PopUpReset();
        }

        if (id == R.id.playAgainBtn) {
            //restart activity
            restartActivity(mActivity);
        }
    }

    public void PopUpReset() {
        //Inflate PopUpLose layout in a view
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View myPopUpLose = inflater.inflate(R.layout.pop_up_reset, null, false);

        //putting view into a pop-up
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(myPopUpLose);
        builder.setCancelable(false);
        myPopUpResetDialog = builder.create();

        //display Text and buttons

        final Button playAgainBtn = myPopUpLose.findViewById(R.id.playAgainBtn);

        //set Listener
        if (playAgainBtn != null) {
            playAgainBtn.setOnClickListener(this);
        }
        myPopUpResetDialog.show();


    }

    public void initResetBtn() {
        resetBtn = findViewById(R.id.resetGameBtn);
        if (resetBtn != null) {
            resetBtn.setOnClickListener(this);
        }
    }

    public void restartActivity(Activity activity) {
        if (Build.VERSION.SDK_INT >= 11) {
            activity.recreate();
        } else {
            activity.finish();
            activity.startActivity(activity.getIntent());
        }
    }

    @SuppressLint("SetTextI18n")
    protected void initMatchView() {
        matchText = findViewById(R.id.matchCounter);
        matchText.setText(countMatchedPairs + " of " + selectedImages.size() + " images");
    }

    protected void startStopWatch() {
        stopWatch = findViewById(R.id.chronometer);
        ImageView timerImage = findViewById(R.id.timerView);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);
        if (!isStopWatchRunning) {
            stopWatch.setBase(SystemClock.elapsedRealtime());
            stopWatch.start();
            isStopWatchRunning = true;
            timerImage.startAnimation(animation);
        }
    }

    protected void stopStopWatch() {
        ImageView timerImage = findViewById(R.id.timerView);
        if (isStopWatchRunning) {
            stopWatch.stop();
            stopTime = SystemClock.elapsedRealtime() - stopWatch.getBase();
            isStopWatchRunning = false;
            timerImage.clearAnimation();
        }
    }

    public void playMatch() {
        mediaPlayer = MediaPlayer.create(this, R.raw.correct);
        mediaPlayer.setVolume(max, max);
        mediaPlayer.start();
    }

    public void playUnMatch() {
        mediaPlayer = MediaPlayer.create(this, R.raw.wrong);
        mediaPlayer.setVolume(max, max);
        mediaPlayer.start();
    }
}