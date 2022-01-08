package iss.workshop.android_game_t3;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PlayActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private final ArrayList<ImageDTO> selectedImages = new ArrayList<>();
    private final ArrayList<ImageDTO> gameImages = new ArrayList<>();

    //--- This variables are used in the method onitemClick
    private ImageView image1 = null;
    private ImageView image2 = null;
    private int countMatchedPairs = 0;
    private int previousPosition = -1;
    private int numOfSelectedImage = 0;
    private final ArrayList<Integer> matchedImagePositions = new ArrayList<>();
    TextView matchText;
    private long clickedStartTime;
    private long clickedEndTime;

    //-- Variables to be used for threads
    Handler handler;
    Runnable runnable;

    //Variables to be used in onClick (submitBtn and okBtn)
    int score = 6;
    String inputName = "Diego ";
    AlertDialog myPopUpWinDialog;

    //This function is just a helper method -- to be deleted
    public void getSelectedImages() {
        selectedImages.add(new ImageDTO(R.drawable.laugh, BitmapFactory.decodeResource(this.getResources(), R.drawable.laugh)));
        selectedImages.add(new ImageDTO(R.drawable.peep, BitmapFactory.decodeResource(this.getResources(), R.drawable.peep)));

        selectedImages.add(new ImageDTO(R.drawable.snore, BitmapFactory.decodeResource(this.getResources(), R.drawable.snore)));
        selectedImages.add(new ImageDTO(R.drawable.what, BitmapFactory.decodeResource(this.getResources(), R.drawable.what)));

        selectedImages.add(new ImageDTO(R.drawable.tired, BitmapFactory.decodeResource(this.getResources(), R.drawable.tired)));
        selectedImages.add(new ImageDTO(R.drawable.stop, BitmapFactory.decodeResource(this.getResources(), R.drawable.stop)));
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

        //This is all to be deleted
        getSelectedImages();
        duplicateSelectedImages();

        //Shuffle the images so that the grid view has no adjacent same image
        Collections.shuffle(gameImages);

        //This method is used to display the start of the game (dummy images)
        initGridView();
        startTimer();
        initMatchView();


        //This is a thread to change displayed images back to dummy when there is no match
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                image1.setImageBitmap(BitmapFactory.decodeResource(PlayActivity.this.getResources(), R.drawable.dummy));
                image2.setImageBitmap(BitmapFactory.decodeResource(PlayActivity.this.getResources(), R.drawable.dummy));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        };
    }

    protected void initGridView() {
        GridView gridView = (GridView) findViewById(R.id.gameGridView);
        ImageAdapter imageAdapter = new ImageAdapter(this);

        if (gridView != null) {
            gridView.setAdapter(imageAdapter);
            gridView.setOnItemClickListener(this);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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

            image1.animate().rotationYBy(720).rotationXBy(720).setDuration(200).withEndAction(new Runnable() {
                @Override
                public void run() {
                    image1.setImageBitmap(gameImages.get(position).getBitmap());
                }
            });
            score--;
            clickedStartTime = System.currentTimeMillis();
        }
        //This code handles the second image click
        else if (image1 != null && image2 == null) {
            image2 = (ImageView) gridElement.getChildAt(0);

            //If the image 1 and image 2 are same
            if (gameImages.get(previousPosition).getBitmap() == gameImages.get(position).getBitmap()) {
                matchedImagePositions.add(previousPosition);
                matchedImagePositions.add(position);
                image2.animate().rotationYBy(720).rotationXBy(720).setDuration(200).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        image2.setImageBitmap(gameImages.get(position).getBitmap());
                    }
                });

                countMatchedPairs++;
                matchText.setText(countMatchedPairs + " of " + selectedImages.size() + " images");
                clickedEndTime = System.currentTimeMillis();
                if ((clickedEndTime - clickedStartTime) <= 5000) {
                    score += 5;
                    if ((clickedEndTime - clickedStartTime) <= 3000)
                        score += 3;
                } else
                    score += 3;
            } else {
                image2.animate().rotationYBy(720).rotationXBy(720).setDuration(200).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        image2.setImageBitmap(gameImages.get(position).getBitmap());
                    }
                });
                handler.postDelayed(runnable, 500);
                score--;
            }

            //If the number of matched images is same as selected image display winner
            if (countMatchedPairs == selectedImages.size()) {
                Toast.makeText(getApplicationContext(), "You win!", Toast.LENGTH_SHORT).show();
                PopUpWin();
            }
            numOfSelectedImage = 0;
        }
        if (score < 0) {
            //Implement GameOver function
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
        builder.setCancelable(true);
        myPopUpWinDialog = builder.create();

        //unpack all the view(congrats, score, EditName, SubmitBtn)
        TextView congratulation = myPopUpWin.findViewById(R.id.congratulations);

        TextView scoreTextView = myPopUpWin.findViewById(R.id.score);
        scoreTextView.setText("Your score is " + score);//need to get score from getScore()?

        Button submitBtn = myPopUpWin.findViewById(R.id.submitBtn);
        if (submitBtn != null) {
            submitBtn.setOnClickListener(this);
            submitBtn.setEnabled(false); //only set to true when the inputName is filled in
        }

        EditText inputName = myPopUpWin.findViewById(R.id.inputName);
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
            editor.putString("player" + i, inputName);//add nth player
            editor.putInt("score" + i, score); //add nth player's score //need to get score from getScore()?
            editor.commit();
            //data/data/iss.workshop.android_game_t3/shared_prefs/Leaderboard.xml -->check shared pref here

            //dismiss the pop up
            myPopUpWinDialog.dismiss();
            //intent send to leaderboard Activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }
    }


    @SuppressLint("SetTextI18n")
    protected void initMatchView() {

        matchText = findViewById(R.id.matchCounter);
        matchText.setText(countMatchedPairs + " of " + selectedImages.size() + " images");
    }

    protected void startTimer() {
        TextView timerView = findViewById(R.id.timer);
        long duration = TimeUnit.MINUTES.toMillis(1);
        ImageView timer = (ImageView) findViewById(R.id.timerView);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);

        new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String timeLeft = String.format(Locale.ENGLISH, "%02d : %02d"
                        , TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                        , TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                timerView.setText(timeLeft);
                if (millisUntilFinished < 10000) {
                    timerView.setTextColor(Color.RED);
                    timer.startAnimation(animation);
                }
            }

            @Override
            public void onFinish() {
                //Implement game over function
            }
        }.start();
    }
}