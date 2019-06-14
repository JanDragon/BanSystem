package de.einjava.bansystem.listener;

import de.einjava.bansystem.BanSystem;
import de.einjava.bansystem.Data;
import de.einjava.bansystem.utils.BanHandler;
import de.einjava.bansystem.utils.MuteHandler;
import de.einjava.bansystem.utils.NotifyHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by Leon on 24.05.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class LoginListener implements Listener {

    @EventHandler
    public void onLogin(LoginEvent event) {
        String name = event.getConnection().getName();
        String address = event.getConnection().getAddress().getHostString();
        BanHandler banHandler = new BanHandler(name);
        banHandler.createPlayer();
        banHandler.updateName();
        banHandler.setAddress(address);
        if (banHandler.hasVPN(address)) {
            event.setCancelReason(banHandler.getVPNScreen());
            event.setCancelled(true);
            return;
        }
        if (banHandler.isBanned()) {
            if (banHandler.getDuration() == -1) {
                event.setCancelReason(banHandler.getBanScreen());
                event.setCancelled(true);
                return;
            }
            if (banHandler.getDuration() <= System.currentTimeMillis()) {
                banHandler.setBanned(false);
                banHandler.setIPBanned(false);
                banHandler.setDuration(0);
                banHandler.setDate("");
                banHandler.setReason("Unknown");
                return;
            }
            event.setCancelReason(banHandler.getBanScreen());
            event.setCancelled(true);
            return;
        }
        if (banHandler.isIpBanned(address)) {
            event.setCancelReason(banHandler.getIPBanScreen(address));
            event.setCancelled(true);
            return;
        }
        MuteHandler muteHandler = new MuteHandler(name);
        muteHandler.createPlayer();
        muteHandler.updateName();
    }
}
