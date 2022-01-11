package iss.workshop.android_game_t3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

public class LeaderboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        Intent intent = new Intent(this, MyMusicService.class);
        intent.setAction("play_leaderboard");
        startService(intent);

        ArrayList<Player> leaderboardPlayerList = getPlayerList();
        Collections.sort(leaderboardPlayerList);

        ListView highScoreListView = findViewById(R.id.highScoreListView);
        LeaderboardAdapter adapter = new LeaderboardAdapter(this, leaderboardPlayerList);
        highScoreListView.setAdapter(adapter);
        //get list of players with their score from shared pref
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LeaderboardActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public ArrayList<Player> getPlayerList() {
        ArrayList<Player> playerList = new ArrayList<>();
        SharedPreferences pref = getSharedPreferences("Leaderboard", MODE_PRIVATE);
        int count = 0;
        while(pref.contains("player" + count)){
            count++;
        }
        //traverse the whole sharedPref
        for(int i=0; i<count; i++){
            String name = pref.getString("player"+i,"");
            Integer score = pref.getInt("score"+i, 99);
            Long time = pref.getLong("time"+i,0);//add time

            Player player = new Player(name, score, time);
            playerList.add(player);
        }
        return playerList;
    }

    @Override
    public void onResume(){
        super.onResume();
        Intent intent = new Intent(this, MyMusicService.class);
        intent.setAction("play_leaderboard");
        startService(intent);
    }

    @Override
    public void onPause(){
        super.onPause();
        Intent intent = new Intent(this,MyMusicService.class);
        intent.setAction("pause_music");
        startService(intent);
    }
}