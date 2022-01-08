package iss.workshop.android_game_t3;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class LeaderboardAdapter extends ArrayAdapter<Object> {
    private Context context;
    private ArrayList<Player> leaderboardPlayerList = new ArrayList<Player>();

    public LeaderboardAdapter(@NonNull Context context, ArrayList<Player> leaderboardPlayerList) {
        super(context, R.layout.row_leaderboard);
        this.context = context;
        this.leaderboardPlayerList = leaderboardPlayerList;

        addAll(new Object[leaderboardPlayerList.size()]);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //super.getView(position, convertView, parent);

        if(convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_leaderboard, parent,false);
        }
        TextView rankTextView = convertView.findViewById(R.id.rankTextView);
        rankTextView.setText("");

        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        nameTextView.setText(leaderboardPlayerList.get(position).getName());

        TextView scoreTextView = convertView.findViewById(R.id.scoreTextView);
        scoreTextView.setText(leaderboardPlayerList.get(position).getScore().toString());

        return convertView;
    }
}


