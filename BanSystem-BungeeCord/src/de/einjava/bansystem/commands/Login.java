package de.einjava.bansystem.commands;

import de.einjava.bansystem.Data;
import de.einjava.bansystem.utils.Config;
import de.einjava.bansystem.utils.NotifyHandler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Leon on 22.06.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class Login extends Command {

    public Login() {
        super("login");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (player.hasPermission(Config.getConfiguration().getString("Command.Notify.Permission"))) {
            if (args.length == 0) {
                player.sendMessage(Config.getConfiguration().getString("Message.Status")
                        .replace("%prefix%", Data.getPrefix()));
                player.sendMessage(Config.getConfiguration().getString("Message.YourStatus")
                        .replace("%prefix%", Data.getPrefix())
                        .replace("%status%", getState(player, Data.notify)));
                player.sendMessage(Config.getConfiguration().getString("Command.Notify.Syntax").replace("%prefix%", Data.getPrefix()));
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("on")) {
                    if (!Data.notify.contains(player.getUniqueId())) {
                        Data.notify.add(player.getUniqueId());
                        new NotifyHandler(player.getName(), player.getUniqueId()).setState(1);
                    }
                    player.sendMessage(Config.getConfiguration().getString("Message.NotifyEnable").replace("%prefix%", Data.getPrefix()));
                }
                if (args[0].equalsIgnoreCase("off")) {
                    if (Data.notify.contains(player.getUniqueId())) {
                        Data.notify.remove(player.getUniqueId());
                        new NotifyHandler(player.getName(), player.getUniqueId()).setState(0);
                    }
                    player.sendMessage(Config.getConfiguration().getString("Message.NotifyDisable").replace("%prefix%", Data.getPrefix()));
                }
            }
        } else {
            player.sendMessage(Data.getPerms());
        }
    }

    private String getState(ProxiedPlayer player, ArrayList<UUID> list) {
        if (list.contains(player.getUniqueId())) {
            return Config.getConfiguration().getString("Message.Enable");
        } else {
            return Config.getConfiguration().getString("Message.Disable");
        }
    }
}
