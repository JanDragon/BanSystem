package de.einjava.bansystem.listener;

import de.einjava.bansystem.BanSystem;
import de.einjava.bansystem.Data;
import de.einjava.bansystem.utils.Config;
import de.einjava.bansystem.utils.NotifyHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by Leon on 24.04.2019.
 * development with love.
 * © Copyright by Papiertuch
 */

public class PostLoginListener implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (player.hasPermission(Config.getConfiguration().getString("Command.Notify.Permission"))) {
            NotifyHandler notifyHandler = new NotifyHandler(player.getName(), player.getUniqueId());
            notifyHandler.createPlayer();
            ProxyServer.getInstance().getScheduler().schedule(BanSystem.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (notifyHandler.getState() == 1) {
                        if (!Data.notify.contains(player.getUniqueId())) {
                            Data.notify.add(player.getUniqueId());
                        }
                    }
                }
            }, 1, TimeUnit.SECONDS);
        }
    }
}
