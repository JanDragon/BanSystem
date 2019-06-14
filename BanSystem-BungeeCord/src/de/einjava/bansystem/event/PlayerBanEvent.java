package de.einjava.bansystem.event;

import de.einjava.bansystem.BanSystem;

/**
 * Created by Leon on 10.04.2019.
 * development with love.
 * © Copyright by Papiertuch
 */

public class PlayerBanEvent extends CustomEvent {

    private String player;
    private String operator;
    private String reason;
    private long duration;

    public PlayerBanEvent(String player, String operator, String reason, long duration) {
        this.player = player;
        this.operator = operator;
        this.reason = reason;
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

    public String getReason() {
        return reason;
    }
}
