package iss.workshop.android_game_t3;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    private final Context context;

    public ImageAdapter(Context context)
    {
        this.context=context;
    }

    @Override
    public int getCount() {
        return 12;
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
        ImageView imageView;
        if (convertView==null)
        {
            //imageView=new ImageView(this.context);
            //imageView.setLayoutParams(new ViewGroup.LayoutParams(360,360));
            //imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.tile,parent,false);
        }
        ImageView gameImageView = convertView.findViewById(R.id.gameImageView);
        gameImageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.dummy));
        return convertView;
    }
}
