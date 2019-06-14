package de.einjava.bansystem.commands;

import de.einjava.bansystem.Data;
import de.einjava.bansystem.utils.BanHandler;
import de.einjava.bansystem.utils.Config;
import de.einjava.bansystem.utils.MuteHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Leon on 14.09.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class History implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission(Config.getConfiguration().getString("Command.Ban.Permission"))) {
            commandSender.sendMessage(Data.getPerms());
            return true;
        }
        Player player = (Player) commandSender;
        if (args.length == 2) {
            if (args[1].toLowerCase().equalsIgnoreCase("ban")) {
                BanHandler banHandler = new BanHandler(args[0]);
                if (banHandler.playerExists()) {
                    if (!banHandler.getHistory().equalsIgnoreCase("Unknown")) {
                        player.sendMessage(Data.getPrefix() + " §7BanHistory über §8» §e" + args[0]);
                        for (String string : banHandler.getHistory().split(",")) {
                            player.sendMessage(" ");
                            player.sendMessage("§8» §7Grund §8» §e" + string.split("-")[0].replace("  ", "") + " #" + string.split("-")[2]);
                            player.sendMessage("§8» §7Von §8» §e" + string.split("-")[1]);
                            player.sendMessage("§8» §7Datum §8» §e" + string.split("-")[3]);
                            player.sendMessage(" ");
                        }
                    } else {
                        player.sendMessage(Data.getPrefix() + " §cDieser Spieler wurde noch nie gebannt!");
                    }
                } else {
                    player.sendMessage(Data.getPrefix() + " §cDieser Spieler ist nicht in der Datenbank!");
                }
            } else if (args[1].toLowerCase().equalsIgnoreCase("mute")) {
                MuteHandler banHandler = new MuteHandler(args[0]);
                if (banHandler.playerExists()) {
                    if (!banHandler.getHistory().equalsIgnoreCase("Unknown")) {
                        player.sendMessage(Data.getPrefix() + " §7MuteHistory über §8» §e" + args[0]);
                        for (String string : banHandler.getHistory().split(",")) {
                            player.sendMessage(" ");
                            player.sendMessage("§8» §7Grund §8» §e" + string.split("-")[0].replace("  ", "") + " #" + string.split("-")[2]);
                            player.sendMessage("§8» §7Von §8» §e" + string.split("-")[1]);
                            player.sendMessage("§8» §7Datum §8» §e" + string.split("-")[3]);
                            player.sendMessage(" ");
                        }
                    } else {
                        player.sendMessage(Data.getPrefix() + " §cDieser Spieler wurde noch nie gemutet!");
                    }
                } else {
                    player.sendMessage(Data.getPrefix() + " §cDieser Spieler ist nicht in der Datenbank!");
                }
            } else {
                player.sendMessage(Data.getPrefix() + " §7Nutze§8: /§ehistory §8<§eName§8> <§eBan/Mute§8>");
            }
        } else {
            player.sendMessage(Data.getPrefix() + " §7Nutze§8: /§ehistory §8<§eName§8> <§eBan/Mute§8>");
        }
        return false;
    }
}
