package com.example.max.teamctrl_fb;

import java.io.Serializable;

/**
 * Created by max_1_000 on 18-10-2016.
 */

public class MatchEvent implements Serializable {

    private MatchEventType type;
    private String playerId;
    private Player player;

    private int minute;

    private String description;

    private String playerIdSubbedOut;
    private Player playerSubbedOut;


    public MatchEvent(MatchEventType type, String playerId, int minute){
        this.type = type;
        this.playerId = playerId;
        this.minute = minute;
    }

    public MatchEvent(MatchEventType type, Player player, int minute){
        this.type = type;
        this.player = player;
        this.minute = minute;
    }

    public MatchEventType getType() {
        return type;
    }

    public void setType(MatchEventType type) {
        this.type = type;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlayerIdSubbedOut() {
        return playerIdSubbedOut;
    }

    public void setPlayerIdSubbedOut(String playerIdSubbedOut) {
        this.playerIdSubbedOut = playerIdSubbedOut;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayerSubbedOut() {
        return playerSubbedOut;
    }

    public void setPlayerSubbedOut(Player playerSubbedOut) {
        this.playerSubbedOut = playerSubbedOut;
    }
}
