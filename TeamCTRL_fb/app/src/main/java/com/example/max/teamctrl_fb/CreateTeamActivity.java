package com.example.max.teamctrl_fb;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by max_1_000 on 9-10-2016.
 */

public class CreateTeamActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    //This activity is only called when a user does not have a team yet (when he first logs in)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_team);
    }

    public void createNewTeam(View v){
        EditText teamName = (EditText) findViewById(R.id.edit_team_name);
        String name = teamName.getText().toString();

        Team t = new Team(Sport.rugby, name);

        //Code to store our new team in the database, as a child of the logged in User
        Firebase ref = new Firebase("https://teamctrl-880e4.firebaseio.com/");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String userid = getIntent().getStringExtra("userid");
        mDatabase.child("users").child(userid).child("team").setValue(t);
        finish();
    }
}
