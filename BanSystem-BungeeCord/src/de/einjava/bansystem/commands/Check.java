package de.einjava.bansystem.commands;

import de.einjava.bansystem.Data;
import de.einjava.bansystem.utils.BanHandler;
import de.einjava.bansystem.utils.Config;
import de.einjava.bansystem.utils.MuteHandler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Leon on 25.05.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class Check extends Command {

    public Check() {
        super("check");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!commandSender.hasPermission(Config.getConfiguration().getString("Command.Check.Permission"))) {
            commandSender.sendMessage(Data.getPerms());
            return;
        }
        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) commandSender;
            if (!Data.notify.contains(player.getUniqueId())) {
                commandSender.sendMessage(Config.getConfiguration().getString("Message.Login").replace("%prefix%", Data.getPrefix()));
                return;
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
    }
}
