package iss.workshop.android_game_t3;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class TutorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        Intent intent = new Intent(this, MyMusicService.class);
        intent.setAction("play_tutorial");
        startService(intent);
    }

    @Override
    public void onResume(){
        super.onResume();
        Intent intent = new Intent(this, MyMusicService.class);
        intent.setAction("play_tutorial");
        startService(intent);

    }

    @Override
    public void onPause(){
        super.onPause();
        Intent intent = new Intent(this, MyMusicService.class);
        intent.setAction("pause_music");
        startService(intent);

    }
}