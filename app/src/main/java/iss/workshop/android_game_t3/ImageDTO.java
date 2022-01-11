package iss.workshop.android_game_t3;

import android.graphics.Bitmap;

public class ImageDTO {
    private final int id;
    private final Bitmap bitmap;

    public ImageDTO(int id, Bitmap bitmap) {
        this.id = id;
        this.bitmap = bitmap;
    }

    public int getId() {
        return id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
