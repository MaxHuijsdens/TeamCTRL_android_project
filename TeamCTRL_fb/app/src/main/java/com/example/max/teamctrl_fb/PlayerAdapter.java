package com.example.max.teamctrl_fb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by max_1_000 on 9-10-2016.
 */

public class PlayerAdapter extends ArrayAdapter {
    private int resource;
    private LayoutInflater inflater;
    private Context context;


    public PlayerAdapter(Context ctx, int resourceId, List Listobjects) {
        super(ctx, resourceId, Listobjects);
        resource = resourceId;
        inflater = LayoutInflater.from(ctx);
        context = ctx;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /* create a new view of my layout and inflate it in the row */
        convertView = (RelativeLayout) inflater.inflate(resource, null);
         /* Extract the restaurant's object to show */
        Player player = (Player) getItem(position);

        /* Take the TextView from layout and set the player's name */
        TextView txtName = (TextView) convertView.findViewById(R.id.playerName);
        txtName.setText(player.getName());

        /* Take the TextView from layout and set the player's preferred position*/
        TextView txtWiki = (TextView) convertView.findViewById(R.id.playerPrefPosition);
        txtWiki.setText(player.getPreferredPosition());

        /*profile pic*/
        ImageView profileImg = (ImageView) convertView.findViewById(R.id.playerProfilePicture);

        profileImg.setImageBitmap(player.getProfilePicture());

        return convertView;
    }
}