package de.einjava.bansystem.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Leon on 14.04.2019.
 * development with love.
 * © Copyright by Papiertuch
 */

public class BanReduceEvent extends Event {

    private String player;
    private String operator;
    private long duration;
    private HandlerList handlers = new HandlerList();

    public BanReduceEvent(String player, String operator, long duration) {
        this.player = player;
        this.operator = operator;
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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
