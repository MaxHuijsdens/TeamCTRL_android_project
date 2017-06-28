package com.example.max.teamctrl_fb;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by max_1_000 on 12-10-2016.
 */

public class LineUpActivity extends AppCompatActivity {

    private Match match;
    private Context ctx;
    private ArrayList<ImageView> positions;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_lineup);

        retrievePlayers();

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
        }

        match = (Match) getIntent().getSerializableExtra("match");

        //loadLineUp();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadLineUp();
    }

    View.OnTouchListener touchListen = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d("Touch", "Touched " + v.getId());
            DragShadow dragShadow = new DragShadow(v);
            ClipData data = ClipData.newPlainText("", "");

            //If api level is 24 or higher, the new startDragAndDrop() is used.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                v.startDragAndDrop(data, dragShadow, v, 0);
            }
            //If api level is 23 or lower, the old, deprecated, startDrag() is used.
            else {
                v.startDrag(data, dragShadow, v, 0);
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

                    //If position is already assigned to a player, the imageviews and values need to be swapped
                    if (target.getTag() != null) {
                        String playerOut = (String) target.getTag();
                        String playerIn = (String) dragged.getTag();
                        Drawable playerOutDrawable = target.getDrawable();
                        Drawable playerInDrawable = dragged.getDrawable();

                        target.setTag(playerIn);
                        target.setImageDrawable(playerInDrawable);

                        addPlayerToBench(playerOut, playerOutDrawable);
                    } else {

                        target.setImageDrawable(dragged.getDrawable());

                        target.setTag((String) dragged.getTag());

                    }

                    removePlayerFromBench(dragged);
                    break;
            }

            return true;
        }
    };

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

    private void removePlayerFromBench(ImageView player) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_all_players);
        layout.removeView(player);
    }

    public void resetLineUp(View v) {
        for (ImageView image : positions) {
            image.setTag(null);
            image.setImageResource(R.drawable.ic_person_unknown);
        }
        retrievePlayers();
    }

    private void loadLineUp() {
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("team").child("matches")
                .child(match.getMatch_id()).child("lineup").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {
                    };
                    ArrayList<String> a = dataSnapshot.getValue(t);

                    if (a != null) {
                        Log.d("Arraylist tag", a.toString());
                        for (String key : a) {

                            if (key != null) {
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
                } catch (DatabaseException e) {
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

    public void saveLineUp(View v) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("team").child("matches")
                .child(match.getMatch_id()).child("lineup");

        for (ImageView image : positions) {
            Integer i = positions.indexOf(image) + 1;
            if (image.getTag() != null) {
                //Pair p = (Pair) image.getTag();

                //String playerid = p.first.toString();
                String playerid = (String) image.getTag();
                ref.child(i.toString()).setValue(playerid);

                //Player objects in firebase get the match id added to their data. This is a recommended practice by FireBase.
                FirebaseDatabase.getInstance().getReference().child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("team").child("players")
                        .child(playerid).child("matches").child(match.getMatch_id()).setValue(0);
            } else {
                ref.child(i.toString()).setValue(null);
            }
        }
        Toast.makeText(ctx, "The line up for this match has been saved", Toast.LENGTH_SHORT).show();
        finish();
    }


    private void retrievePlayers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("team").child("players");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            //dataSnapshot contains all data from the path to our players
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> players = (Map<String, Object>) dataSnapshot.getValue();


                final LinearLayout layout = (LinearLayout) findViewById(R.id.layout_all_players);
                layout.removeAllViews();


                //We return our player data in a map. Loop through each value in the map and create a player object for each player
                //And add that player object to our list of players
                for (Map.Entry<String, Object> entry : players.entrySet()) {

                    final ImageView image = new ImageView(ctx);
                    image.setImageResource(R.drawable.ic_person_outline_filled);
                    image.setOnTouchListener(touchListen);

                    //getting the key for every unique player
                    final String id = entry.getKey();
                    final Map singlePlayer = (Map) entry.getValue();

                    //load player here
                    createPlayer(id, singlePlayer, image, layout);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });}

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

                //setting image position
                //image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        //0));

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

                //Create player
                Pair p = new Pair(id, singlePlayer);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 100);
                lp.weight = 1;

                image.setLayoutParams(lp);
                //image.setTag(p);
                image.setTag(id);
                layout.addView(image);
            }
        });

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("LineUp Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}

class DragShadow extends View.DragShadowBuilder
{
public DragShadow(View view){
    super(view);
}
}


