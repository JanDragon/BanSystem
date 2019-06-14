package de.einjava.bansystem.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Leon on 10.04.2019.
 * development with love.
 * © Copyright by Papiertuch
 */

public class PlayerBanEvent extends Event {

    private String player;
    private String operator;
    private HandlerList handlers = new HandlerList();
    private String reason;
    private long duration;

    public PlayerBanEvent(String player, String operator, String reason, long duration) {
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
