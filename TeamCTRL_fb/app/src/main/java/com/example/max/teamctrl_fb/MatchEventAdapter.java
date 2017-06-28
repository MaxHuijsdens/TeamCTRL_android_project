package com.example.max.teamctrl_fb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by max_1_000 on 24-10-2016.
 */

public class MatchEventAdapter extends ArrayAdapter {

    private int resource;
    private LayoutInflater inflater;
    private Context context;

    public MatchEventAdapter(Context ctx, int resourceId, List Listobjects) {
        super(ctx, resourceId, Listobjects);
        resource = resourceId;
        inflater = LayoutInflater.from(ctx);
        context = ctx;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /* create a new view of my layout and inflate it in the row */
        convertView = (LinearLayout) inflater.inflate(resource, null);

        MatchEvent event = (MatchEvent) getItem(position);


        TextView txtPlayerName = (TextView) convertView.findViewById(R.id.txt_event_player_name);
        txtPlayerName.setText(event.getPlayer().getName());
        //Player name needs to be set, not id

        TextView txtMinute = (TextView) convertView.findViewById(R.id.txt_event_minute);

        Integer minute = event.getMinute();
        txtMinute.setText("Minute: "+minute.toString());

        TextView txtDescription = (TextView) convertView.findViewById(R.id.txt_event_description);
        txtDescription.setText(event.getType() + ": " + event.getDescription());


        if(event.getType() == MatchEventType.substitute) {
            if (event.getPlayerSubbedOut() != null) {
                TextView txtPlayerOut = (TextView) convertView.findViewById(R.id.txt_event_player_out);
                txtPlayerOut.setText("Out: " + event.getPlayerSubbedOut().getName());
                txtDescription.setText(event.getType().toString() + ": In-" + event.getPlayer().getName() + ", Out-" + event.getPlayerSubbedOut().getName());
            }
            else{
                txtDescription.setText(event.getType().toString() + ": In-" + event.getPlayer().getName());
            }
        }
        return convertView;
    }


}
