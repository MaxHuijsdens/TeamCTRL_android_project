package com.example.max.teamctrl_fb;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by max_1_000 on 5-10-2016.
 */

public class MatchesFragment extends Fragment {


    private ListView listViewUpcomingMatches;
    private ListView listViewRecentMatches;

    private List listUpcomingMatches;
    private List listRecentMatches;

    private View fragmentView;

    private SimpleDateFormat sdf;


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            sdf = new SimpleDateFormat("dd/MM/yyyy");
            createMatchesLists();
            //createUpcomingMatchesList();
            //createRecentMatchesList();
        }

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_matches, container, false);
        this.fragmentView = rootView;

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            //sdf = new SimpleDateFormat("dd/MM/yyyy");

            createMatchesListViews();
            //createMatchesLists();
            //createUpcomingMatchesList();
            //createRecentMatchesList();
        }

        Button addMatchButton = (Button) rootView.findViewById(R.id.btn_new_match);
        addMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMatchDialog();
            }
        });

        return rootView;
    }


    public void createMatchesListViews(){
        listViewUpcomingMatches = (ListView) fragmentView.findViewById(R.id.upcoming_matches_list);
        listViewUpcomingMatches.setAdapter(new MatchAdapter(getActivity(), R.layout.row_upcoming_matches_list_item, listUpcomingMatches));
        listViewUpcomingMatches.setItemsCanFocus(true);

        listViewUpcomingMatches.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Match m = (Match) listViewUpcomingMatches.getItemAtPosition(position);

                Intent i = new Intent("com.example.max.teamctrl_fb.MatchDetailActivity");
                i.putExtra("match", (Serializable) m);
                startActivity(i);
            }
        });

        listViewRecentMatches = (ListView) fragmentView.findViewById(R.id.recent_matches_list);
        listViewRecentMatches.setAdapter(new MatchRecentAdapter(getActivity(), R.layout.row_recent_matches_list_item, listRecentMatches));
        listViewRecentMatches.setItemsCanFocus(true);
        listViewRecentMatches.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Match m = (Match) listViewRecentMatches.getItemAtPosition(position);

                Intent i = new Intent("com.example.max.teamctrl_fb.MatchRecentActivity");
                i.putExtra("match", (Serializable) m);
                startActivity(i);
            }
        });
    }

    public void createMatchesLists(){

        listUpcomingMatches = new ArrayList<Match>();
        listRecentMatches = new ArrayList<Match>();

        FirebaseDatabase.getInstance().getReference().child("users")
                .child( FirebaseAuth.getInstance().getCurrentUser().getUid() ).child("team").child("matches")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> matches = (Map<String, Object>) dataSnapshot.getValue();
                        if(matches != null){


                            for (Map.Entry<String, Object> entry : matches.entrySet()){
                                Map singleMatch = (Map) entry.getValue();
                                try {
                                    if(sdf.parse((String) singleMatch.get("dateString")).before(new Date()) || singleMatch.get("score") != null){

                                        Match m = new Match((String) singleMatch.get("opponent"),
                                                sdf.parse((String) singleMatch.get("dateString")),
                                                (String) singleMatch.get("score"));
                                        m.setMatch_id(entry.getKey());
                                        m.setLocation((String) singleMatch.get("location"));
                                        listRecentMatches.add(m);
                                        createMatchesListViews();
                                        Log.d("match added", "added: "+singleMatch.get("opponent"));
                                    }
                                    else {
                                        Match m = new Match((String) singleMatch.get("opponent"),
                                                sdf.parse((String) singleMatch.get("dateString")));
                                        m.setMatch_id(entry.getKey());
                                        m.setLocation((String) singleMatch.get("location"));

                                        listUpcomingMatches.add(m);

                                        createMatchesListViews();
                                    }
                                } catch (ParseException e) {e.printStackTrace();}
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });


    }

    public void createUpcomingMatchesList(){
        listUpcomingMatches = new ArrayList<Match>();
        FirebaseDatabase.getInstance().getReference().child("users")
                .child( FirebaseAuth.getInstance().getCurrentUser().getUid() ).child("team").child("matches")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> matches = (Map<String, Object>) dataSnapshot.getValue();

                        //We return our player data in a map. Loop through each value in the map and create a player object for each player
                        //And add that player object to our list of players
                        for (Map.Entry<String, Object> entry : matches.entrySet()){
                            Map singleMatch = (Map) entry.getValue();
                            try {
                                listUpcomingMatches.add(new Match((String) singleMatch.get("opponent"), sdf.parse((String) singleMatch.get("dateString"))));
                            } catch (ParseException e) {e.printStackTrace();}
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

        listViewUpcomingMatches = (ListView) fragmentView.findViewById(R.id.upcoming_matches_list);
        listViewUpcomingMatches.setAdapter(new MatchAdapter(getActivity(), R.layout.row_upcoming_matches_list_item, listUpcomingMatches));
        listViewUpcomingMatches.setItemsCanFocus(true);
    }

    public void createRecentMatchesList(){
        listRecentMatches = new ArrayList<Match>();


        try {

            listRecentMatches.add(new Match("Opponent 6", sdf.parse("26/8/2016"), "14-22"));
            listRecentMatches.add(new Match("Opponent 7", sdf.parse("13/7/2016"), "16-9"));
            listRecentMatches.add(new Match("Opponent 8", sdf.parse("12/3/2016"), "30-22"));
            listRecentMatches.add(new Match("Opponent 9", sdf.parse("7/8/2016"), "42-9"));
            listRecentMatches.add(new Match("Opponent 10", sdf.parse("11/5/2016"), "28-51"));

        } catch (ParseException e) {e.printStackTrace();}

        listViewRecentMatches = (ListView) fragmentView.findViewById(R.id.recent_matches_list);
        listViewRecentMatches.setAdapter(new MatchRecentAdapter(getActivity(), R.layout.row_recent_matches_list_item, listRecentMatches));
        listViewRecentMatches.setItemsCanFocus(true);
    }

    public void showAddMatchDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setTitle("Add new match");

        final View dialogView = inflater.inflate(R.layout.dialog_new_match, null);

        builder.setView(dialogView)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        EditText edit_opponent = (EditText) dialogView.findViewById(R.id.edit_match_opponent);
                        EditText edit_date = (EditText) dialogView.findViewById(R.id.edit_match_date);
                        final String opponent = edit_opponent.getText().toString();

                        String date = edit_date.getText().toString();

                        try {

                            Match m = new Match(opponent, sdf.parse(date));
                            //Parse and then format again in an attempt to make the dateString cleaner
                            m.setDateString(sdf.format(sdf.parse(date)));
                            String key = FirebaseDatabase.getInstance().getReference().child("users")
                                    .child(user.getUid()).child("team").child("matches")
                                    .push().getKey();
                            FirebaseDatabase.getInstance().getReference().child("users")
                                    .child(user.getUid()).child("team").child("matches")
                                    .child(key).setValue(m);

                            m.setMatch_id(key);
                            listUpcomingMatches.add(m);

                            createMatchesListViews();
                        } catch (ParseException e) {
                            Toast.makeText(getContext(), "Please enter a valid date", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "You clicked the CANCEL button in the dialog", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        builder.create().show();


    }

}