package de.einjava.bansystem.commands;

import de.einjava.bansystem.Data;
import de.einjava.bansystem.utils.BanHandler;
import de.einjava.bansystem.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Leon on 17.08.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class BanReduce implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender.hasPermission(Config.getConfiguration().getString("Command.BanReduce.Permission"))) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                if (!Data.notify.contains(player.getUniqueId())) {
                    commandSender.sendMessage(Config.getConfiguration().getString("Message.Login").replace("%prefix%", Data.getPrefix()));
                    return true;
                }
            }
            if (args.length == 2) {
                String target = args[0];
                Integer days = 14;
                try {
                    days = Integer.valueOf(args[1]);
                } catch (Exception ignored) {
                    commandSender.sendMessage(Config.getConfiguration().getString("Command.BanReduce.Syntax").replace("%prefix%", Data.getPrefix()));
                }
                new BanHandler(target).reduceBan(Bukkit.getPlayer(commandSender.getName()).getName(), days);
            } else {
                commandSender.sendMessage(Config.getConfiguration().getString("Command.BanReduce.Syntax").replace("%prefix%", Data.getPrefix()));
            }
        } else {
            commandSender.sendMessage(Data.getPerms());
        }
        return false;
    }
}

