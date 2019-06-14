package de.einjava.bansystem.utils;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.einjava.bansystem.BanSystem;
import de.einjava.bansystem.Data;
import de.einjava.bansystem.event.MuteReduceEvent;
import de.einjava.bansystem.event.PlayerMuteEvent;
import de.einjava.bansystem.event.PlayerUnMuteEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Leon on 17.08.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class MuteHandler {

    private String name;

    public MuteHandler(String name) {
        this.name = name;
    }


    public void mute(CommandSender sender, String reason) {
        MuteHandler muteHandler = new MuteHandler(name);
        if (BanSystem.getInstance().getUuidFetcher().getUUID(name) == null) {
            sender.sendMessage(Config.getConfiguration().getString("Message.NotExist").replace("%prefix%", Data.getPrefix()));
            return;
        }
        if (muteHandler.isBanned()) {
            sender.sendMessage(Config.getConfiguration().getString("Message.Muted").replace("%prefix%", Data.getPrefix()));
            return;
        }
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);
        if (!Config.getConfiguration().getBoolean("Module.CloudNet.enable")) {
            if (player != null) {
                if (player.hasPermission("mute.bypass")) {
                    sender.sendMessage(Data.getPrefix() + "§cDiesen Spieler darfst du nicht muten!");
                    return;
                }
            }
        } else {
            OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(name);
            CommandSender commandSender = ProxyServer.getInstance().getConsole();
            if (sender != commandSender) {
                String rank = CloudAPI.getInstance().getPermissionPool().getDefaultGroup().getName();
                if (player != null) {
                    rank = CloudAPI.getInstance().getOnlinePlayer(player.getUniqueId()).getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getName();
                } else if (offlinePlayer != null) {
                    rank = offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getName();
                }
                if (Data.teamGroups.contains(rank)) {
                    sender.sendMessage(Data.getPrefix() + "§cDu kannst keine Teammitglieder muten!");
                    return;
                }
            }
        }
        long seconds;
        if (getTimeWhereReason(reason) == -1) {
            seconds = -1;
        } else {
            seconds = getTimeWhereReason(reason) + System.currentTimeMillis();
        }
        muteHandler.createPlayer();
        muteHandler.setDuration(seconds);
        muteHandler.setReason(getExactReason(reason));
        muteHandler.setID(Integer.valueOf(getBanID()));
        muteHandler.setBanned(true);
        muteHandler.setModerator(ChatColor.stripColor(getName(sender)));
        List<String> history = new ArrayList<>();
        if (!muteHandler.getHistory().equalsIgnoreCase("Unknown")) {
            for (String string : muteHandler.getHistory().split(",")) {
                history.add(string);
            }
        }
        String date = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());
        history.add(getExactReason(reason) + "-" + ChatColor.stripColor(getName(sender)) + "-" + muteHandler.getID() + "-" + date + ",");

        muteHandler.setHistory(history.toString().replace("[", "").replace("]", ""));
        muteHandler.setDate(date);
        if (player != null) {
            for (UUID uuid : Data.notify) {
                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
                if (p.hasPermission(Config.getConfiguration().getString("Command.Notify.Permission"))) {
                    p.sendMessage(Config.getConfiguration().getString("Message.Notify.Mute.First")
                            .replace("%prefix%", Data.getPrefix())
                            .replace("%name%", player.getDisplayName())
                            .replace("%operator%", getName(sender))
                            .replace("%reason%", getExactReason(reason)));
                    p.sendMessage(Config.getConfiguration().getString("Message.Notify.Mute.Second")
                            .replace("%prefix%", Data.getPrefix())
                            .replace("%duration%", getRemainingTime(muteHandler.getDuration())));
                }
            }
        } else {
            for (UUID uuid : Data.notify) {
                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
                if (p.hasPermission(Config.getConfiguration().getString("Command.Notify.Permission"))) {
                    p.sendMessage(Config.getConfiguration().getString("Message.Notify.Mute.First")
                            .replace("%prefix%", Data.getPrefix())
                            .replace("%name%", name)
                            .replace("%operator%", getName(sender))
                            .replace("%reason%", getExactReason(reason)));
                    p.sendMessage(Config.getConfiguration().getString("Message.Notify.Mute.Second")
                            .replace("%prefix%", Data.getPrefix())
                            .replace("%duration%", getRemainingTime(muteHandler.getDuration())));
                }
            }
        }
        ProxyServer.getInstance().getPluginManager().callEvent(new PlayerMuteEvent(name, sender.getName(), getExactReason(reason), muteHandler.getDuration()));
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(Config.getConfiguration().getString("Message.Notify.Mute.First")
                    .replace("%prefix%", Data.getPrefix())
                    .replace("%name%", name)
                    .replace("%operator%", getName(sender))
                    .replace("%reason%", getExactReason(reason)));
            sender.sendMessage(Config.getConfiguration().getString("Message.Notify.Mute.Second")
                    .replace("%prefix%", Data.getPrefix())
                    .replace("%duration%", getRemainingTime(muteHandler.getDuration())));
        }
    }


    public void reduceMute(String mod, int days) {
        ProxiedPlayer moderator = ProxyServer.getInstance().getPlayer(mod);
        MuteHandler muteHandler = new MuteHandler(name);
        if (!muteHandler.isBanned()) {
            moderator.sendMessage(Config.getConfiguration().getString("Message.NotMuted").replace("%prefix%", Data.getPrefix()));
            return;
        }
        if (muteHandler.getBanInfo().contains("Reduziert")) {
            moderator.sendMessage(Config.getConfiguration().getString("Message.MuteReduce").replace("%prefix%", Data.getPrefix()));
            return;
        }
        long seconds;
        seconds = getDays(days);
        muteHandler.setBanned(true);
        muteHandler.setDuration(seconds);
        muteHandler.setBanInfo("Reduziert");
        muteHandler.setModerator(mod);
        muteHandler.setID(Integer.valueOf(getBanID()));
        String date = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());
        muteHandler.setDate(date);
        for (UUID uuid : Data.notify) {
            ProxiedPlayer a = ProxyServer.getInstance().getPlayer(uuid);
            if (a.hasPermission(Config.getConfiguration().getString("Command.Notify.Permission"))) {
                a.sendMessage(Config.getConfiguration().getString("Message.Notify.MuteReduce.First")
                        .replace("%prefix%", Data.getPrefix())
                        .replace("%name%", name)
                        .replace("%operator%", moderator.getDisplayName()));
                a.sendMessage(Config.getConfiguration().getString("Message.Notify.MuteReduce.Second")
                        .replace("%prefix%", Data.getPrefix())
                        .replace("%duration%", getRemainingTime(muteHandler.getDuration())));
            }
        }
        ProxyServer.getInstance().getPluginManager().callEvent(new MuteReduceEvent(name, moderator.getName(), seconds));
    }

    public String getName(CommandSender sender) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            return player.getDisplayName();
        } else {
            return "Console";
        }
    }


    public void unMute(CommandSender sender) {
        if (BanSystem.getInstance().getUuidFetcher().getUUID(name) == null) {
            sender.sendMessage(Config.getConfiguration().getString("Message.NotExist").replace("%prefix%", Data.getPrefix()));
            return;
        }
        MuteHandler muteHandler = new MuteHandler(name);
        if (!muteHandler.isBanned()) {
            sender.sendMessage(Config.getConfiguration().getString("Message.NotMuted").replace("%prefix%", Data.getPrefix()));
            return;
        }
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);
        if (player != null) {
            for (UUID uuid : Data.notify) {
                ProxiedPlayer a = ProxyServer.getInstance().getPlayer(uuid);
                if (a.hasPermission(Config.getConfiguration().getString("Command.Notify.Permission"))) {
                    a.sendMessage(Config.getConfiguration().getString("Message.Notify.UnMute.First")
                            .replace("%prefix%", Data.getPrefix())
                            .replace("%name%", player.getDisplayName())
                            .replace("%operator%", getName(sender)));
                }
            }
        } else {
            for (UUID uuid : Data.notify) {
                ProxiedPlayer a = ProxyServer.getInstance().getPlayer(uuid);
                if (a.hasPermission(Config.getConfiguration().getString("Command.Notify.Permission"))) {
                    a.sendMessage(Config.getConfiguration().getString("Message.Notify.UnMute.First")
                            .replace("%prefix%", Data.getPrefix())
                            .replace("%name%", name)
                            .replace("%operator%", getName(sender)));
                }
            }
        }
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(Config.getConfiguration().getString("Message.Notify.UnMute.First")
                    .replace("%prefix%", Data.getPrefix())
                    .replace("%name%", name)
                    .replace("%operator%", getName(sender)));
        }
        muteHandler.setBanned(false);
        muteHandler.setDuration(0);
        muteHandler.setReason("Unknown");
        muteHandler.setModerator("Unknown");
        muteHandler.setDate("0");
        muteHandler.setID(0);
        muteHandler.setBanInfo("Unknown");
        ProxyServer.getInstance().getPluginManager().callEvent(new PlayerUnMuteEvent(name, sender.getName()));
    }

    private long getDays(int days) {
        /*
        if (format.equalsIgnoreCase("d")) {
            return (System.currentTimeMillis() + ((TimeUnit.DAYS.toMillis(days))));
        } else if (format.equalsIgnoreCase("h")) {
            return (System.currentTimeMillis() + ((TimeUnit.HOURS.toMillis(days))));
        } else if (format.equalsIgnoreCase("m")) {
            return (System.currentTimeMillis() + ((TimeUnit.MINUTES.toMillis(days))));
        } else if (format.equalsIgnoreCase("s")) {
            return (System.currentTimeMillis() + ((TimeUnit.SECONDS.toMillis(days))));
        }
         */
        return (System.currentTimeMillis() + ((TimeUnit.DAYS.toMillis(days))));
    }

    public String getRemainingTime(Long duration) {
        if (duration == -1) {
            return "§cPermanent";
        }
        SimpleDateFormat today = new SimpleDateFormat("dd.MM.yyyy");
        today.format(System.currentTimeMillis());

        SimpleDateFormat future = new SimpleDateFormat("dd.MM.yyyy");
        future.format(duration);

        long time = future.getCalendar().getTimeInMillis() - today.getCalendar().getTimeInMillis();
        int days = (int) (time / (1000 * 60 * 60 * 24));
        int hours = (int) (time / (1000 * 60 * 60) % 24);
        int minutes = (int) (time / (1000 * 60) % 60);

        String day = "Tage";
        if (days == 1) {
            day = "Tag";
        }

        String hour = "Stunden";
        if (hours == 1) {
            hour = "Stunde";
        }

        String minute = "Minuten";
        if (minutes == 1) {
            minute = "Minuten";
        }
        if (minutes < 1 && days == 0 && hours == 0) {
            return "weniger als eine Minute";
        }
        if (hours == 0 && days == 0) {
            return minutes + " " + minute;
        }
        if (days == 0) {
            return hours + " " + hour + " " + minutes + " " + minute;
        }
        return days + " " + day + " " + hours + " " + hour + " " + minutes + " " + minute;
    }

    private String getBanID() {
        String str = "";
        int lastRandom = 0;
        for (int i = 0; i < 4; i++) {
            Random r = new Random();
            int rand = r.nextInt(9);
            while (rand == lastRandom) {
                rand = r.nextInt(9);
            }
            lastRandom = rand;
            str = str + rand;
        }
        return str;
    }

    private long getTimeWhereReason(String string) {
        String format = "-";
        long duration = 0;
        long time = 0;
        for (Reason reason : Data.muteReasons) {
            if (reason.getName().toLowerCase().equalsIgnoreCase(string.toLowerCase())) {
                format = reason.getDuration();
                if (reason.getDuration().equalsIgnoreCase("-1")) {
                    time = -1;
                    break;
                }
                duration = Long.parseLong(format.split(" ")[0]);
                break;
            }
            if (reason.getId().equalsIgnoreCase(string)) {
                format = reason.getDuration();
                if (reason.getDuration().equalsIgnoreCase("-1")) {
                    time = -1;
                    break;
                }
                duration = Long.parseLong(format.split(" ")[0]);
                break;
            }
        }
        if (format.contains("s")) {
            time = duration * 1000;
        } else if (format.contains("m")) {
            time = duration * 1000 * 60;
        } else if (format.contains("h")) {
            time = duration * 1000 * 60 * 60;
        } else if (format.contains("d")) {
            time = duration * 1000 * 60 * 60 * 24;
        }
        return time;
    }

    public String getExactReason(String string) {
        String exact = "";
        for (Reason reason : Data.muteReasons) {
            if (reason.getName().toLowerCase().equalsIgnoreCase(string.toLowerCase())) {
                exact = reason.getName();
                break;
            }
            if (reason.getId().equalsIgnoreCase(string)) {
                exact = reason.getName();
                break;
            }
        }
        return exact;
    }

    public String getExactReasonId(String string) {
        String exact = "";
        for (Reason reason : Data.muteReasons) {
            if (reason.getName().toLowerCase().equalsIgnoreCase(string.toLowerCase())) {
                exact = reason.getId();
                break;
            }
            if (reason.getId().equalsIgnoreCase(string)) {
                exact = reason.getId();
                break;
            }
        }
        return exact;
    }


    public String getReason() {
        try {
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM mute WHERE NAME = ?");
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getString("REASON");
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean isBanned() {
        try {
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM mute WHERE NAME = ?");
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                if (rs.getString("BANNED").equalsIgnoreCase("true")) {
                    return true;
                } else {
                    return false;
                }
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getDate() {
        try {
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM mute WHERE NAME = ?");
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getString("DATE");
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setBanned(boolean state) {
        try {
            BanSystem.getInstance().getMySQL().update("UPDATE mute SET BANNED= '" + state + "' WHERE NAME= '" + name + "';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getModerator() {
        try {
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM mute WHERE NAME = ?");
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getString("USER");
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getBanInfo() {
        try {
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM mute WHERE NAME = ?");
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getString("INFO");
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getHistory() {
        try {
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM mute WHERE NAME = ?");
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getString("HISTORY");
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Long getDuration() {
        try {
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM mute WHERE NAME = ?");
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getLong("DURATION");
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateName() {
        try {
            BanSystem.getInstance().getMySQL().update("UPDATE mute SET NAME= '" + name + "' WHERE UUID= '" + BanSystem.getInstance().getUuidFetcher().getUUID(name) + "';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setID(int id) {
        try {
            BanSystem.getInstance().getMySQL().update("UPDATE mute SET ID= '" + id + "' WHERE NAME= '" + name + "';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDate(String date) {
        try {
            BanSystem.getInstance().getMySQL().update("UPDATE mute SET DATE= '" + date + "' WHERE NAME= '" + name + "';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDuration(long time) {
        try {
            BanSystem.getInstance().getMySQL().update("UPDATE mute SET DURATION= '" + time + "' WHERE NAME= '" + name + "';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setHistory(String time) {
        try {
            BanSystem.getInstance().getMySQL().update("UPDATE mute SET HISTORY= '" + time + "' WHERE NAME= '" + name + "';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setBanInfo(String time) {
        try {
            BanSystem.getInstance().getMySQL().update("UPDATE mute SET INFO= '" + time + "' WHERE NAME= '" + name + "';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setModerator(String name) {
        try {
            BanSystem.getInstance().getMySQL().update("UPDATE mute SET USER= '" + name + "' WHERE NAME= '" + this.name + "';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setReason(String reason) {
        try {
            BanSystem.getInstance().getMySQL().update("UPDATE mute SET REASON= '" + reason + "' WHERE NAME= '" + name + "';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getID() {
        try {
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM mute WHERE NAME = ?");
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("ID");
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean playerExists() {
        try {
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM mute WHERE NAME = ?");
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                if (rs.getString("NAME") != null) {
                    return true;
                }
                return false;
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createPlayer() {
        if (!playerExists()) {
            BanSystem.getInstance().getMySQL().update("INSERT INTO mute (UUID, NAME, BANNED, REASON, HISTORY, DURATION, DATE, ID, USER) VALUES ('" + BanSystem.getInstance().getUuidFetcher().getUUID(name) + "', '" + name + "', 'false', 'Unknown', 'Unknown', '0', '0', '0', 'Unknown');");
        }
    }
}
