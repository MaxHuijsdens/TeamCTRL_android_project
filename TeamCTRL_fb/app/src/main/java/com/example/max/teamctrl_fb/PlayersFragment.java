package com.example.max.teamctrl_fb;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.utilities.Base64;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.example.max.teamctrl_fb.R.id.profilePic;


/**
 * Created by max_1_000 on 5-10-2016.
 */

public class PlayersFragment extends Fragment {

    private ListView listViewPlayers;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView mImageView;
    String mCurrentPhotoPath;
    private Uri photoURI;
    private Bitmap profilePicture;

    //The list of players that will be filled
    final List listPlayers = new ArrayList<Player>();




    private View fragmentView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            //createPlayerList();
        }
    }

    //The oncreateview is activated when this fragment is created, and every time the screen rotates
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_players, container, false);

        //Save this fragment's view, because we (may) need it later
        this.fragmentView = rootView;

        //Create all players
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            createPlayerList();
            createPlayerListView();
            Log.d("Create player list", "create player list");
        }
        //The register button and its onClickListener
        Button registerPlayerButton = (Button) rootView.findViewById(R.id.btn_register_player);
        registerPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //When the button is clicked, this method is called which causes a dialog to appear
                showRegisterPlayerDialog();
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mImageView.setImageBitmap(bitmap);
        }
    }

    //Method to create the list of players. To do: call again after adding new player
    public void createPlayerList(){


        //The path where all our players are stored.
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users")
                .child( FirebaseAuth.getInstance().getCurrentUser().getUid() ).child("team").child("players");

        //Method that just gets called once
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            //dataSnapshot contains all data from the path to our players
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> players = (Map<String, Object>) dataSnapshot.getValue();

                //We return our player data in a map. Loop through each value in the map and create a player object for each player

                //Create Storage instance
                FirebaseStorage storage = FirebaseStorage.getInstance();
                //Create storageReference
                final StorageReference storageRef = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/teamctrl-880e4.appspot.com/o");

                // Create a reference to a file from a Google Cloud Storage URI
                StorageReference gsReference = storage.getReferenceFromUrl(storageRef + "/players/");


                for (Map.Entry<String, Object> entry : players.entrySet()){
                    final Map singlePlayer = (Map) entry.getValue();

                    //Get unique key
                    final String key = entry.getKey();

                    //Check if the profile picture can be found
                    gsReference.child(key).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            try {
                                createPlayer(bytes, singlePlayer,key);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            //Player profile pic has not been found, getting standard image to add player to the list
                            //standard image
                            Bitmap profilePicture = BitmapFactory.decodeResource(getResources(), R.drawable.ic_person_outline_filled);

                            //Create player
                            Player p = new Player((String) singlePlayer.get("name"), (String) singlePlayer.get("preferredPosition"), profilePicture);
                            p.setPlayerId(key);
                            //Add player to list
                            listPlayers.add(p);

                            createPlayerListView();

                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void createPlayerListView(){
        //Add all results from database to our listview
        listViewPlayers = (ListView) fragmentView.findViewById(R.id.players_list);

        //Attach adapter to listview, to get a layout for each row
        listViewPlayers.setAdapter(new PlayerAdapter(getActivity(), R.layout.row_player_list_item, listPlayers));
        //Necessary for handling clicks
        listViewPlayers.setItemsCanFocus(true);
        listViewPlayers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Player p = (Player) listViewPlayers.getItemAtPosition(position);
                p.setProfilePicture(null);
                Intent i = new Intent("com.example.max.teamctrl_fb.PlayerInformationActivity");

                i.putExtra("player", (Serializable) p);
                startActivity(i);
            }
        });
    }


    public void createPlayer(byte[] bytes, Map singlePlayer, String key) throws IOException {

        //Load bytes into bitmap, creating the picture
        Bitmap profilePicture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        //Create player
        Player p = new Player((String) singlePlayer.get("name"), (String) singlePlayer.get("preferredPosition"), profilePicture   );
        p.setPlayerId(key);
        //Add player to list
        listPlayers.add(p);
        createPlayerListView();

    };

    public void showRegisterPlayerDialog(){

        //Build the dialog to create a new player
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setTitle("Register new player");

        //View of our dialog, use this dialogView to refer to elements in our dialog
        final View dialogView = inflater.inflate(R.layout.dialog_register_player,null);

        //Set mImageview to profile picture
        mImageView = (ImageView) dialogView.findViewById(profilePic);

        //The camera button and its onClickListener
        Button cameraButton = (Button) dialogView.findViewById(R.id.cameraTrigger);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                            photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        photoURI = FileProvider.getUriForFile(getActivity(),
                                "com.example.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, 1);
                    }
                }
            }
        });


        //Declare positive and negative buttons to our dialog, and display the dialog
        builder.setView(dialogView)
                .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        EditText edit_name = (EditText) dialogView.findViewById(R.id.edit_player_name);
                        EditText edit_pref_position = (EditText) dialogView.findViewById(R.id.edit_player_position);

                        final String name = edit_name.getText().toString();
                        final String preferred_position = edit_pref_position.getText().toString();

                        Player p = new Player(name, preferred_position, null);

                        //This code is necessary for collections in databases, such as our collection of players.
                        //The push().getKey() will make sure a unique key is generated each time we store a player
                        String key = FirebaseDatabase.getInstance().getReference().child("users")
                                .child(user.getUid()).child("team").child("players")
                                .push().getKey();


                        //if picture is made, store here in storage/keystring/picture
                        // Create FB storage reference
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReferenceFromUrl("gs://teamctrl-880e4.appspot.com");

                        // Create a child reference
                        StorageReference filepath = storageRef.child("players").child(key);

                        if(photoURI != null) {
                            //upload files
                            filepath.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //done
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //done
                                }
                            });
                        }
                        //Add the player to the database as a child of the key we just generated
                        FirebaseDatabase.getInstance().getReference().child("users")
                                .child(user.getUid()).child("team").child("players")
                                .child(key).setValue(p);

                        //TO DO: put the player's profile picture here if he has one
                        Bitmap profilePicture = BitmapFactory.decodeResource(getResources(), R.drawable.ic_person_outline_filled);
                        p.setProfilePicture(profilePicture);
                        p.setPlayerId(key);
                        listPlayers.add(p);
                        createPlayerListView();

                    }
                }) //If cancel is clicked, nothing happens
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Canceled", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }



}
