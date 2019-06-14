package de.einjava.bansystem.event;

import de.einjava.bansystem.BanSystem;

/**
 * Created by Leon on 14.04.2019.
 * development with love.
 * © Copyright by Papiertuch
 */

public class MuteReduceEvent extends CustomEvent {

    private String player;
    private String operator;
    private long duration;

    public MuteReduceEvent(String player, String operator, long duration) {
        this.player = player;
        this.operator = operator;
        this.duration = duration;
    }

    @Override
    public BanSystem getBanSystem() {
        return super.getBanSystem();
    }

    public long getDuration() {
        return duration;
    }

    public String getOperator() {
        return operator;
    }

    public String getPlayer() {
        return player;
    }

}
