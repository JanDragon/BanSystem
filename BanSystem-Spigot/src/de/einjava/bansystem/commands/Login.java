package de.einjava.bansystem.commands;

import de.einjava.bansystem.Data;
import de.einjava.bansystem.utils.Config;
import de.einjava.bansystem.utils.NotifyHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Leon on 22.06.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class Login implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = (Player) commandSender;
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
        return false;
    }

    private String getState(Player player, ArrayList<UUID> list) {
        if (list.contains(player.getUniqueId())) {
            return Config.getConfiguration().getString("Message.Enable");
        } else {
            return Config.getConfiguration().getString("Message.Disable");
        }
    }
}
