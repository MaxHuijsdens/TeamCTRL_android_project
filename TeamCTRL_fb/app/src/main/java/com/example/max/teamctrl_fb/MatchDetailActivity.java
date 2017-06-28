package com.example.max.teamctrl_fb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Created by max_1_000 on 12-10-2016.
 */

public class MatchDetailActivity extends AppCompatActivity {
    //This activity is only called when a user does not have a team yet (when he first logs in)

    private Match match;
    private SimpleDateFormat sdf;
    private String key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_match_detail);

        sdf = new SimpleDateFormat("dd/MM/yyyy");

        match = (Match) getIntent().getSerializableExtra("match");

        TextView edit_opponent = (TextView) findViewById(R.id.edit_match_opponent);
        edit_opponent.setText(match.getOpponent());

        TextView edit_date = (TextView) findViewById(R.id.edit_match_date);
        edit_date.setText(sdf.format(match.getDate()));

        Spinner spinner = (Spinner) findViewById(R.id.spinner_strategy);
        spinner.setAdapter(new ArrayAdapter<Strategy>(this, android.R.layout.simple_list_item_1, Strategy.values()));

    }

    public void createLineUp(View v){
        Intent i = new Intent("com.example.max.teamctrl_fb.LineUpActivity");
        i.putExtra("match", (Serializable) match);
        startActivity(i);
    }

    public void startMatch(View v){
        Intent i = new Intent("com.example.max.teamctrl_fb.MatchCurrentActivity");
        i.putExtra("match", (Serializable) match);
        startActivity(i);
    }

}
