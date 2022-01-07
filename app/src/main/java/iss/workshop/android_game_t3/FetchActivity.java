package iss.workshop.android_game_t3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FetchActivity extends AppCompatActivity {

    private String mURL; // this is to hold the image catalogue URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch);

        Button fetchBtn = findViewById(R.id.btnFetch);
        fetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        EditText urlSearchBar = findViewById(R.id.urlSearchBar);
        mURL = urlSearchBar.getText().toString();
    }


}