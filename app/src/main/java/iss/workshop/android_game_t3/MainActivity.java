package iss.workshop.android_game_t3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Button fetchBtn = findViewById(R.id.btnFetch);
            Button playBtn = findViewById(R.id.btnPlay);
            Button leaderBoardBtn = findViewById(R.id.btnLeaderBoard);

            //set onClickListener
            if (fetchBtn != null) {
                fetchBtn.setOnClickListener(this);
            }

            if (playBtn != null) {
                playBtn.setOnClickListener(this);
            }

            if(leaderBoardBtn != null){
                leaderBoardBtn.setOnClickListener(this);
            }

        }

        @Override
        public void onClick(View view) {
            int id = view.getId();

            if (id == R.id.btnFetch) {


                //image adapter
                Intent intent = new Intent(this, FetchActivity.class);

                startActivity(intent);

            }

            if (id == R.id.btnPlay) {


                //image adapter
                Intent intent = new Intent(this, PlayActivity.class);

                startActivity(intent);

            }

            if (id == R.id.btnLeaderBoard) {


                //image adapter
                Intent intent = new Intent(this, LeaderboardActivity.class);

                startActivity(intent);

            }

        }
    }


