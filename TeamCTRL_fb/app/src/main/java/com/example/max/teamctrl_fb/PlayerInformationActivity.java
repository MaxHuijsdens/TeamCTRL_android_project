package com.example.max.teamctrl_fb;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by max_1_000 on 25-10-2016.
 */

public class PlayerInformationActivity extends AppCompatActivity {


    private Player player;
    private SimpleDateFormat sdf;
    private ArrayList<String> matchIdsInvolved;
    private ArrayList<Match> matchesInvolved;
    private ArrayList<MatchEvent> matchEventsList;

    private ListView listViewRecentMatches;
    private ListView listViewMatchEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player_information);

        sdf = new SimpleDateFormat("dd/MM/yyyy");

        player = (Player) getIntent().getSerializableExtra("player");

        fillPlayerData();
        fillRelevantMatchesList();
    }

    @Override
    protected void onResume(){
        super.onResume();
        createMatchesList();
        createMatchesListview();
    }

    private void fillPlayerData(){
        TextView txt_name = (TextView) findViewById(R.id.txt_player_name);
        TextView txt_prefposition = (TextView) findViewById(R.id.txt_player_prefposition);

        txt_name.setText(player.getName());
        txt_prefposition.setText(player.getPreferredPosition());

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("team").child("players")
                .child(player.getPlayerId()).child("total_stats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> m = (Map<String, Object>) dataSnapshot.getValue();
                if(m!=null) {

                    TextView txt_injuries = (TextView) findViewById(R.id.txt_total_injuries);
                    TextView txt_scored = (TextView) findViewById(R.id.txt_total_scored);
                    TextView txt_subs = (TextView) findViewById(R.id.txt_total_subs);
                    TextView txt_faults = (TextView) findViewById(R.id.txt_total_faults);

                    if(m.get("injured") != null){
                        txt_injuries.setText(m.get("injured").toString());
                    }
                    if(m.get("scored") != null){
                        txt_scored.setText(m.get("scored").toString());
                    }
                    if(m.get("substitute") != null){
                        txt_subs.setText(m.get("substitute").toString());
                    }
                    if(m.get("fault") != null){
                        txt_faults.setText(m.get("fault").toString());
                    }
                    String id = (String) player.getPlayerId();

                    //Create firebase Storage
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    //Create storageReference
                    final StorageReference storageRef = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/teamctrl-880e4.appspot.com/o");
                    // Create a reference to a file from a Google Cloud Storage URI
                    StorageReference gsReference = storage.getReferenceFromUrl(storageRef + "/players/");
                    //Trying to get the picture
                    gsReference.child(id).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            //Load bytes into bitmap, creating the picture
                            ImageView image = (ImageView)findViewById(R.id.img_player_profilepicture);
                            Bitmap profilePicture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            image.setImageBitmap(profilePicture);
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            ImageView image = (ImageView)findViewById(R.id.img_player_profilepicture);
                            image.setImageResource(R.drawable.ic_person_outline_filled);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fillRelevantMatchesList(){

        matchIdsInvolved = new ArrayList<String>();
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("team").child("players")
        .child(player.getPlayerId()).child("matches").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Integer> m = (Map<String, Integer>) dataSnapshot.getValue();
                if(m!=null){
                    for (Map.Entry<String, Integer> entry : m.entrySet()) {
                        matchIdsInvolved.add(entry.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createMatchesList(){
        matchesInvolved = new ArrayList<Match>();
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("team").child("matches")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> matches = (Map<String, Object>) dataSnapshot.getValue();
                        for (Map.Entry<String, Object> entry : matches.entrySet()){
                            if(matchIdsInvolved.contains(entry.getKey())){
                                Map singleMatch = (Map) entry.getValue();
                                Match m;
                                try {
                                    m = new Match((String) singleMatch.get("opponent"),
                                            sdf.parse((String) singleMatch.get("dateString")),
                                            (String) singleMatch.get("score"));
                                } catch (ParseException e) {
                                    m = new Match((String) singleMatch.get("opponent"),
                                            null,
                                            (String) singleMatch.get("score"));
                                }
                                m.setMatch_id(entry.getKey());
                                matchesInvolved.add(m);
                                createMatchesListview();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    private void createMatchesListview(){

        listViewRecentMatches = (ListView) findViewById(R.id.lst_matched_played);
        listViewRecentMatches.setAdapter(new MatchRecentAdapter(this, R.layout.row_recent_matches_list_item, matchesInvolved));
        listViewRecentMatches.setItemsCanFocus(true);
        listViewRecentMatches.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Match match = (Match) listViewRecentMatches.getItemAtPosition(position);
                viewEventsPerPlayerPerMatch(match);
            }
        });
    }

    public void viewEventsPerPlayerPerMatch(Match match){

        matchEventsList = new ArrayList<MatchEvent>();

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("team").child("matches").child(match.getMatch_id()).child("match_events")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> matchEvents = (Map<String, Object>) dataSnapshot.getValue();
                        if (matchEvents != null) {
                            for (Map.Entry<String, Object> entry : matchEvents.entrySet()) {
                                Map singleMatchEvent = (Map) entry.getValue();

                                //Check to ensure only matchEvents that this player are involved in are used
                                if(singleMatchEvent.get("playerId").equals(player.getPlayerId()) ){
                                    Long minute = (Long) singleMatchEvent.get("minute");
                                    int minuteInt = minute.intValue();
                                    MatchEvent m = new MatchEvent(MatchEventType.getEnum((String) singleMatchEvent.get("type")),
                                            player,
                                            minuteInt
                                    );

                                    m.setDescription((String) singleMatchEvent.get("description"));
                                    matchEventsList.add(m);
                                    createMatchEventsListView();
                                }
                                else{
                                    Log.d("SME-id", String.valueOf(singleMatchEvent.get("playerId")));
                                    Log.d("P-id", player.getPlayerId());
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void createMatchEventsListView(){

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_match_events);

        ListView lv = (ListView ) dialog.findViewById(R.id.lst_match_events_info_dialog);
        dialog.setCancelable(true);
        dialog.setTitle("Events in this match");
        dialog.show();

        lv.setAdapter(new MatchEventAdapter(this, R.layout.row_match_event_list_item, matchEventsList));

    }
}
