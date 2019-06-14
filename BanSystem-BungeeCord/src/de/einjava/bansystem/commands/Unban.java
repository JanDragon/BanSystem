package de.einjava.bansystem.commands;

import de.einjava.bansystem.Data;
import de.einjava.bansystem.utils.BanHandler;
import de.einjava.bansystem.utils.Config;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Leon on 24.05.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class Unban extends Command {

    public Unban() {
        super("Unban");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!commandSender.hasPermission(Config.getConfiguration().getString("Command.Unban.Permission"))) {
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
        if (args.length == 1) {
            String name = args[0];
            new BanHandler(name).unBanPlayer(commandSender);
        } else {
            commandSender.sendMessage(Config.getConfiguration().getString("Command.Unban.Syntax").replace("%prefix%", Data.getPrefix()));
        }
    }
}
