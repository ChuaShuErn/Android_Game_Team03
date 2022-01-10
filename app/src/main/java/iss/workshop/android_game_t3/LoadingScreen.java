package iss.workshop.android_game_t3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class LoadingScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        TextView appName=findViewById(R.id.app_name);
        TextView team3=findViewById(R.id.team3);
        LottieAnimationView lottieAnimationView=findViewById(R.id.lottie);

        appName.animate().translationY(1400).setDuration(1000).setStartDelay(4000);
        team3.animate().translationY(1400).setDuration(1000).setStartDelay(4000);
        lottieAnimationView.animate().translationY(1400).setDuration(1000).setStartDelay(4000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(LoadingScreen.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },5300);

    }
}