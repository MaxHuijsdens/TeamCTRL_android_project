package com.example.max.teamctrl_fb;

/**
 * Created by max_1_000 on 18-10-2016.
 */

public enum MatchEventType {
    injured("injured"),
    scored("scored"),
    substitute("substitute"),
    fault("fault");

    private String eventType;

    MatchEventType(String eventType){
        this.eventType = eventType;
    }

    @Override
    public String toString(){
        return eventType;
    }

    public static MatchEventType getEnum(String code){
        switch (code) {
            case "injured":
                return injured;
            case "scored":
                return scored;
            case "substitute":
                return substitute;
            case "fault":
                return fault;
            default:
                return null;
        }
    }
}
