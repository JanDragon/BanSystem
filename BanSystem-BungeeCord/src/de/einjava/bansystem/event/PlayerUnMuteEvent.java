package de.einjava.bansystem.event;

import de.einjava.bansystem.BanSystem;

/**
 * Created by Leon on 14.04.2019.
 * development with love.
 * © Copyright by Papiertuch
 */

public class PlayerUnMuteEvent extends CustomEvent {

    private String player;
    private String operator;

    public PlayerUnMuteEvent(String player, String operator) {
        this.player = player;
        this.operator = operator;
    }

    @Override
    public BanSystem getBanSystem() {
        return super.getBanSystem();
    }


    public String getOperator() {
        return operator;
    }

    public String getPlayer() {
        return player;
    }
}
