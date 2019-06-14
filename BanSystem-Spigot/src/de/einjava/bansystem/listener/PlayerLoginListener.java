package de.einjava.bansystem.listener;

import de.einjava.bansystem.BanSystem;
import de.einjava.bansystem.Data;
import de.einjava.bansystem.utils.BanHandler;
import de.einjava.bansystem.utils.Config;
import de.einjava.bansystem.utils.MuteHandler;
import de.einjava.bansystem.utils.NotifyHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * Created by Leon on 24.05.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class PlayerLoginListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        String name = event.getPlayer().getName();
        String address = event.getAddress().getHostAddress();
        BanHandler banHandler = new BanHandler(name);
        banHandler.createPlayer();
        banHandler.updateName();
        banHandler.setAddress(address);
        if (banHandler.hasVPN(address)) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(banHandler.getVPNScreen());
            return;
        }
        if (banHandler.isBanned()) {
            if (banHandler.getDuration() == -1) {
                event.setKickMessage(banHandler.getBanScreen());
                event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
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
            event.setKickMessage(banHandler.getBanScreen());
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            return;
        }
        if (banHandler.isIpBanned(address)) {
            event.setKickMessage(banHandler.getIPBanScreen(address));
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            return;
        }
        if (event.getPlayer().hasPermission(Config.getConfiguration().getString("Command.Notify.Permission"))) {
            NotifyHandler notifyHandler = new NotifyHandler(name, event.getPlayer().getUniqueId());
            notifyHandler.createPlayer();
            Bukkit.getScheduler().runTaskLater(BanSystem.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (notifyHandler.getState() == 1) {
                        if (!Data.notify.contains(event.getPlayer().getUniqueId())) {
                            Data.notify.add(event.getPlayer().getUniqueId());
                        }
                    }
                }
            }, 20);
        }
        MuteHandler muteHandler = new MuteHandler(name);
        muteHandler.createPlayer();
        muteHandler.updateName();
    }
}
