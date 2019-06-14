package de.einjava.bansystem.utils;

import de.einjava.bansystem.Data;

/**
 * Created by Leon on 21.06.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class Reason {

    private String name;
    private int id;
    private String duration;

    public Reason(String name, int id, String duration) {
        this.name = name;
        this.id = id;
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }

    public String getId() {
        return String.valueOf(id);
    }

    public String getName() {
        return name;
    }
}
