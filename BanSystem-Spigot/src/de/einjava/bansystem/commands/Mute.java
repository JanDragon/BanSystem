package de.einjava.bansystem.commands;

import de.einjava.bansystem.Data;
import de.einjava.bansystem.utils.Config;
import de.einjava.bansystem.utils.MuteHandler;
import de.einjava.bansystem.utils.Reason;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Leon on 17.08.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class Mute implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission(Config.getConfiguration().getString("Command.Mute.Permission"))) {
            commandSender.sendMessage(Data.getPerms());
            return true;
        }
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!Data.notify.contains(player.getUniqueId())) {
                commandSender.sendMessage(Config.getConfiguration().getString("Message.Login").replace("%prefix%", Data.getPrefix()));
                return true;
            }
        }
        if (args.length == 2) {
            String target = args[0];
            String reason = args[1];
            if (contains(reason)) {
                MuteHandler muteHandler = new MuteHandler(target);
                if (commandSender.hasPermission("muteReason." + muteHandler.getExactReasonId(reason)) || commandSender.hasPermission("muteReason.*")) {
                    muteHandler.mute(commandSender, reason);
                } else {
                    commandSender.sendMessage(Data.getPerms());
                }
            } else {
                getReasons(commandSender);
            }
        } else {
            getReasons(commandSender);

        }
        return false;
    }

    private void getReasons(CommandSender commandSender) {
        commandSender.sendMessage(Data.getPrefix() + " §7Gründe");
        for (Reason reason : Data.muteReasons) {
            if (commandSender.hasPermission("muteReason." + reason.getId()) || commandSender.hasPermission("muteReason.*")) {
                commandSender.sendMessage("§8» §e" + reason.getName() + " §8[§e" + reason.getId() + "§8]");
            }
        }
        commandSender.sendMessage(Config.getConfiguration().getString("Command.Mute.Syntax").replace("%prefix%", Data.getPrefix()));
    }

    public static boolean contains(String string) {
        boolean bool = false;
        for (Reason reason : Data.muteReasons) {
            if (reason.getName().equalsIgnoreCase(string)) {
                bool = true;
                break;
            }
            if (reason.getId().equalsIgnoreCase(string)) {
                bool = true;
                break;
            }
        }
        return bool;
    }
}
