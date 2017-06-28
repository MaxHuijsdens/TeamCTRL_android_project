package com.example.max.teamctrl_fb;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by max_1_000 on 11-10-2016.
 */

public class Match implements Serializable{

    private Strategy strat;
    private String match_id;
    private String opponent;
    private String location;
    private Date date;
    private String dateString;
    private String season;
    private List<PlayerMatch> playerMatches;
    private String score;
    private List<MatchEvent> matchEvents;

    public Match(String opponent, Date date){
        this.opponent = opponent;
        this.date = date;

        this.strat = Strategy.strategy_1;
    }

    public Match(String opponent, Date date, String score){
        this.opponent = opponent;
        this.date = date;
        this.score = score;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public Strategy getStrat() {
        return strat;
    }

    public void setStrat(Strategy strat) {
        this.strat = strat;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getMatch_id() {
        return match_id;
    }

    public void setMatch_id(String match_id) {
        this.match_id = match_id;
    }

    public List<MatchEvent> getMatchEvents() {
        return matchEvents;
    }

    public void setMatchEvents(List<MatchEvent> matchEvents) {
        this.matchEvents = matchEvents;
    }
}