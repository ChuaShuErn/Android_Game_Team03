package iss.workshop.android_game_t3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FetchActivity extends AppCompatActivity implements View.OnClickListener {

    private final int FETCH_IMAGES_MAX = 20;
    private final int SELECT_IMAGES_MAX = 6;
    private String mURL; // this is to hold the image catalogue URL
    private File myDirectory;
    private ArrayList<File> imgFileList;
    private ArrayList<String> imgUrlList;
    private ArrayList<ImageDTO> fetchedImages;
    private ArrayList<ImageDTO> selectedImages;
    private boolean isDownloadThreadRunning;
    private Thread downloadImageThread;

    //View attributes
    private EditText urlSearchBar;
    private Button fetchBtn;
    private GridView imageGridView;
    private SelectImgListener listener;
    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch);
        myDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        urlSearchBar = findViewById(R.id.urlSearchBar);
        fetchBtn = findViewById(R.id.fetchBtn);
        if (fetchBtn != null)
            fetchBtn.setOnClickListener(this);
        isDownloadThreadRunning = false; //create page with false running
        initGridView();
    }

    private void parseHTMLImgURLs() {

        try {
            Document doc = Jsoup.connect(mURL).get();
            Elements links = doc.select("img[src]");
            ArrayList<String> thisImgUrlList = new ArrayList<>();
            for (Element link : links) {
                if (link.attr("src").contains(".jpg") && link.attr("src").contains("https://")
                        && !link.attr("src").contains("?")) {
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

    protected int getImgUrlList() {
        try {
            parseHTMLImgURLs();
            if (imgUrlList != null && imgUrlList.size() >= 20) {
                return 1; //1 = all good
            } else if (imgUrlList.size() < 20) {
                return 2;
            } else
                return 3; //invalid url
        } catch (Exception e) {
            return 3;
        }
    }

    protected void enterNewURLToast(boolean isURLValid) {
        String msg = "";
        if (!isURLValid)
            msg = "Unable to parse webpage. \nPlease enter a valid URL.";
        else
            msg = "Insufficient images on webpage. \nPlease enter a URL with more images";

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    protected ImageDTO decodeImageIntoDTO(File DestFile, int imageID) {
        Bitmap bmp = BitmapFactory.decodeFile(DestFile.getAbsolutePath());
        return new ImageDTO(imageID, bmp);
    }

    protected boolean downloadThisImage(String imageURL, File destFile) {
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

        } catch (Exception e) {
            return false;
        }
    }

    protected void initGridView() {
        setDefaultImage();
        GridView gridView = (GridView) findViewById(R.id.fetchedImageGridView);
        adapter = new FetchedImageAdapter(this, fetchedImages);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.dummy);
        listener = new SelectImgListener(this, bitmap);

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(listener);
    }

    private void setDefaultImage() {
        fetchedImages = new ArrayList<>();
        for (int i = 0; i < FETCH_IMAGES_MAX; i++) {
            fetchedImages.add(new ImageDTO(R.drawable.unavailable, BitmapFactory.decodeResource(this.getResources(), R.drawable.unavailable)));
        }
    }

    @Override
    public void onClick(View view) { //this onClick will be the Fetch btn
        if (view != null) {
            mURL = urlSearchBar.getText().toString();
            if (isDownloadThreadRunning == true && downloadImageThread != null) {
                downloadImageThread.interrupt();
            }

            downloadImageThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    isDownloadThreadRunning = true; //start running, say True

                    imgFileList = createDestFiles(); //get twenty blank files to store
                    int fetchURLStatusCode = getImgUrlList();

                    if (fetchURLStatusCode == 1) {
                        System.out.println("All good---ImgURLList Have-" + imgUrlList.size() + " URL strings");
                        fetchedImages = new ArrayList<>();
                        for (int i = 0; i < FETCH_IMAGES_MAX; i++) {
                            if (downloadThisImage(imgUrlList.get(i), imgFileList.get(i))) {

                                //>onInterrupt() clear GridView + Progress, clear arrays to prevent overstacking
                                if (downloadImageThread.interrupted()) {
                                    /*
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            while(!housekeepOnDownloadInterrupt())
                                                ;
                                        }
                                    });
                                    */
                                    isDownloadThreadRunning = false;
                                    return;
                                }

                                int imgID = i + 1;
                                fetchedImages.add(decodeImageIntoDTO(imgFileList.get(i), imgID));
                                System.out.println("Adding fetchImages ImageDTO object ---->> No." + imgID);

                                runOnUiThread(new progressUiRunnable(imgID));

                            }
                        }
                        System.out.println("there are ..." + fetchedImages.size() + " imageDTO objects in fetchedImages");
                        setUpListener();
                        isDownloadThreadRunning = false;
                    } else {
                        if (fetchURLStatusCode == 2) { //
                            System.out.println(">>>> TOAST: Cannot get enough images");
                            runOnUiThread(() -> enterNewURLToast(true));
                        } else { // 3 = invalid URL
                            System.out.println(">>>> TOAST: invalid URL");
                            runOnUiThread(() -> enterNewURLToast(false));
                        }

                        isDownloadThreadRunning = false;
                    }
                }
            });
            downloadImageThread.start();

        }
    }

    private void setUpListener() {
        listener.setFiles(imgFileList);
        listener.setFetchedImages(fetchedImages);
        List<Boolean> list = new ArrayList<>(Arrays.asList(new Boolean[fetchedImages.size()]));
        Collections.fill(list, Boolean.FALSE);
        listener.setSelectedFlags(list);
        listener.setDownloadFinished(true);
    }

    public class progressUiRunnable implements Runnable {

        protected int imgIdDone;

        progressUiRunnable(int idDone) {
            super();
            this.imgIdDone = idDone;
        }

        @Override
        public void run() {
            updateProgressViews(imgIdDone);
        }
    }

    protected void updateProgressViews(int numberDone) {

        //1 - Update ProgressBar
        System.out.println("UPDATING PROGRESS BAR:  ==== " + numberDone);

        //2 - Update Progress Text
        System.out.println("UPDATING PROGRESS TEXT:  ==== " + numberDone);

        //3-  Update GridView with new image
        System.out.println("UPDATING GridView:  ==== " + numberDone);

        FetchedImageAdapter fetchedImageAdapter = new FetchedImageAdapter(this, fetchedImages);
        imageGridView = findViewById(R.id.fetchedImageGridView);
        if(imageGridView != null){
            imageGridView.setAdapter(fetchedImageAdapter);
        }


    }

    protected boolean housekeepOnDownloadInterrupt() {
        //1 - reset ProgressBar


        System.out.println("++++++Reset PROGRESS BAR after interrupt +++++ ");


        //2 - Update Progress Text
        System.out.println("++++++Reset PROGRESS Text after interrupt +++++ ");

        //3-  Update GridView with new image
        System.out.println("++++++Reset GridView after interrupt +++++ ");

        fetchedImages = null;
        imgFileList = null;

        return true;

    }

    public class FetchedImageAdapter extends BaseAdapter{

        private final Context context;
        private LayoutInflater inflater;

        public FetchedImageAdapter(Context context, ArrayList<ImageDTO> fetchedImages) {
            this.context = context;
            this.fetchedImages = fetchedImages;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        private ArrayList<ImageDTO> fetchedImages;

        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = inflater.inflate(R.layout.grid_item, parent, false);
            }

            ImageView imageView = convertView.findViewById(R.id.gridImage);

            Bitmap[] bitmaps = new Bitmap[20];

            for(int i = 0; i < fetchedImages.size(); i++){
                bitmaps[i] = fetchedImages.get(i).getBitmap();
            }

            imageView.setImageBitmap(bitmaps[position]);

            return convertView;
        }
    }


}