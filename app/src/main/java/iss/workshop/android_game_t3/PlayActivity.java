package iss.workshop.android_game_t3;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class PlayActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ArrayList<ImageDTO> selectedImages = new ArrayList<>();
    private ArrayList<ImageDTO> gameImages = new ArrayList<>();

    //--- This variables are used in the method onitemClick
    private ImageView image1=null;
    private ImageView image2=null;
    private int countMatchedPairs=0;
    private int previousPosition=-1;
    private int numOfSelectedImage=0;
    private ArrayList<Integer> matchedImagePositions = new ArrayList<>();

    //-- Variables to be used for threads
    Handler handler;
    Runnable runnable;

    //This function is just a helper method -- to be deleted
    public void getSelectedImages(){
        selectedImages.add(new ImageDTO(R.drawable.laugh, BitmapFactory.decodeResource(this.getResources(),R.drawable.laugh)));
        selectedImages.add(new ImageDTO(R.drawable.peep, BitmapFactory.decodeResource(this.getResources(),R.drawable.peep)));

        selectedImages.add(new ImageDTO(R.drawable.snore, BitmapFactory.decodeResource(this.getResources(),R.drawable.snore)));
        selectedImages.add(new ImageDTO(R.drawable.what, BitmapFactory.decodeResource(this.getResources(),R.drawable.what)));

        selectedImages.add(new ImageDTO(R.drawable.tired, BitmapFactory.decodeResource(this.getResources(),R.drawable.tired)));
        selectedImages.add(new ImageDTO(R.drawable.stop, BitmapFactory.decodeResource(this.getResources(),R.drawable.stop)));
    }

    public void duplicateSeletedImages(){
        for(ImageDTO imageDTO:selectedImages){
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
        duplicateSeletedImages();

        //Shuffle the images so that the grid view has no adjacent same image
        Collections.shuffle(gameImages);

        //This method is used to display the start of the game (dummy images)
        initGridView();


        //This is a thread to change displayed images back to dummy when there is no match
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                image1.setImageBitmap(BitmapFactory.decodeResource(PlayActivity.this.getResources(),R.drawable.dummy));
                image2.setImageBitmap(BitmapFactory.decodeResource(PlayActivity.this.getResources(),R.drawable.dummy));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        };
    }

    protected void initGridView()
    {
        GridView gridView=(GridView) findViewById(R.id.gameGridView);
        ImageAdapter imageAdapter=new ImageAdapter(this);

        if (gridView!=null)
        {
            gridView.setAdapter(imageAdapter);
            gridView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        GridView gridView = findViewById(R.id.gameGridView);
        ViewGroup gridElement = (ViewGroup) gridView.getChildAt(position);

        //if the same image is selected twice or an already matched image is selected then return
        if(position==previousPosition || matchedImagePositions.contains(position)) return;

        //If no images are selected, set all dependent variables to null
        if(numOfSelectedImage == 0){
            previousPosition = -1;
            image1 = null;
            image2 = null;
        }

        //This code handles the first image click
        if(previousPosition<0 && image1 == null){
            numOfSelectedImage++;
            previousPosition = position;
            image1 = (ImageView) gridElement.getChildAt(0);
            image1.setImageBitmap(gameImages.get(position).getBitmap());
        }
        //This code handles the second image click
        else if(image1!=null && image2 == null){
            image2 = (ImageView) gridElement.getChildAt(0);

            //If the image 1 and image 2 are same
            if(gameImages.get(previousPosition).getBitmap()==gameImages.get(position).getBitmap()){
                matchedImagePositions.add(previousPosition);
                matchedImagePositions.add(position);
                image2.setImageBitmap(gameImages.get(position).getBitmap());
                countMatchedPairs++;
            }
            else{
                image2.setImageBitmap(gameImages.get(position).getBitmap());
                handler.postDelayed(runnable, 300);
            }

            //If the number of matched images is same as selected image display winner
            if (countMatchedPairs==selectedImages.size())
            {
                Toast.makeText(getApplicationContext(),"You win!",Toast.LENGTH_SHORT).show();
            }
            numOfSelectedImage = 0;
        }
    }
}