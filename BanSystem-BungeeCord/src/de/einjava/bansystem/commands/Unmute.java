package de.einjava.bansystem.commands;

import de.einjava.bansystem.Data;
import de.einjava.bansystem.utils.BanHandler;
import de.einjava.bansystem.utils.Config;
import de.einjava.bansystem.utils.MuteHandler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Leon on 17.08.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class Unmute extends Command {

    public Unmute() {
        super("Unmute");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!commandSender.hasPermission(Config.getConfiguration().getString("Command.UnMute.Permission"))) {
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
            new MuteHandler(name).unMute(commandSender);
        } else {
            commandSender.sendMessage(Config.getConfiguration().getString("Command.UnMute.Syntax").replace("%prefix%", Data.getPrefix()));
        }
    }
}
