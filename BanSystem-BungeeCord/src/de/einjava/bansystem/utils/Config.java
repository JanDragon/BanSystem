package de.einjava.bansystem.utils;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Leon on 24.05.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class Config {

    private static final ConfigurationProvider configurationProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    private static final Path configPath = Paths.get("plugins/BanSystem/config.yml");
    private static Configuration configuration;
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
        try {
            if (!Files.exists(Paths.get("plugins/BanSystem"))) {
                Files.createDirectories(Paths.get("plugins/BanSystem"));
            }
            if (Files.exists(configPath)) {
                try (InputStream inputStream = Files.newInputStream(configPath); InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                    configuration = configurationProvider.load(inputStreamReader);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            configuration = new Configuration();

            configuration.set("MySQL.Host", "localhost");
            configuration.set("MySQL.Database", "database");
            configuration.set("MySQL.User", "user");
            configuration.set("MySQL.Password", "password");

            configuration.set("VPN.email", "YOUREMAIL");
            configuration.set("IpBan.enable", true);

            configuration.set("Module.CloudNet.enable", false);
            List<String> groups = new ArrayList<>();
            groups.add("Admin");
            groups.add("Dev");
            groups.add("Sup");
            configuration.set("Module.CloudNet.Groups", groups);

            configuration.set("Command.Ban.Permission", "system.ban");
            configuration.set("Command.Ban.Syntax", "%prefix% §7Nutze§8: §8/§eban §8<§eSpieler§8> <§eGrund§8>");

            configuration.set("Command.Mute.Permission", "system.mute");
            configuration.set("Command.Mute.Syntax", "%prefix% §7Nutze§8: §8/§emute §8<§eSpieler§8> <§eGrund§8>");

            configuration.set("Command.Check.Permission", "system.check");
            configuration.set("Command.Check.Syntax", "%prefix% §7Nutze§8: §8/§echeck §8<§eSpieler§8> <§eBan/Mute§8>");

            configuration.set("Command.Kick.Permission", "system.kick");
            configuration.set("Command.Kick.Syntax", "%prefix% §7Nutze§8: §8/§ekick §8<§eSpieler§8> <§eGrund§8>");

            configuration.set("Command.Unban.Permission", "system.unban");
            configuration.set("Command.Unban.Syntax", "%prefix% §7Nutze§8: §8/§eunban §8<§eSpieler§8>");

            configuration.set("Command.UnMute.Permission", "system.unmute");
            configuration.set("Command.UnMute.Syntax", "%prefix% §7Nutze§8: §8/§eunmute §8<§eSpieler§8>");

            configuration.set("Command.Notify.Permission", "system.notify");
            configuration.set("Command.Notify.Syntax", "%prefix% §7Nutze§8: §8/§enotify §8<§eon§8/§eoff§8>");

            configuration.set("Command.BanReduce.Permission", "system.reduce");
            configuration.set("Command.BanReduce.Syntax", "%prefix% §7Nutze§8: §8/§eban-reduce §8<§eSpieler§8> §8<§eTage§8>");

            configuration.set("Command.MuteReduce.Permission", "system.reduce");
            configuration.set("Command.MuteReduce.Syntax", "%prefix% §7Nutze§8: §8/§emute-reduce §8<§eSpieler§8> §8<§eTage§8>");

            configuration.set("Message.Prefix", "§8[§6BanSystem§8]");
            configuration.set("Message.Server", "§eDeinServer.de");
            configuration.set("Message.Perms", "%prefix% §cDazu hast du keine Rechte");
            configuration.set("Message.Login", "%prefix% §cDu hast dich nicht eingeloggt!");
            configuration.set("Message.Banned", "%prefix% §cDiser Spieler ist bereits gebannt!");
            configuration.set("Message.NotBanned", "%prefix% §cDieser Spieler ist nicht gebannt!");
            configuration.set("Message.KickedSelf", "%prefix% §cDu kannst dich nicht selbst kicken!");
            configuration.set("Message.NotOnline", "%prefix% §cDer Spieler ist nicht online!");
            configuration.set("Message.NotBanned", "%prefix% §cDer Spieler ist nicht gebannt!");
            configuration.set("Message.NotMuted", "%prefix% §cDer Spieler ist nicht gemutet!");
            configuration.set("Message.BanReduce", "%prefix% §cDer Ban wurde bereits reduziert!");
            configuration.set("Message.MuteReduce", "%prefix% §cDer Mute wurde bereits reduziert!");
            configuration.set("Message.NotExist", "%prefix% §cDieser Spieler ist nicht in der Datenbank!");
            configuration.set("Message.Status", "%prefix% §7Dein Status§8:");
            configuration.set("Message.YourStatus", "%prefix%, §7Ban Nachrichten§8: %status%");
            configuration.set("Message.NotifyEnable", "%prefix% §7Du hast dich §awieder §7eingeloggt!");
            configuration.set("Message.NotifyDisable", "%prefix% §7Du hast dich §cnun §7ausgeloggt!");
            configuration.set("Message.Enable", "§8[§a✔§8]");
            configuration.set("Message.Disable", "§8[§c✖§8]");
            configuration.set("Message.Muted", "%prefix% §cDiser Spieler ist bereits gemutet!");

            configuration.set("Message.Notify.Kick.First", "%prefix% §7%name% §7wurde von §a%operator% §7gekickt");
            configuration.set("Message.Notify.Kick.Second", "%prefix% §7Grund §8» §e%reason%");

            configuration.set("Message.Notify.Ban.First", "%prefix% §7%name% §7wurde von §a%operator% §7gebannt §8┃ §4%reason%");
            configuration.set("Message.Notify.Ban.Second", "%prefix% §7Verbleibende Zeit §8» §e%duration%");

            configuration.set("Message.Notify.Mute.First", "%prefix% §7%name% §7wurde von §a%operator% §7gemutet §8┃ §4%reason%");
            configuration.set("Message.Notify.Mute.Second", "%prefix% §7Verbleibende Zeit §8» §e%duration%");

            configuration.set("Message.Notify.Unban.First", "%prefix% §7%name% §7wurde von §a%operator% §7entbannt!");

            configuration.set("Message.Notify.UnMute.First", "%prefix% §7%name% §7wurde von §a%operator% §7entmutet!");

            configuration.set("Message.Notify.BanReduce.First", "%prefix% §7Der Ban von §7%name% §7wurde von §a%operator% §7reduziert");
            configuration.set("Message.Notify.BanReduce.Second", "%prefix% §7Verbleibende Zeit §8» §e%duration%");

            configuration.set("Message.Notify.MuteReduce.First", "%prefix% §7Der Mute von §7%name% §7wurde von §a%operator% §7reduziert");
            configuration.set("Message.Notify.MuteReduce.Second", "%prefix% §7Verbleibende Zeit §8» §e%duration%");

            configuration.set("Message.Mute.1", "%prefix% §cDu bist gemutet!");
            configuration.set("Message.Mute.2", "%prefix% §7Grund §8» §e%reason%");
            configuration.set("Message.Mute.3", "%prefix% §7Verbleibende Zeit §8» §e%duration%");

            List<String> blocked = new ArrayList<>();
            blocked.add("/msg");
            blocked.add("/tell");
            configuration.set("Mute.Blocked", blocked);

            List<String> reason = new ArrayList<>();
            reason.add("Clientmods, 1, -1");
            reason.add("Verhalten, 2, 2 d");
            reason.add("Werbung, 3, -1");
            reason.add("Sonstiges, 4, 14 d");
            configuration.set("Reason.Ban", reason);

            List<String> mute = new ArrayList<>();
            mute.add("Beleidigung, 1, 2 d");
            mute.add("Sonstiges, 2, 2 d");
            configuration.set("Reason.Mute", mute);

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
            configuration.set("Screen.Ban", list);

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
            configuration.set("Screen.IpBan", list1);

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
            configuration.set("Screen.Kick", list2);

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
            configuration.set("Screen.VPN", list3);

            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(configPath), StandardCharsets.UTF_8)) {
                configurationProvider.save(configuration, outputStreamWriter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Config getConfiguration() {
        return new Config();
    }
}
