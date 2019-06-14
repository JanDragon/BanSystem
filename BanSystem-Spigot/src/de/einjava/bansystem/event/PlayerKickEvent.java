package de.einjava.bansystem.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Leon on 14.04.2019.
 * development with love.
 * © Copyright by Papiertuch
 */

public class PlayerKickEvent extends Event {

    private String player;
    private String operator;
    private String reason;
    private HandlerList handlers = new HandlerList();

    public PlayerKickEvent(String player, String operator, String reason) {
        this.player = player;
        this.operator = operator;
        this.reason = reason;
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


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
