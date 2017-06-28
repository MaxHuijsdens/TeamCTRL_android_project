package com.example.max.teamctrl_fb;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by max_1_000 on 18-10-2016.
 */

public class MatchCurrentActivity extends AppCompatActivity {

    private Match match;
    private Context ctx;
    private ArrayList<ImageView> positions;
    private ArrayList<MatchEvent> events;
    private boolean matchActive;

    //Timing variables
    private Long startTime;

    //These variables are used for location detection
    private LocationManager locationManager;
    private android.location.LocationListener locationListener;
    private Location loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_match_current);

        match = (Match) getIntent().getSerializableExtra("match");

        positions = new ArrayList<ImageView>();
        positions.add((ImageView) findViewById(R.id.img_pos_1));
        positions.add((ImageView) findViewById(R.id.img_pos_2));
        positions.add((ImageView) findViewById(R.id.img_pos_3));
        positions.add((ImageView) findViewById(R.id.img_pos_4));
        positions.add((ImageView) findViewById(R.id.img_pos_5));
        positions.add((ImageView) findViewById(R.id.img_pos_6));
        positions.add((ImageView) findViewById(R.id.img_pos_7));
        positions.add((ImageView) findViewById(R.id.img_pos_8));
        positions.add((ImageView) findViewById(R.id.img_pos_9));
        positions.add((ImageView) findViewById(R.id.img_pos_10));
        positions.add((ImageView) findViewById(R.id.img_pos_11));
        positions.add((ImageView) findViewById(R.id.img_pos_12));
        positions.add((ImageView) findViewById(R.id.img_pos_13));
        positions.add((ImageView) findViewById(R.id.img_pos_14));
        positions.add((ImageView) findViewById(R.id.img_pos_15));

        for (ImageView image : positions) {
            image.setOnDragListener(dropListener);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getTag() != null) {
                        if (matchActive == true) {
                            showMatchEventDialog((String) v.getTag());
                        }
                    }
                }
            });
        }
        setupMinuteTextViewsWithImageViews();
        retrievePlayers();
        loadLineUp();
        events = new ArrayList<MatchEvent>();
    }

    private void loadLineUp() {
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("team").child("matches")
                .child(match.getMatch_id()).child("lineup").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                    ArrayList<String> a = dataSnapshot.getValue(t);

                    if(a != null){
                        Log.d("Arraylist tag", a.toString());
                        for(String key : a){

                            if(key != null){
                                int position = a.indexOf(key);
                                final ImageView image = positions.get(position - 1);

                                final LinearLayout layout = (LinearLayout) findViewById(R.id.layout_all_players);
                                layout.removeView(layout.findViewWithTag(key));

                                image.setTag(key);


                                final String id = key;

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
                                        Bitmap profilePicture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        image.setImageBitmap(profilePicture);
                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        image.setImageResource(R.drawable.ic_person_outline_filled);
                                    }
                                });
                            }

                        }
                    }
                }
                catch (DatabaseException e){
                    Map<Integer, Object> m = (Map<Integer, Object>) dataSnapshot.getValue();
                    if (m != null) {
                        for (Map.Entry<Integer, Object> entry : m.entrySet()) {

                            Integer i = Integer.parseInt(String.valueOf(entry.getKey()));
                            final ImageView image = positions.get(i - 1);

                            String player_id = (String) entry.getValue();

                            LinearLayout layout = (LinearLayout) findViewById(R.id.layout_all_players);
                            layout.removeView(layout.findViewWithTag(player_id));

                            image.setTag(player_id);
                            final String id = player_id;

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
                                    Bitmap profilePicture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    image.setImageBitmap(profilePicture);
                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    image.setImageResource(R.drawable.ic_person_outline_filled);
                                }
                            });

                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void setupMinuteTextViewsWithImageViews(){
        findViewById(R.id.img_pos_1).setTag(R.id.imageview_minute_textview, findViewById(R.id.txt_pos_1));
        findViewById(R.id.img_pos_2).setTag(R.id.imageview_minute_textview, findViewById(R.id.txt_pos_2));
        findViewById(R.id.img_pos_3).setTag(R.id.imageview_minute_textview, findViewById(R.id.txt_pos_3));
        findViewById(R.id.img_pos_4).setTag(R.id.imageview_minute_textview, findViewById(R.id.txt_pos_4));
        findViewById(R.id.img_pos_5).setTag(R.id.imageview_minute_textview, findViewById(R.id.txt_pos_5));
        findViewById(R.id.img_pos_6).setTag(R.id.imageview_minute_textview, findViewById(R.id.txt_pos_6));
        findViewById(R.id.img_pos_7).setTag(R.id.imageview_minute_textview, findViewById(R.id.txt_pos_7));
        findViewById(R.id.img_pos_8).setTag(R.id.imageview_minute_textview, findViewById(R.id.txt_pos_8));
        findViewById(R.id.img_pos_9).setTag(R.id.imageview_minute_textview, findViewById(R.id.txt_pos_9));
        findViewById(R.id.img_pos_10).setTag(R.id.imageview_minute_textview, findViewById(R.id.txt_pos_10));
        findViewById(R.id.img_pos_11).setTag(R.id.imageview_minute_textview, findViewById(R.id.txt_pos_11));
        findViewById(R.id.img_pos_12).setTag(R.id.imageview_minute_textview, findViewById(R.id.txt_pos_12));
        findViewById(R.id.img_pos_13).setTag(R.id.imageview_minute_textview, findViewById(R.id.txt_pos_13));
        findViewById(R.id.img_pos_14).setTag(R.id.imageview_minute_textview, findViewById(R.id.txt_pos_14));
        findViewById(R.id.img_pos_15).setTag(R.id.imageview_minute_textview, findViewById(R.id.txt_pos_15));
    }

    public void startMatch(View v) {
        if (matchActive == false) {
            matchActive = true;

            //Get current time, used for time
            startTime = System.currentTimeMillis()/1000;

            final ImageView i = (ImageView) findViewById(R.id.img_pos_1);
            //i.setTag(R.id.imageview_minute_textview, );
            i.setTag(R.id.imageview_minute_id, 0);

            Timer timer = new Timer();
            timer.schedule(new TimerTask(){
                public void run(){
                    Log.d("Timer","time");
                    final TextView txt_match_timer = (TextView) findViewById(R.id.txt_timer_match);
                    Integer oldMinuteMatch = Integer.parseInt(txt_match_timer.getText().toString());
                    final Integer newMinuteMatch = oldMinuteMatch+1;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txt_match_timer.setText(newMinuteMatch.toString());
                        }
                    });

                    for (ImageView image : positions) {
                        if(image.getTag() != null){

                            final TextView txt_minute = (TextView) image.getTag(R.id.imageview_minute_textview);
                            Integer oldMinute = Integer.parseInt(txt_minute.getText().toString());
                            final Integer newMinute = oldMinute+1;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txt_minute.setText(newMinute.toString());
                                }
                            });
                        }
                    }
                }
            },0,60000);

            //Set up the location manager and listener
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationListener = new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d("Lat&Long:", location.getLatitude() + " " + location.getLongitude());
                    loc = location;
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onProviderDisabled(String provider) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            };
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                    }, 10);

                    return;
                }
                else{
                    startLocationTracking();
                }
            } else {
                startLocationTracking();
            }
        }
    }

    private void startLocationTracking() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        //Location is checked every once in a while, in the stop match the final location will be saved.
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
        loc = locationManager.getLastKnownLocation("gps");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 10:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startLocationTracking();
                }
                return;
        }
    }

    public void showStopMatchDialog(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle("Stop match");

        final View dialogView = inflater.inflate(R.layout.dialog_end_match, null);

        builder.setView(dialogView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editScore = (EditText) dialogView.findViewById(R.id.edit_final_score);
                        String score = editScore.getText().toString();
                        stopMatch(score);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ctx, "Canceled", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }


    public void stopMatch(String score) {
        if (matchActive == true) {
            matchActive = false;
            match.setMatchEvents(events);
            match.setScore(score);

            //Set end score in database
            FirebaseDatabase.getInstance().getReference().child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("team")
                    .child("matches").child(match.getMatch_id()).child("score").setValue(score);

            //Set all match events in database
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("team")
                    .child("matches").child(match.getMatch_id()).child("match_events");

            for (MatchEvent event : match.getMatchEvents()) {
                String key = ref.push().getKey();
                ref.child(key).setValue(event);
            }
            updateTotalEventsPerPlayer();
            //Geocoding: last recorded location into database
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                String matchLocation;

                List<Address> addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                if(addresses != null) {
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    matchLocation = address + ", " + city;
                }
                else {
                    matchLocation = "Unknown address";
                }
                match.setLocation(matchLocation);
                FirebaseDatabase.getInstance().getReference().child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("team")
                        .child("matches").child(match.getMatch_id()).child("location").setValue(matchLocation);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(ctx, "The match has ended.", Toast.LENGTH_SHORT).show();

            //Go to the evaluation screen after a match has ended.
            Intent i = new Intent("com.example.max.teamctrl_fb.MatchRecentActivity");
            i.putExtra("match", (Serializable) match);
            startActivity(i);

        }
    }


    private void updateTotalEventsPerPlayer(){
        for(final MatchEvent event : events){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("team")
                    .child("players").child(event.getPlayerId()).child("total_stats").child(event.getType().toString());

            ref.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Integer i = (Integer) mutableData.getValue();
                    if(i == null){
                        mutableData.setValue(1);
                    }
                    else{
                        mutableData.setValue(i + 1);
                    }
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    Log.d("UpdateEventsPerPlayer", "updated: " + event.getPlayerId() + ", " + event.getType().toString());
                }
            });

        }
    }

    private void showMatchEventDialog(final String player_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle("Match event");

        final View dialogView = inflater.inflate(R.layout.dialog_new_match_event, null);

        builder.setView(dialogView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText description_edit = (EditText) dialogView.findViewById(R.id.edit_event_description);
                        String description = description_edit.getText().toString();

                        RadioButton injured = (RadioButton) dialogView.findViewById(R.id.rbn_injured);
                        RadioButton scored = (RadioButton) dialogView.findViewById(R.id.rbn_scored);
                        RadioButton fault = (RadioButton) dialogView.findViewById(R.id.rbn_fault);

                        MatchEvent newMatchEvent;
                        Long currentTime = System.currentTimeMillis()/1000;
                        Long timeElapsed = currentTime - startTime;
                        Log.d("Time elapsed seconds", timeElapsed.toString());
                        Long timeElapsedMinutes = timeElapsed / 60;
                        Log.d("Time elapsed minutes", timeElapsedMinutes.toString());
                        Integer timeElapsedMinutesInt = timeElapsedMinutes.intValue();
                        Log.d("TimeElapsedMinutesInt", timeElapsedMinutesInt.toString());

                        if (injured.isChecked()) {
                            newMatchEvent = new MatchEvent(MatchEventType.injured, player_id, timeElapsedMinutesInt);
                            newMatchEvent.setDescription(description);
                        } else if (scored.isChecked()) {
                            newMatchEvent = new MatchEvent(MatchEventType.scored, player_id, timeElapsedMinutesInt);
                            newMatchEvent.setDescription(description);
                        } else if (fault.isChecked()) {
                            newMatchEvent = new MatchEvent(MatchEventType.fault, player_id, timeElapsedMinutesInt);
                            newMatchEvent.setDescription(description);
                        } else {
                            newMatchEvent = null;
                            Toast.makeText(ctx, "You did not select an event", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                        events.add(newMatchEvent);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ctx, "Canceled", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }


    private void addPlayerToBench(String playerId, Drawable playerDrawable) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_all_players);
        ImageView image = new ImageView(ctx);
        image.setImageDrawable(playerDrawable);
        image.setOnTouchListener(touchListen);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 100);
        lp.weight = 1;

        image.setLayoutParams(lp);
        image.setTag(playerId);
        layout.addView(image);
    }

    private void retrievePlayers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("team").child("players");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> players = (Map<String, Object>) dataSnapshot.getValue();


                LinearLayout layout = (LinearLayout) findViewById(R.id.layout_all_players);
                layout.removeAllViews();

                //We return our player data in a map. Loop through each value in the map and create a player object for each player
                //And add that player object to our list of players
                for (Map.Entry<String, Object> entry : players.entrySet()) {

                    ImageView image = new ImageView(ctx);
                    image.setOnTouchListener(touchListen);

                    String id = entry.getKey();
                    Map singlePlayer = (Map) entry.getValue();
                    //load player here
                    createPlayer(id, singlePlayer, image, layout);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void createPlayer(final String id, final Map singlePlayer, final ImageView image, final LinearLayout layout) {

        //Initiating firebase
        //Create Storage instance
        FirebaseStorage storage = FirebaseStorage.getInstance();
        //Create storageReference
        final StorageReference storageRef = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/teamctrl-880e4.appspot.com/o");
        // Create a reference to a file from a Google Cloud Storage URI
        StorageReference gsReference = storage.getReferenceFromUrl(storageRef + "/players/");

        //Trying to get the picture
        gsReference.child(id).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                loadPlayer(bytes, singlePlayer);
            }

            private void loadPlayer(byte[] bytes, Map singlePlayer) {

                //Load bytes into bitmap, creating the picture
                Bitmap profilePicture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                //Create player
                Pair p = new Pair(id, singlePlayer);
                image.setImageBitmap(profilePicture);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 100);
                lp.weight = 1;

                image.setLayoutParams(lp);
                //image.setTag(p);
                image.setTag(id);
                layout.addView(image);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                //Player profile pic has not been found, getting standard image to add player to the list
                //standard image
                Bitmap profilePicture = BitmapFactory.decodeResource(getResources(), R.drawable.ic_person_outline_filled);
                image.setImageBitmap(profilePicture);
                //Create player
                Pair p = new Pair(id, singlePlayer);
                //image.setTag(p);


                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 100);
                lp.weight = 1;

                image.setLayoutParams(lp);


                image.setTag(id);
                layout.addView(image);
            }
        });

    }


    View.OnTouchListener touchListen = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (matchActive == true) {
                Log.d("Touch", "Touched " + v.getId());
                MatchCurrentActivity.DragShadow dragShadow = new MatchCurrentActivity.DragShadow(v);
                ClipData data = ClipData.newPlainText("", "");

                //If api level is 24 or higher, the new startDragAndDrop() is used.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    v.startDragAndDrop(data, dragShadow, v, 0);
                }
                //If api level is 23 or lower, the old, deprecated, startDrag() is used.
                else {
                    v.startDrag(data, dragShadow, v, 0);
                }
            }
            return false;
        }
    };

    View.OnDragListener dropListener = new View.OnDragListener() {
        @Override


        public boolean onDrag(View v, DragEvent event) {
            int dragEvent = event.getAction();
            switch (dragEvent) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.d("Drag event", "Entered");
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    Log.d("Drag event", "Exited");
                    break;
                case DragEvent.ACTION_DROP:
                    Log.d("Drag event", "Dropped");
                    ImageView target = (ImageView) v;
                    ImageView dragged = (ImageView) event.getLocalState();

                    Long currentTime = System.currentTimeMillis()/1000;
                    Long timeElapsed = currentTime - startTime;
                    Long timeElapsedMinutes = timeElapsed / 60;
                    Integer timeElapsedMinutesInt = timeElapsedMinutes.intValue();
                    Log.d("TimeElapsedMinutesInt", timeElapsedMinutesInt.toString());

                    //Create a MatchEvent for the subbed in player
                    MatchEvent substitute = new MatchEvent(MatchEventType.substitute, (String) dragged.getTag(), timeElapsedMinutesInt);

                    TextView txt_minute = (TextView) target.getTag(R.id.imageview_minute_textview);
                    txt_minute.setText("0");

                    if (target.getTag() != null) {
                        String playerOut = (String) target.getTag();
                        String playerIn = (String) dragged.getTag();
                        Drawable playerOutDrawable = target.getDrawable();
                        Drawable playerInDrawable = dragged.getDrawable();

                        target.setTag(playerIn);
                        target.setImageDrawable(playerInDrawable);

                        addPlayerToBench(playerOut, playerOutDrawable);

                        //Indicate to the MatchEvent which player was subbed out
                        substitute.setPlayerIdSubbedOut(playerOut);

                    } else {

                        target.setImageDrawable(dragged.getDrawable());

                        target.setTag((String) dragged.getTag());


                    }
                    events.add(substitute);
                    removePlayerFromBench(dragged);
                    break;

            }

            return true;
        }
    };

    private void removePlayerFromBench(ImageView player) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_all_players);
        layout.removeView(player);
    }


    private class DragShadow extends View.DragShadowBuilder
    {
        public DragShadow(View view){
            super(view);
        }
    }

}
