package de.einjava.bansystem.commands;

import de.einjava.bansystem.Data;
import de.einjava.bansystem.utils.BanHandler;
import de.einjava.bansystem.utils.Config;
import de.einjava.bansystem.utils.MuteHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;

/**
 * Created by Leon on 25.05.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class Check implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission(Config.getConfiguration().getString("Command.Check.Permission"))) {
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
            if (args[1].toLowerCase().equalsIgnoreCase("ban")) {
                BanHandler banHandler = new BanHandler(target);
                if (!banHandler.isBanned()) {
                    commandSender.sendMessage(Config.getConfiguration().getString("Message.NotBanned").replace("%prefix%", Data.getPrefix()));
                } else {
                    commandSender.sendMessage(Data.getPrefix() + " §7Spieler §8» §e" + target);
                    commandSender.sendMessage("§8» §7Grund §8» §e" + banHandler.getReason());
                    commandSender.sendMessage("§8» §7Von §8» §e" + banHandler.getModerator());
                    commandSender.sendMessage("§8» §7Datum §8» §e" + banHandler.getDate());
                    long duration = banHandler.getDuration();
                    if (duration == -1) {
                        commandSender.sendMessage("§8» §7Ende §8» §cPermanent");
                    } else {
                        commandSender.sendMessage("§8» §7Ende §8» §e" + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(duration));
                    }
                    commandSender.sendMessage("§8» §7ID §8» §e" + banHandler.getID());
                }
            }
            if (args[1].toLowerCase().equalsIgnoreCase("mute")) {
                MuteHandler muteHandler = new MuteHandler(target);
                if (!muteHandler.isBanned()) {
                    commandSender.sendMessage(Config.getConfiguration().getString("Message.NotMuted").replace("%prefix%", Data.getPrefix()));
                } else {
                    commandSender.sendMessage(Data.getPrefix() + "§7Spieler §8» §e" + target);
                    commandSender.sendMessage("§8» §7Grund §8» §e" + muteHandler.getReason());
                    commandSender.sendMessage("§8» §7Von §8» §e" + muteHandler.getModerator());
                    commandSender.sendMessage("§8» §7Datum §8» §e" + muteHandler.getDate());
                    long duration = muteHandler.getDuration();
                    if (duration == -1) {
                        commandSender.sendMessage("§8» §7Ende §8» §cPermanent");
                    } else {
                        commandSender.sendMessage("§8» §7Ende §8» §e" + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(duration));
                    }
                    commandSender.sendMessage("§8» §7ID §8» §e" + muteHandler.getID());
                }
            }

        } else {
            commandSender.sendMessage(Config.getConfiguration().getString("Command.Check.Syntax").replace("%prefix%", Data.getPrefix()));
        }
        return false;
    }
}
