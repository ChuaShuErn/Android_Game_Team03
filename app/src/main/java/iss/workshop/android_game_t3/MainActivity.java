package iss.workshop.android_game_t3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
    public void onPause(){
        super.onPause();
        Intent intent = new Intent(this, MyMusicService.class);
        intent.setAction("pause_music");
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
                .setPositiveButton(android.R.string.yes, ((arg0, arg1)->{
                    finishAffinity();
                    System.exit(0);})).create().show();
    }
}


