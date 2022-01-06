package iss.workshop.android_game_t3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class PlayActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ImageView image1=null;
    private ImageView image2=null;
    private int countPair=0;
    private final int[] selectedImage=new int[]{
            R.drawable.laugh,R.drawable.peep,R.drawable.snore,
            R.drawable.stop,R.drawable.tired,R.drawable.what,
            R.drawable.laugh,R.drawable.peep,R.drawable.snore,
            R.drawable.stop,R.drawable.tired,R.drawable.what
    };
    int[] pos={0,1,2,3,4,5,0,1,2,3,4,5};
    int currentPos=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        initGridView();
    }

    protected void initGridView()
    {
        GridView gridView=(GridView) findViewById(R.id.gridView);
        ImageAdapter imageAdapter=new ImageAdapter(this);

        if (gridView!=null)
        {
            gridView.setAdapter(imageAdapter);
            gridView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (currentPos<0 && image1==null)
        {
            currentPos=position;
            image1=(ImageView) view;
            ((ImageView) view).setImageResource(selectedImage[pos[position]]);
        }
        else if (image1 !=null && image2==null)
        {
            if (currentPos==position)
            {
                return;
            }
            image2=(ImageView) view;
            if (pos[currentPos]!=pos[position])
            {
                image1.setImageResource(R.drawable.dummy);
                Toast.makeText(getApplicationContext(),"Not match",Toast.LENGTH_SHORT).show();
            }
            else
            {
                image2.setImageResource(selectedImage[pos[position]]);
                countPair++;
                if (countPair==6)
                {
                    Toast.makeText(getApplicationContext(),"You win!",Toast.LENGTH_SHORT).show();
                }
            }
            currentPos=-1;
            image1=null;
            image2=null;
        }
    }
}