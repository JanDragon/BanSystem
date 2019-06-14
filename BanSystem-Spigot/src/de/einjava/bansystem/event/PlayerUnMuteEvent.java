package de.einjava.bansystem.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Leon on 14.04.2019.
 * development with love.
 * © Copyright by Papiertuch
 */

public class PlayerUnMuteEvent extends Event {

    private String player;
    private String operator;
    private HandlerList handlers = new HandlerList();

    public PlayerUnMuteEvent(String player, String operator) {
        this.player = player;
        this.operator = operator;
    }



    public String getOperator() {
        return operator;
    }

    public String getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
