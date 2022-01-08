package iss.workshop.android_game_t3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SelectImgListener implements AdapterView.OnItemClickListener {
    private final AppCompatActivity currentActivity;
    private final Bitmap selectedBitmap;
    private final List<ImageDTO> fetchedImages;
    private final List<Boolean> selectedFlags;
    private boolean downloadFinished;

    public SelectImgListener(AppCompatActivity currentActivity, Bitmap selectedBitmap, List<ImageDTO> fetchedImages) {
        this.currentActivity = currentActivity;
        this.selectedBitmap = selectedBitmap;
        this.fetchedImages = fetchedImages;
        this.selectedFlags = new ArrayList<>(fetchedImages.size());
        this.downloadFinished = false;
    }

    public void setDownloadFinished(boolean downloadFinished) {
        this.downloadFinished = downloadFinished;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (!downloadFinished) {
            return;
        }

        GridView gridView = (GridView) adapterView;
        ViewGroup gridElement = (ViewGroup) gridView.getChildAt(position);

        Boolean selected = !selectedFlags.get(position);
        selectedFlags.set(position, selected);

        ImageView clickedView = (ImageView) gridElement.getChildAt(0);
        if (selected) {
            clickedView.setImageBitmap(selectedBitmap);
        } else {
            clickedView.setImageBitmap(fetchedImages.get(position).getBitmap());
        }

        int numOfSelected = Collections.frequency(selectedFlags, true);
        if (numOfSelected == 6) {
            List<ImageDTO> selectedImages = IntStream.range(0, selectedFlags.size())
                    .filter(selectedFlags::get)
                    .mapToObj(fetchedImages::get)
                    .collect(Collectors.toList());

            System.out.println("jump to game activity...");
            Intent intent = new Intent(this.currentActivity, PlayActivity.class);
            intent.putExtra("images", (Serializable) selectedImages);
            this.currentActivity.startActivity(intent);
        }
    }
}
