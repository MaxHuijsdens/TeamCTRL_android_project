package com.example.max.teamctrl_fb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by max_1_000 on 24-10-2016.
 */

public class MatchRecentActivity extends AppCompatActivity {

    private Match match;
    private ListView listViewEvents;

    private List listEvents;

    private Map<String, Object> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_recent);

        match = (Match) getIntent().getSerializableExtra("match");
        fillPlayerMap();
        fillMatchData();
    }

    @Override
    protected void onResume(){
        super.onResume();
        createEventsList();
        createEventsListView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent a = new Intent(this, MainActivity.class);
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void fillMatchData(){
        TextView txt_opponent_name = (TextView) findViewById(R.id.txt_recent_match_opponent);
        TextView txt_date= (TextView) findViewById(R.id.txt_recent_match_date);
        TextView txt_score = (TextView) findViewById(R.id.txt_recent_match_score);


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = sdf.format(match.getDate());

        txt_opponent_name.setText(match.getOpponent());
        txt_date.setText(date + ", " + match.getLocation());
        txt_score.setText(match.getScore());
    }

    private void fillPlayerMap(){
        FirebaseDatabase.getInstance().getReference().child("users")
                .child( FirebaseAuth.getInstance().getCurrentUser().getUid() ).child("team").child("players")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        players = (Map<String, Object>) dataSnapshot.getValue();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void createEventsList(){
        listEvents = new ArrayList<MatchEvent>();
        FirebaseDatabase.getInstance().getReference().child("users")
                .child( FirebaseAuth.getInstance().getCurrentUser().getUid() )
                .child("team").child("matches").child(match.getMatch_id()).child("match_events")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot != null) {
                            Map<String, Object> matchEvents = (Map<String, Object>) dataSnapshot.getValue();
                            if (matchEvents != null) {
                                for (Map.Entry<String, Object> entry : matchEvents.entrySet()) {
                                    Map singleMatchEvent = (Map) entry.getValue();
                                    if(singleMatchEvent != null) {

                                        Long minute = (Long) singleMatchEvent.get("minute");
                                        int minuteInt = minute.intValue();

                                        Map playerMap = (Map) players.get((String) singleMatchEvent.get("playerId"));
                                        Player playerEvent = new Player((String)playerMap.get("name"), (String)playerMap.get("preferredPosition"), null);

                                        MatchEvent m = new MatchEvent(MatchEventType.getEnum((String) singleMatchEvent.get("type")),
                                                playerEvent,
                                                minuteInt
                                        );
                                        m.setDescription((String) singleMatchEvent.get("description"));

                                        if(m.getType() == MatchEventType.substitute){
                                            //m.setPlayerIdSubbedOut((String) singleMatchEvent.get("playerIdSubbedOut"));
                                            if(singleMatchEvent.get("playerIdSubbedOut") != null){
                                                Map playerOutMap = (Map) players.get((String) singleMatchEvent.get("playerIdSubbedOut"));
                                                Player playerOutEvent = new Player((String)playerOutMap.get("name"), (String)playerOutMap.get("preferredPosition"), null);
                                                m.setPlayerSubbedOut(playerOutEvent);
                                            }
                                        }

                                        listEvents.add(m);
                                    }
                                    createEventsListView();

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

    }

    public void createEventsListView(){
        listViewEvents = (ListView) findViewById(R.id.match_events_list);
        listViewEvents.setAdapter(new MatchEventAdapter(this, R.layout.row_match_event_list_item, listEvents));

        //necessary if we want do anything with handling clicks on match events
        listViewEvents.setItemsCanFocus(true);
    }

}
