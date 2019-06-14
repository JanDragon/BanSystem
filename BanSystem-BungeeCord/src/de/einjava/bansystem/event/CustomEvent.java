package de.einjava.bansystem.event;

import de.einjava.bansystem.BanSystem;
import net.md_5.bungee.api.plugin.Event;

/**
 * Created by Leon on 10.04.2019.
 * development with love.
 * © Copyright by Papiertuch
 */

public abstract class CustomEvent extends Event {

    public BanSystem getBanSystem() {
        return BanSystem.getInstance();
    }
}
