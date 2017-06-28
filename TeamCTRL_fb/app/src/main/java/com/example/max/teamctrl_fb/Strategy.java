package com.example.max.teamctrl_fb;

/**
 * Created by max_1_000 on 11-10-2016.
 */

public enum Strategy {
    strategy_1("Strategy 1");


    private String strat;

    Strategy(String strat){
        this.strat = strat;
    }

    @Override
    public String toString(){
        return strat;
    }
}