package de.einjava.bansystem.commands;

import de.einjava.bansystem.Data;
import de.einjava.bansystem.event.PlayerKickEvent;
import de.einjava.bansystem.utils.Config;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Leon on 26.05.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class Kick implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission(Config.getConfiguration().getString("Command.Kick.Permission"))) {
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
        if (args.length < 2) {
            commandSender.sendMessage(Config.getConfiguration().getString("Command.Kick.Syntax").replace("%prefix%", Data.getPrefix()));
            return true;
        }
        String name = args[0];
        Player target = Bukkit.getPlayer(name);
        if (name.equalsIgnoreCase(ChatColor.stripColor(getName(commandSender)))) {
            commandSender.sendMessage(Config.getConfiguration().getString("Message.KickedSelf").replace("%prefix%", Data.getPrefix()));
            return true;
        }
        Player player = Bukkit.getPlayer(name);
        if (!Config.getConfiguration().getBoolean("Module.CloudNet.enable")) {
            if (player != null) {
                if (player.hasPermission("kick.bypass")) {
                    commandSender.sendMessage(Data.getPrefix() + "§cDiesen Spieler darfst du nicht kicken!");
                    return true;
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
                Player a = Bukkit.getPlayer(uuid);
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
            Bukkit.getPluginManager().callEvent(new PlayerKickEvent(name, commandSender.getName(), reason));
            if (!(commandSender instanceof Player)) {
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
            target.kickPlayer(getKickScreen(getName(commandSender), reason));
        } else {
            commandSender.sendMessage(Config.getConfiguration().getString("Message.NotOnline").replace("%prefix%", Data.getPrefix()));
        }
        return false;
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
        if (sender instanceof Player) {
            Player player = (Player) sender;
            return player.getDisplayName();
        } else {
            return "Console";
        }
    }
}
