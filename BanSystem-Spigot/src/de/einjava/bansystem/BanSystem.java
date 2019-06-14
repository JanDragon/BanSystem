package de.einjava.bansystem;

import de.einjava.bansystem.commands.*;
import de.einjava.bansystem.listener.ChatListener;
import de.einjava.bansystem.listener.PlayerLoginListener;
import de.einjava.bansystem.listener.PlayerQuitListener;
import de.einjava.bansystem.utils.Config;
import de.einjava.bansystem.utils.MySQL;
import de.einjava.bansystem.utils.Reason;
import de.einjava.bansystem.utils.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * Created by Leon on 23.05.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class BanSystem extends JavaPlugin {

    private static BanSystem instance;
    private UUIDFetcher uuidFetcher;
    private MySQL mySQL;

    @Override
    public void onEnable() {
        instance = this;
        System.out.print(" ____               _____           _                 ");
        System.out.print("|  _ \\             / ____|         | |                ");
        System.out.print("| |_) | __ _ _ __ | (___  _   _ ___| |_ ___ _ __ ___  ");
        System.out.print("|  _ < / _` | '_ \\ \\___ \\| | | / __| __/ _ \\ '_ ` _ \\ ");
        System.out.print("| |_) | (_| | | | |____) | |_| \\__ \\ ||  __/ | | | | |");
        System.out.print("|____/ \\__,_|_| |_|_____/ \\__, |___/\\__\\___|_| |_| |_|");
        System.out.print("                           __/ |                      ");
        System.out.print("                          |___/                       ");
        System.out.print("                                                       ");
        System.out.print("» BanSystem-Spigot - by Papiertuch");
        System.out.print("» Version: " + Bukkit.getPluginManager().getPlugin("BanSystem-Spigot").getDescription().getVersion());
        System.out.print("» Java: " + System.getProperty("java.version") + " System: " + System.getProperty("os.name"));
        System.out.print("   ");
        Config.loadConfig();
        register();
        Data.setPrefix(Config.getConfiguration().getString("Message.Prefix"));
        Data.setPerms(Config.getConfiguration().getString("Message.Perms").replace("%prefix%", Data.getPrefix()));
        Data.banScreen = Config.getConfiguration().getStringList("Screen.Ban");
        Data.ipBanScreen = Config.getConfiguration().getStringList("Screen.IpBan");
        Data.kickScreen = Config.getConfiguration().getStringList("Screen.Kick");
        Data.commands = Config.getConfiguration().getStringList("Mute.Blocked");
        Data.setEmail(Config.getConfiguration().getString("VPN.email"));
        for (String reason : Config.getConfiguration().getStringList("Reason.Ban")) {
            String[] args = reason.split(", ");
            Data.reasons.add(new Reason(args[0], Integer.valueOf(args[1]), args[2]));
        }
        for (String reason : Config.getConfiguration().getStringList("Reason.Mute")) {
            String[] args = reason.split(", ");
            Data.muteReasons.add(new Reason(args[0], Integer.valueOf(args[1]), args[2]));
        }
        uuidFetcher = new UUIDFetcher();

        mySQL = new MySQL();
        mySQL.createTable();
    }

    @Override
    public void onDisable() {
        if (mySQL.getConnection() != null) {
            mySQL.disconnect();
        }
    }

    private void register() {
        PluginManager pluginManager = getServer().getPluginManager();

        getCommand("ban").setExecutor(new Ban());
        getCommand("mute").setExecutor(new Mute());
        getCommand("unban").setExecutor(new Unban());
        getCommand("check").setExecutor(new Check());
        getCommand("kick").setExecutor(new Kick());
        getCommand("login").setExecutor(new Login());
        getCommand("ban-reduce").setExecutor(new BanReduce());
        getCommand("history").setExecutor(new History());
        getCommand("unmute").setExecutor(new Unmute());
        getCommand("mute-reduce").setExecutor(new MuteReduce());

        pluginManager.registerEvents(new PlayerLoginListener(), this);
        pluginManager.registerEvents(new ChatListener(), this);
        pluginManager.registerEvents(new PlayerQuitListener(), this);
    }


    public MySQL getMySQL() {
        return mySQL;
    }

    public static BanSystem getInstance() {
        return instance;
    }

    public UUIDFetcher getUuidFetcher() {
        return uuidFetcher;
    }
}
