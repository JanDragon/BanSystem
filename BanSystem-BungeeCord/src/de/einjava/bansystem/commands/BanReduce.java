package de.einjava.bansystem.commands;

import de.einjava.bansystem.Data;
import de.einjava.bansystem.utils.BanHandler;
import de.einjava.bansystem.utils.Config;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Leon on 17.08.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class BanReduce extends Command {

    public BanReduce() {
        super("ban-reduce");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission(Config.getConfiguration().getString("Command.BanReduce.Permission"))) {
            if (commandSender instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) commandSender;
                if (!Data.notify.contains(player.getUniqueId())) {
                    commandSender.sendMessage(Config.getConfiguration().getString("Message.Login").replace("%prefix%", Data.getPrefix()));
                    return;
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
                new BanHandler(target).reduceBan(ProxyServer.getInstance().getPlayer(commandSender.getName()).getName(), days);
            } else {
                commandSender.sendMessage(Config.getConfiguration().getString("Command.BanReduce.Syntax").replace("%prefix%", Data.getPrefix()));
            }
        } else {
            commandSender.sendMessage(Data.getPerms());
        }
    }
}
