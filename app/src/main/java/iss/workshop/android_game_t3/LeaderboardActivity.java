package iss.workshop.android_game_t3;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        List<Player> leaderboardPlayerList = getPlayerList();
        Collections.sort(leaderboardPlayerList);

        ListView highScoreListView = findViewById(R.id.highScoreListView);
        LeaderboardAdapter adapter = new LeaderboardAdapter(this, (ArrayList<Player>) leaderboardPlayerList);
        highScoreListView.setAdapter(adapter);
        //get list of players with their score from shared pref
    }

    public ArrayList<Player> getPlayerList(){
        ArrayList<Player> playerList = new ArrayList<Player>();
        SharedPreferences pref = getSharedPreferences("Leaderboard", MODE_PRIVATE);
        int count = 0;
        while(pref.contains("player" + count)){
            count++;
        }
        //traverse the whole sharedPref
        for(int i=0; i<count; i++){
            String name = pref.getString("player"+i,"");
            Integer score = pref.getInt("score"+i, 99);

            Player player = new Player(name, score);
            playerList.add(player);
        }
        return playerList;
    }
}