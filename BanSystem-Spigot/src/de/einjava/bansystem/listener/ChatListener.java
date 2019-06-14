package de.einjava.bansystem.listener;

import de.einjava.bansystem.Data;
import de.einjava.bansystem.utils.Config;
import de.einjava.bansystem.utils.MuteHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by Leon on 17.08.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!event.getMessage().startsWith("/")) {
            MuteHandler muteHandler = new MuteHandler(player.getName());
            if (muteHandler.isBanned()) {
                if (muteHandler.getDuration() == -1) {
                    event.setCancelled(true);
                    sendMuteMessage(muteHandler, player);
                    return;
                }
                if (muteHandler.getDuration() <= System.currentTimeMillis()) {
                    muteHandler.setBanned(false);
                    muteHandler.setDuration(0);
                    muteHandler.setDate("");
                    muteHandler.setReason("Unknown");
                    return;
                }
                event.setCancelled(true);
                sendMuteMessage(muteHandler, player);
                return;
            }
        } else {
            for (String command : Data.commands) {
                if (event.getMessage().toLowerCase().startsWith(command.toLowerCase())) {
                    MuteHandler muteHandler = new MuteHandler(player.getName());
                    if (muteHandler.isBanned()) {
                        if (muteHandler.getDuration() == -1) {
                            event.setCancelled(true);
                            sendMuteMessage(muteHandler, player);
                            return;
                        }
                        if (muteHandler.getDuration() <= System.currentTimeMillis()) {
                            muteHandler.setBanned(false);
                            muteHandler.setDuration(0);
                            muteHandler.setDate("");
                            muteHandler.setReason("Unknown");
                            return;
                        }
                        event.setCancelled(true);
                        sendMuteMessage(muteHandler, player);
                        return;
                    }
                }
            }
        }
    }

    private void sendMuteMessage(MuteHandler muteHandler, Player player) {
        player.sendMessage(Config.getConfiguration().getString("Message.Mute.1")
                .replace("%prefix%", Data.getPrefix())
                .replace("%operator%", muteHandler.getModerator())
                .replace("%reason%", muteHandler.getReason())
                .replace("%duration%", muteHandler.getRemainingTime(muteHandler.getDuration())));
        player.sendMessage(Config.getConfiguration().getString("Message.Mute.2")
                .replace("%prefix%", Data.getPrefix())
                .replace("%operator%", muteHandler.getModerator())
                .replace("%reason%", muteHandler.getReason())
                .replace("%duration%", muteHandler.getRemainingTime(muteHandler.getDuration())));
        player.sendMessage(Config.getConfiguration().getString("Message.Mute.3")
                .replace("%prefix%", Data.getPrefix())
                .replace("%operator%", muteHandler.getModerator())
                .replace("%reason%", muteHandler.getReason())
                .replace("%duration%", muteHandler.getRemainingTime(muteHandler.getDuration())));
    }
}
