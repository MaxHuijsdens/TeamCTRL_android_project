package com.example.max.teamctrl_fb;

import java.util.List;

/**
 * Created by max_1_000 on 9-10-2016.
 */

public class Team {

    private int team_id;
    private List<Player> players;
    private Sport sport;
    private String name;

    public Team(List<Player> players, Sport sport, String name, int team_id) {
        this.players = players;
        this.sport = sport;
        this.name = name;
        this.team_id = team_id;
    }

    public Team(Sport sport, String name, int team_id) {
        this.sport = sport;
        this.name = name;
        this.team_id = team_id;
    }

    public Team(Sport sport, String name) {
        this.sport = sport;
        this.name = name;
    }


    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public int getId(){
        return team_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
