package de.einjava.bansystem.commands;

import de.einjava.bansystem.Data;
import de.einjava.bansystem.utils.BanHandler;
import de.einjava.bansystem.utils.Config;
import de.einjava.bansystem.utils.Reason;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;


/**
 * Created by Leon on 24.05.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class Ban extends Command {

    public Ban() {
        super("ban");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!commandSender.hasPermission(Config.getConfiguration().getString("Command.Ban.Permission"))) {
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
            String reason = args[1];
            if (contains(reason)) {
                BanHandler banHandler = new BanHandler(target);
                if (commandSender.hasPermission("banReason." + banHandler.getExactReasonId(reason)) || commandSender.hasPermission("banReason.*")) {
                    banHandler.banPlayer(commandSender, reason);
                } else {
                    commandSender.sendMessage(Data.getPerms());
                }
            } else {
                getReasons(commandSender);
            }
        } else {
            getReasons(commandSender);

        }
    }

    private void getReasons(CommandSender commandSender) {
        commandSender.sendMessage(Data.getPrefix() + " §7Gründe");
        for (Reason reason : Data.reasons) {
            if (commandSender.hasPermission("banReason." + reason.getId()) || commandSender.hasPermission("banReason.*")) {
                commandSender.sendMessage("§8» §e" + reason.getName() + " §8[§e" + reason.getId() + "§8]");
            }
        }
        commandSender.sendMessage(Config.getConfiguration().getString("Command.Ban.Syntax").replace("%prefix%", Data.getPrefix()));
    }

    public static boolean contains(String string) {
        boolean bool = false;
        for (Reason reason : Data.reasons) {
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
