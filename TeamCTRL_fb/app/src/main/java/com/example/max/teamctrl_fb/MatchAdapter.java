package com.example.max.teamctrl_fb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by max_1_000 on 11-10-2016.
 */

public class MatchAdapter extends ArrayAdapter {
    private int resource;
    private LayoutInflater inflater;
    private Context context;


    public MatchAdapter(Context ctx, int resourceId, List Listobjects) {
        super(ctx, resourceId, Listobjects);
        resource = resourceId;
        inflater = LayoutInflater.from(ctx);
        context = ctx;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /* create a new view of my layout and inflate it in the row */
        convertView = (LinearLayout) inflater.inflate(resource, null);

        Match match = (Match) getItem(position);

        TextView txtOpponentName = (TextView) convertView.findViewById(R.id.txt_opponent_name);
        txtOpponentName.setText(match.getOpponent());

        TextView txtDate = (TextView) convertView.findViewById(R.id.txt_date);
        //To do: format date!
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        txtDate.setText(sdf.format(match.getDate()));

        return convertView;
    }
}