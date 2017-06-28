package com.example.max.teamctrl_fb;

import android.graphics.Bitmap;
import android.media.Image;

import java.io.Serializable;

/**
 * Created by max_1_000 on 9-10-2016.
 */

public class Player implements Serializable {

    private String name;
    private String preferredPosition;
    private Bitmap profilePicture;
    private Team team;

    private String playerId;

    public Player(String name, String preferredPosition, Bitmap profilePicture) {
        this.name = name;
        this.preferredPosition = preferredPosition;
        this.profilePicture = profilePicture;
    }

    public Player(String name, String preferredPosition, Bitmap profilePicture, Team team) {
        this.name = name;
        this.preferredPosition = preferredPosition;
        this.profilePicture = profilePicture;
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreferredPosition() {
        return preferredPosition;
    }

    public void setPreferredPosition(String preferredPosition) {
        this.preferredPosition = preferredPosition;
    }

    public Bitmap getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Bitmap profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}
