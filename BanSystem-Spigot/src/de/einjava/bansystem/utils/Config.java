package de.einjava.bansystem.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Leon on 24.05.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class Config {

    private static File file = new File("plugins/BanSystem-Spigot", "config.yml");
    private static FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
    private static HashMap<String, String> cache = new HashMap<>();

    public String getString(String string) {
        if (cache.containsKey(string)) {
            return cache.get(string);
        } else {
            String output = configuration.getString(string);
            cache.put(string, output);
            return output;
        }
    }

    public Boolean getBoolean(String string) {
        return configuration.getBoolean(string);
    }

    public List<String> getStringList(String string) {
        return configuration.getStringList(string);
    }


    public static void loadConfig() {
        configuration.options().copyDefaults(true);
        configuration.addDefault("MySQL.Host", "localhost");
        configuration.addDefault("MySQL.Database", "database");
        configuration.addDefault("MySQL.User", "user");
        configuration.addDefault("MySQL.Password", "password");

        configuration.addDefault("VPN.email", "YOUREMAIL");

        configuration.addDefault("Command.Ban.Permission", "system.ban");
        configuration.addDefault("Command.Ban.Syntax", "%prefix% §7Nutze§8: §8/§eban §8<§eSpieler§8> <§eGrund§8>");

        configuration.addDefault("Command.Mute.Permission", "system.mute");
        configuration.addDefault("Command.Mute.Syntax", "%prefix% §7Nutze§8: §8/§emute §8<§eSpieler§8> <§eGrund§8>");

        configuration.addDefault("Command.Check.Permission", "system.check");
        configuration.addDefault("Command.Check.Syntax", "%prefix% §7Nutze§8: §8/§echeck §8<§eSpieler§8> <§eBan/Mute§8>");

        configuration.addDefault("Command.Kick.Permission", "system.kick");
        configuration.addDefault("Command.Kick.Syntax", "%prefix% §7Nutze§8: §8/§ekick §8<§eSpieler§8> <§eGrund§8>");

        configuration.addDefault("Command.Unban.Permission", "system.unban");
        configuration.addDefault("Command.Unban.Syntax", "%prefix% §7Nutze§8: §8/§eunban §8<§eSpieler§8>");

        configuration.addDefault("Command.UnMute.Permission", "system.unmute");
        configuration.addDefault("Command.UnMute.Syntax", "%prefix% §7Nutze§8: §8/§eunmute §8<§eSpieler§8>");

        configuration.addDefault("Command.Notify.Permission", "system.notify");
        configuration.addDefault("Command.Notify.Syntax", "%prefix% §7Nutze§8: §8/§elogin §8<§eon§8/§eoff§8>");

        configuration.addDefault("Command.BanReduce.Permission", "system.reduce");
        configuration.addDefault("Command.BanReduce.Syntax", "%prefix% §7Nutze§8: §8/§eban-reduce §8<§eSpieler§8> §8<§eTage§8>");

        configuration.addDefault("Command.MuteReduce.Permission", "system.reduce");
        configuration.addDefault("Command.MuteReduce.Syntax", "%prefix% §7Nutze§8: §8/§emute-reduce §8<§eSpieler§8> §8<§eTage§8>");

        configuration.addDefault("Message.Prefix", "§8[§6BanSystem§8]");
        configuration.addDefault("Message.Server", "§eDeinServer.de");
        configuration.addDefault("Message.Perms", "%prefix% §cDazu hast du keine Rechte");
        configuration.addDefault("Message.Login", "%prefix% §cDu hast dich nicht eingeloggt!");
        configuration.addDefault("Message.Banned", "%prefix% §cDiser Spieler ist bereits gebannt!");
        configuration.addDefault("Message.NotBanned", "%prefix% §cDieser Spieler ist nicht gebannt!");
        configuration.addDefault("Message.KickedSelf", "%prefix% §cDu kannst dich nicht selbst kicken!");
        configuration.addDefault("Message.NotOnline", "%prefix% §cDer Spieler ist nicht online!");
        configuration.addDefault("Message.NotBanned", "%prefix% §cDer Spieler ist nicht gebannt!");
        configuration.addDefault("Message.NotMuted", "%prefix% §cDer Spieler ist nicht gemutet!");
        configuration.addDefault("Message.BanReduce", "%prefix% §cDer Ban wurde bereits reduziert!");
        configuration.addDefault("Message.MuteReduce", "%prefix% §cDer Mute wurde bereits reduziert!");
        configuration.addDefault("Message.NotExist", "%prefix% §cDieser Spieler ist nicht in der Datenbank!");
        configuration.addDefault("Message.Status", "%prefix% §7Dein Status§8:");
        configuration.addDefault("Message.YourStatus", "%prefix%, §7Ban Nachrichten§8: %status%");
        configuration.addDefault("Message.NotifyEnable", "%prefix% §7Du hast dich §awieder §7eingeloggt!");
        configuration.addDefault("Message.NotifyDisable", "%prefix% §7Du hast dich §cnun §7ausgeloggt!");
        configuration.addDefault("Message.Enable", "§8[§a✔§8]");
        configuration.addDefault("Message.Disable", "§8[§c✖§8]");
        configuration.addDefault("Message.Muted", "%prefix% §cDiser Spieler ist bereits gemutet!");

        configuration.addDefault("Message.Notify.Kick.First", "%prefix% §7%name% §7wurde von §a%operator% §7gekickt");
        configuration.addDefault("Message.Notify.Kick.Second", "%prefix% §7Grund §8» §e%reason%");

        configuration.addDefault("Message.Notify.Ban.First", "%prefix% §7%name% §7wurde von §a%operator% §7gebannt §8┃ §4%reason%");
        configuration.addDefault("Message.Notify.Ban.Second", "%prefix% §7Verbleibende Zeit §8» §e%duration%");

        configuration.addDefault("Message.Notify.Mute.First", "%prefix% §7%name% §7wurde von §a%operator% §7gemutet §8┃ §4%reason%");
        configuration.addDefault("Message.Notify.Mute.Second", "%prefix% §7Verbleibende Zeit §8» §e%duration%");

        configuration.addDefault("Message.Notify.Unban.First", "%prefix% §7%name% §7wurde von §a%operator% §7entbannt!");

        configuration.addDefault("Message.Notify.UnMute.First", "%prefix% §7%name% §7wurde von §a%operator% §7entmutet!");

        configuration.addDefault("Message.Notify.BanReduce.First", "%prefix% §7Der Ban von §7%name% §7wurde von §a%operator% §7reduziert");
        configuration.addDefault("Message.Notify.BanReduce.Second", "%prefix% §7Verbleibende Zeit §8» §e%duration%");

        configuration.addDefault("Message.Notify.MuteReduce.First", "%prefix% §7Der Mute von §7%name% §7wurde von §a%operator% §7reduziert");
        configuration.addDefault("Message.Notify.MuteReduce.Second", "%prefix% §7Verbleibende Zeit §8» §e%duration%");

        configuration.addDefault("Message.Mute.1", "%prefix% §cDu bist gemutet!");
        configuration.addDefault("Message.Mute.2", "%prefix% §7Grund §8» §e%reason%");
        configuration.addDefault("Message.Mute.3", "%prefix% §7Verbleibende Zeit §8» §e%duration%");

            List<String> blocked = new ArrayList<>();
            blocked.add("/msg");
            blocked.add("/tell");
        configuration.addDefault("Mute.Blocked", blocked);

            List<String> reason = new ArrayList<>();
            reason.add("Clientmods, 1, -1");
            reason.add("Verhalten, 2, 2 d");
            reason.add("Werbung, 3, -1");
            reason.add("Sonstiges, 4, 14 d");
        configuration.addDefault("Reason.Ban", reason);

            List<String> mute = new ArrayList<>();
            mute.add("Beleidigung, 1, 2 d");
            mute.add("Sonstiges, 2, 2 d");
        configuration.addDefault("Reason.Mute", mute);

            List<String> list = new ArrayList<>();
            list.add("§8§m---------------------------------------");
            list.add("");
            list.add("§cDu wurdest vom %server% §cNetzwerk gebannt");
            list.add("");
            list.add("§aGrund §8» §e%reason% #%id%");
            list.add("");
            list.add("§aVerbleibende Zeit §8» §7%duration%");
            list.add("");
            list.add("§7Stelle einen Entbannungsantrag im TeamSpeak");
            list.add("");
            list.add("§8§m---------------------------------------");
        configuration.addDefault("Screen.Ban", list);

            List<String> list1 = new ArrayList<>();
            list1.add("§8§m---------------------------------------");
            list1.add("");
            list1.add("§cDeine IP-Adresse wurde gebannt");
            list1.add("");
            list1.add("§aDein Account §8» §7%account%");
            list1.add("§aGebannter Account §8» §7%banned-account%");
            list1.add("§aDeine Adresse §8» §7%address%");
            list1.add("");
            list1.add("§7Stelle einen Entbannungsantrag im TeamSpeak");
            list1.add("");
            list1.add("§8§m---------------------------------------");
        configuration.addDefault("Screen.IpBan", list1);

            List<String> list2 = new ArrayList<>();
            list2.add("§8§m---------------------------------------");
            list2.add("");
            list2.add("§cDu wurdest vom %server% §cNetzwerk gekickt");
            list2.add("");
            list2.add("§aGrund §8» §e%reason%");
            list2.add("");
            list2.add("§aVon §8» §7%operator%");
            list2.add("");
            list2.add("§eBitte achte auf dein Verhalten!");
            list2.add("");
            list2.add("§8§m---------------------------------------");
        configuration.addDefault("Screen.Kick", list2);

            List<String> list3 = new ArrayList<>();
            list3.add("§8§m---------------------------------------");
            list3.add("");
            list3.add("§cDu wurdest vom %server% §cNetzwerk gekickt");
            list3.add("");
            list3.add("§aGrund §8» §e%reason%");
            list3.add("");
            list3.add("§eBitte achte auf dein Verhalten!");
            list3.add("");
            list3.add("§8§m---------------------------------------");
        configuration.addDefault("Screen.VPN", list3);

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
            }
    }

    public static Config getConfiguration() {
        return new Config();
    }
}
