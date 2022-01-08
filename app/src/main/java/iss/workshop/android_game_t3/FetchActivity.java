package iss.workshop.android_game_t3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class FetchActivity extends AppCompatActivity implements View.OnClickListener {

    private final int FETCH_IMAGES_MAX = 20;
    private final int SELECT_IMAGES_MAX = 6;
    private String mURL; // this is to hold the image catalogue URL
    private Thread downloadImageThread;
    private File myDirectory;
    private ArrayList<File> imgFileList;
    private ArrayList<String> imgUrlList;
    private ArrayList<ImageDTO> fetchedImages;
    private ArrayList<ImageDTO> selectedImages;

    //View attributes
    private EditText urlSearchBar;
    private Button fetchBtn;
    private GridView imageGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch);
        myDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        urlSearchBar = findViewById(R.id.urlSearchBar);
        fetchBtn = findViewById(R.id.btnFetch);
        if (fetchBtn != null)
            fetchBtn.setOnClickListener(this);

        imgFileList = createDestFiles(); //get twenty blank files to store

        //hardcode first
        String thisURL = "https://stocksnap.io/view-photos/sort/trending/desc";


        parseHTMLImgURLs();

    }

    private void parseHTMLImgURLs() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect("https://stocksnap.io/view-photos/sort/trending/desc").get();
                    Elements links = doc.select("img[src]");
                    ArrayList<String> thisImgUrlList = new ArrayList<>();
                    for (Element link : links) {
                        if (link.attr("src").contains(".jpg")) {
                            System.out.println("printing....");
                            thisImgUrlList.add(link.attr("src"));
                        }
                    }

                    imgUrlList = thisImgUrlList; //will take 2 seconds to fetch URL, therefore can't call imgUrlList variable early otherwise null
                    for (String u : imgUrlList)
                        System.out.println(u);

                } catch (IOException e) {
                    ArrayList<String> thisImgUrlList = null;
                }
            }
        }).start();


    }

    protected ArrayList<File> createDestFiles() {
        ArrayList<File> imgFileList = new ArrayList<>();

        for (int i = 1; i <= FETCH_IMAGES_MAX; i++) {
            String thisFileName = "Image" + i + ".jpg";
            File thisFile = new File(myDirectory, thisFileName);
            imgFileList.add(thisFile);
            System.out.println("Created image files done: " + i);
        }
        return imgFileList;
    }

    protected boolean getImgUrlList(String mURL) {
        try {
            parseHTMLImgURLs();
            if (imgUrlList != null && imgUrlList.size() >= 20) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
    }


    protected ImageDTO decodeImageIntoDTO(File DestFile, int imageID) {
        Bitmap bmp = BitmapFactory.decodeFile(DestFile.getAbsolutePath());
        return new ImageDTO(imageID, bmp);

    }

    protected boolean downloadImage(String imageURL, File destFile) {
        try {
            URL myURL = new URL(imageURL); //parse the url String into a URL object
            URLConnection conn = myURL.openConnection(); // open connection for this URL obj

            InputStream in = conn.getInputStream(); // create an input stream to read received data
            FileOutputStream out = new FileOutputStream(destFile); // output stream to write data into destination file
            byte[] buffer = new byte[1024];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            in.close();
            return true;

        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onClick(View view) { //this onClick will be the Fetch btn
        if (view != null) {
            mURL = urlSearchBar.getText().toString();
            if (getImgUrlList(mURL)) {
                for (int i = 0; i<FETCH_IMAGES_MAX; i++){
                    if (downloadImage(imgUrlList.get(i), imgFileList.get(i)))
                    {
                        fetchedImages.add(decodeImageIntoDTO(imgFileList.get(i), i+1));
                    }
                }
            }

            downloadImageThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (File f : imgFileList) {


                    }
                }
            });

        }


    }


    public void updateProgress(int numberDone) {

    }

}