package com.example.max.teamctrl_fb;

import java.util.List;

/**
 * Created by max_1_000 on 11-10-2016.
 */

public class PlayerMatch {

    private int position;
    private int minute;
    private Player player;

    public PlayerMatch(Player player, int position) {
        this.player = player;
        this.position = position;
        this.minute = 0;
    }


    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
