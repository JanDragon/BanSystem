package de.einjava.bansystem.commands;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.einjava.bansystem.Data;
import de.einjava.bansystem.event.PlayerKickEvent;
import de.einjava.bansystem.utils.Config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

/**
 * Created by Leon on 26.05.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class Kick extends Command {

    public Kick() {
        super("kick");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!commandSender.hasPermission(Config.getConfiguration().getString("Command.Kick.Permission"))) {
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
        if (args.length < 2) {
            commandSender.sendMessage(Config.getConfiguration().getString("Command.Kick.Syntax").replace("%prefix%", Data.getPrefix()));
            return;
        }
        String name = args[0];
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(name);
        if (name.equalsIgnoreCase(ChatColor.stripColor(getName(commandSender)))) {
            commandSender.sendMessage(Config.getConfiguration().getString("Message.KickedSelf").replace("%prefix%", Data.getPrefix()));
            return;
        }
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);
        if (!Config.getConfiguration().getBoolean("Module.CloudNet.enable")) {
            if (player != null) {
                if (player.hasPermission("kick.bypass")) {
                    commandSender.sendMessage(Data.getPrefix() + "§cDiesen Spieler darfst du nicht kicken!");
                    return;
                }
            }
        } else {
            OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(name);
            CommandSender sender = ProxyServer.getInstance().getConsole();
            if (commandSender != sender) {
                String rank = CloudAPI.getInstance().getPermissionPool().getDefaultGroup().getName();
                if (player != null) {
                    rank = CloudAPI.getInstance().getOnlinePlayer(player.getUniqueId()).getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getName();
                } else if (offlinePlayer != null) {
                    rank = offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getName();
                }
                if (Data.teamGroups.contains(rank)) {
                    commandSender.sendMessage(Data.getPrefix() + "§cDu kannst keine Teammitglieder kicken!");
                    return;
                }
            }
        }
        String message = "";
        int i = 1;
        while (i < args.length) {
            message = String.valueOf(message) + args[i] + " ";
            ++i;
        }
        if (target != null) {
            String reason = message;
            for (UUID uuid : Data.notify) {
                ProxiedPlayer a = ProxyServer.getInstance().getPlayer(uuid);
                a.sendMessage(Config.getConfiguration().getString("Message.Notify.Kick.First")
                        .replace("%prefix%", Data.getPrefix())
                        .replace("%name%", name)
                        .replace("%reason%", reason)
                        .replace("%operator%", getName(commandSender)));

                a.sendMessage(Config.getConfiguration().getString("Message.Notify.Kick.Second")
                        .replace("%prefix%", Data.getPrefix())
                        .replace("%name%", name)
                        .replace("%reason%", reason)
                        .replace("%operator%", getName(commandSender)));
            }
            target.disconnect(getKickScreen(getName(commandSender), reason));
            ProxyServer.getInstance().getPluginManager().callEvent(new PlayerKickEvent(name, commandSender.getName(), reason));
            if (!(commandSender instanceof ProxiedPlayer)) {
                commandSender.sendMessage(Config.getConfiguration().getString("Message.Notify.Kick.First")
                        .replace("%prefix%", Data.getPrefix())
                        .replace("%name%", name)
                        .replace("%reason%", reason)
                        .replace("%operator%", getName(commandSender)));

                commandSender.sendMessage(Config.getConfiguration().getString("Message.Notify.Kick.Second")
                        .replace("%prefix%", Data.getPrefix())
                        .replace("%name%", name)
                        .replace("%reason%", reason)
                        .replace("%operator%", getName(commandSender)));
            }
        } else {
            commandSender.sendMessage(Config.getConfiguration().getString("Message.NotOnline").replace("%prefix%", Data.getPrefix()));
        }
    }

    public static String getKickScreen(String operator, String reason) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String screen : Data.kickScreen) {
            stringBuilder.append(screen + "\n");
        }
        return stringBuilder.toString()
                .replace("%reason%", reason)
                .replace("%server%", Config.getConfiguration().getString("Message.Server"))
                .replace("%operator%", operator);
    }

    public String getName(CommandSender sender) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            return player.getDisplayName();
        } else {
            return "Console";
        }
    }
}
