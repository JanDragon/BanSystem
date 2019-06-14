package de.einjava.bansystem.commands;

import de.einjava.bansystem.Data;
import de.einjava.bansystem.utils.BanHandler;
import de.einjava.bansystem.utils.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Leon on 24.05.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class Unban implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission(Config.getConfiguration().getString("Command.Unban.Permission"))) {
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
        if (args.length == 1) {
            String name = args[0];
            new BanHandler(name).unBanPlayer(commandSender);
        } else {
            commandSender.sendMessage(Config.getConfiguration().getString("Command.Unban.Syntax").replace("%prefix%", Data.getPrefix()));
        }
        return false;
    }

}
