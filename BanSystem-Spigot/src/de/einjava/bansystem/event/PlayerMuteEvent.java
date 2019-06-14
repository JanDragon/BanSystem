package de.einjava.bansystem.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Leon on 14.04.2019.
 * development with love.
 * © Copyright by Papiertuch
 */

public class PlayerMuteEvent extends Event {

    private String player;
    private String operator;
    private String reason;
    private HandlerList handlers = new HandlerList();
    private long duration;

    public PlayerMuteEvent(String player, String operator, String reason, long duration) {
        this.player = player;
        this.operator = operator;
        this.reason = reason;
        this.duration = duration;
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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
