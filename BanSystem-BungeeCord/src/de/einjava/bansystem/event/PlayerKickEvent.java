package de.einjava.bansystem.event;

import de.einjava.bansystem.BanSystem;

/**
 * Created by Leon on 14.04.2019.
 * development with love.
 * © Copyright by Papiertuch
 */

public class PlayerKickEvent extends CustomEvent {

    private String player;
    private String operator;
    private String reason;

    public PlayerKickEvent(String player, String operator, String reason) {
        this.player = player;
        this.operator = operator;
        this.reason = reason;
    }

    @Override
    public BanSystem getBanSystem() {
        return super.getBanSystem();
    }

    public String getReason() {
        return reason;
    }

    public String getOperator() {
        return operator;
    }

    public String getPlayer() {
        return player;
    }

}
