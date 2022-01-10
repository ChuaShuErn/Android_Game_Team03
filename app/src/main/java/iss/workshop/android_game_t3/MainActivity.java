package iss.workshop.android_game_t3;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button fetchBtn = findViewById(R.id.btnPlay);
        Button tutorialBtn = findViewById(R.id.btnTutorial);
        Button leaderBoardBtn = findViewById(R.id.btnLeaderBoard);

        //MediaPlayer member variables
        Intent intent = new Intent(this, MyMusicService.class);
        intent.setAction("play_main");
        startService(intent);

        //set onClickListener
        if (fetchBtn != null) {
            fetchBtn.setOnClickListener(this);
        }

        if (tutorialBtn != null) {
            tutorialBtn.setOnClickListener(this);
        }

        if (leaderBoardBtn != null) {
            leaderBoardBtn.setOnClickListener(this);
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        Intent intent = new Intent(this, MyMusicService.class);
        intent.setAction("play_main");
        startService(intent);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnPlay) {


            //image adapter
            Intent intent = new Intent(this, FetchActivity.class);

            startActivity(intent);

        }

        if (id == R.id.btnTutorial) {


            //image adapter
            Intent intent = new Intent(this, PlayActivity.class);
            Intent intent = new Intent(this, TutorialActivity.class);

            startActivity(intent);

        }

        if (id == R.id.btnLeaderBoard) {


            //image adapter
            Intent intent = new Intent(this, LeaderboardActivity.class);

            startActivity(intent);

        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, ((arg0, arg1)-> super.onBackPressed())).create().show();
    }

//    //@Override
//   // public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//      //  MyMusicService.LocalBinder musicBinder = (MyMusicService.LocalBinder) iBinder;
//     //   if (musicBinder != null)
//     //   {
//       //     musicService = musicBinder.getService();
//     //       musicService.playMusic();
//      //  }
//    }

//    @Override
//    public void onServiceDisconnected(ComponentName componentName) {
//
//    }
}


