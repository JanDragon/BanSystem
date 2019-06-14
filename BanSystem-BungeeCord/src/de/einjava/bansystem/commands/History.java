package de.einjava.bansystem.commands;

import de.einjava.bansystem.Data;
import de.einjava.bansystem.utils.BanHandler;
import de.einjava.bansystem.utils.Config;
import de.einjava.bansystem.utils.MuteHandler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Leon on 14.09.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class History extends Command {

    public History() {
        super("History");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!commandSender.hasPermission(Config.getConfiguration().getString("Command.Ban.Permission"))) {
            commandSender.sendMessage(Data.getPerms());
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
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
    }
}
