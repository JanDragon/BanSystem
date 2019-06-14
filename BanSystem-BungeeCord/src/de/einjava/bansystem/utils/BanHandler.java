package de.einjava.bansystem.utils;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.einjava.bansystem.BanSystem;
import de.einjava.bansystem.Data;
import de.einjava.bansystem.event.BanReduceEvent;
import de.einjava.bansystem.event.PlayerBanEvent;
import de.einjava.bansystem.event.PlayerUnBanEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Leon on 23.05.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class BanHandler {

    private String name;

    public BanHandler(String name) {
        this.name = name;
    }

    public boolean hasVPN(String address) {
        final String url = "http://check.getipintel.net/check.php?ip=" + address + "&contact=" + Data.getEmail();
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = bufferedReader.readLine();
            if (line.startsWith("1")) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public void banPlayer(CommandSender sender, String reason) {
        BanHandler banHandler = new BanHandler(name);
        if (BanSystem.getInstance().getUuidFetcher().getUUID(name) == null) {
            sender.sendMessage(Config.getConfiguration().getString("Message.NotExist").replace("%prefix%", Data.getPrefix()));
            return;
        }
        if (banHandler.isBanned()) {
            sender.sendMessage(Config.getConfiguration().getString("Message.Banned").replace("%prefix%", Data.getPrefix()));
            return;
        }
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);
        if (!Config.getConfiguration().getBoolean("Module.CloudNet.enable")) {
            if (player != null) {
                if (player.hasPermission("ban.bypass")) {
                    sender.sendMessage(Data.getPrefix() + " §cDiesen Spieler darfst du nicht bannen!");
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
                    sender.sendMessage(Data.getPrefix() + " §cDu kannst keine Teammitglieder bannen!");
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
        banHandler.createPlayer();
        banHandler.setDuration(seconds);
        banHandler.setReason(getExactReason(reason));
        String banId = getBanID();
        banHandler.setID(Integer.valueOf(banId));
        banHandler.setBanned(true);
        List<String> history = new ArrayList<>();
        if (!banHandler.getHistory().equalsIgnoreCase("Unknown")) {
            for (String string : banHandler.getHistory().split(",")) {
                history.add(string);
            }
        }
        String date = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());
        history.add(getExactReason(reason) + "-" + ChatColor.stripColor(getName(sender)) + "-" + banId + "-" + date + ",");

        banHandler.setHistory(history.toString().replace("[", "").replace("]", ""));

        banHandler.setModerator(ChatColor.stripColor(getName(sender)));
        banHandler.setDate(date);
        if (player != null) {
            for (UUID uuid : Data.notify) {
                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
                if (p.hasPermission(Config.getConfiguration().getString("Command.Notify.Permission"))) {
                    p.sendMessage(Config.getConfiguration().getString("Message.Notify.Ban.First")
                            .replace("%prefix%", Data.getPrefix())
                            .replace("%name%", player.getDisplayName())
                            .replace("%operator%", getName(sender))
                            .replace("%reason%", getExactReason(reason)));
                    p.sendMessage(Config.getConfiguration().getString("Message.Notify.Ban.Second")
                            .replace("%prefix%", Data.getPrefix())
                            .replace("%duration%", getRemainingTime(seconds)));
                }
            }
            banHandler.setIPBanned(true);
            banHandler.setAddress(player.getAddress().getHostString());
            player.disconnect(getBanScreen());
        } else {
            for (UUID uuid : Data.notify) {
                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
                if (p.hasPermission(Config.getConfiguration().getString("Command.Notify.Permission"))) {
                    p.sendMessage(Config.getConfiguration().getString("Message.Notify.Ban.First")
                            .replace("%prefix%", Data.getPrefix())
                            .replace("%name%", name)
                            .replace("%operator%", getName(sender))
                            .replace("%reason%", getExactReason(reason)));
                    p.sendMessage(Config.getConfiguration().getString("Message.Notify.Ban.Second")
                            .replace("%prefix%", Data.getPrefix())
                            .replace("%duration%", getRemainingTime(banHandler.getDuration())));
                }
            }
        }
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(Config.getConfiguration().getString("Message.Notify.Ban.First")
                    .replace("%prefix%", Data.getPrefix())
                    .replace("%name%", name)
                    .replace("%operator%", getName(sender))
                    .replace("%reason%", getExactReason(reason)));
            sender.sendMessage(Config.getConfiguration().getString("Message.Notify.Ban.Second")
                    .replace("%prefix%", Data.getPrefix())
                    .replace("%duration%", getRemainingTime(banHandler.getDuration())));
        }
        ProxyServer.getInstance().getPluginManager().callEvent(new PlayerBanEvent(name, sender.getName(), getExactReason(reason), banHandler.getDuration()));
    }

    public void banPlayer(CommandSender sender, String reason, long duration) {
        BanHandler banHandler = new BanHandler(name);
        if (BanSystem.getInstance().getUuidFetcher().getUUID(name) == null) {
            sender.sendMessage(Config.getConfiguration().getString("Message.NotExist").replace("%prefix%", Data.getPrefix()));
            return;
        }
        if (banHandler.isBanned()) {
            sender.sendMessage(Config.getConfiguration().getString("Message.Banned").replace("%prefix%", Data.getPrefix()));
            return;
        }
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);
        if (!Config.getConfiguration().getBoolean("Module.CloudNet.enable")) {
            if (player != null) {
                if (player.hasPermission("ban.bypass")) {
                    sender.sendMessage(Data.getPrefix() + " §cDiesen Spieler darfst du nicht bannen!");
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
                    sender.sendMessage(Data.getPrefix() + " §cDu kannst keine Teammitglieder bannen!");
                    return;
                }
            }
        }
        long seconds = duration;
        banHandler.createPlayer();
        banHandler.setDuration(seconds);
        banHandler.setReason(reason);
        String banId = getBanID();
        banHandler.setID(Integer.valueOf(banId));
        banHandler.setBanned(true);
        List<String> history = new ArrayList<>();
        if (!banHandler.getHistory().equalsIgnoreCase("Unknown")) {
            for (String string : banHandler.getHistory().split(",")) {
                history.add(string);
            }
        }
        String date = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());
        history.add(reason + "-" + ChatColor.stripColor(getName(sender)) + "-" + banId + "-" + date + ",");

        banHandler.setHistory(history.toString().replace("[", "").replace("]", ""));

        banHandler.setModerator(ChatColor.stripColor(getName(sender)));
        banHandler.setDate(date);
        if (player != null) {
            for (UUID uuid : Data.notify) {
                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
                if (p.hasPermission(Config.getConfiguration().getString("Command.Notify.Permission"))) {
                    p.sendMessage(Config.getConfiguration().getString("Message.Notify.Ban.First")
                            .replace("%prefix%", Data.getPrefix())
                            .replace("%name%", player.getDisplayName())
                            .replace("%operator%", getName(sender))
                            .replace("%reason%", reason));
                    p.sendMessage(Config.getConfiguration().getString("Message.Notify.Ban.Second")
                            .replace("%prefix%", Data.getPrefix())
                            .replace("%duration%", getRemainingTime(seconds)));
                }
            }
            banHandler.setIPBanned(true);
            banHandler.setAddress(player.getAddress().getHostString());
            player.disconnect(getBanScreen());
        } else {
            for (UUID uuid : Data.notify) {
                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
                if (p.hasPermission(Config.getConfiguration().getString("Command.Notify.Permission"))) {
                    p.sendMessage(Config.getConfiguration().getString("Message.Notify.Ban.First")
                            .replace("%prefix%", Data.getPrefix())
                            .replace("%name%", name)
                            .replace("%operator%", getName(sender))
                            .replace("%reason%", reason));
                    p.sendMessage(Config.getConfiguration().getString("Message.Notify.Ban.Second")
                            .replace("%prefix%", Data.getPrefix())
                            .replace("%duration%", getRemainingTime(banHandler.getDuration())));
                }
            }
        }
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(Config.getConfiguration().getString("Message.Notify.Ban.First")
                    .replace("%prefix%", Data.getPrefix())
                    .replace("%name%", name)
                    .replace("%operator%", getName(sender))
                    .replace("%reason%", reason));
            sender.sendMessage(Config.getConfiguration().getString("Message.Notify.Ban.Second")
                    .replace("%prefix%", Data.getPrefix())
                    .replace("%duration%", getRemainingTime(banHandler.getDuration())));
        }
        ProxyServer.getInstance().getPluginManager().callEvent(new PlayerBanEvent(name, sender.getName(), reason, banHandler.getDuration()));
    }


    public String getName(CommandSender sender) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            return player.getDisplayName();
        } else {
            return "Console";
        }
    }

    public void unBanPlayer(CommandSender sender) {
        if (BanSystem.getInstance().getUuidFetcher().getUUID(name) == null) {
            sender.sendMessage(Config.getConfiguration().getString("Message.NotExist").replace("%prefix%", Data.getPrefix()));
            return;
        }
        BanHandler banHandler = new BanHandler(name);
        if (!banHandler.isBanned()) {
            sender.sendMessage(Config.getConfiguration().getString("Message.NotBanned").replace("%prefix%", Data.getPrefix()));
            return;
        }
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);
        if (player != null) {
            for (UUID uuid : Data.notify) {
                ProxiedPlayer a = ProxyServer.getInstance().getPlayer(uuid);
                if (a.hasPermission(Config.getConfiguration().getString("Command.Notify.Permission"))) {
                    a.sendMessage(Config.getConfiguration().getString("Message.Notify.Unban.First")
                            .replace("%prefix%", Data.getPrefix())
                            .replace("%name%", player.getDisplayName())
                            .replace("%operator%", getName(sender)));
                }
            }
        } else {
            for (UUID uuid : Data.notify) {
                ProxiedPlayer a = ProxyServer.getInstance().getPlayer(uuid);
                if (a.hasPermission(Config.getConfiguration().getString("Command.Notify.Permission"))) {
                    a.sendMessage(Config.getConfiguration().getString("Message.Notify.Unban.First")
                            .replace("%prefix%", Data.getPrefix())
                            .replace("%name%", name)
                            .replace("%operator%", getName(sender)));
                }
            }
        }
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(Config.getConfiguration().getString("Message.Notify.Unban.First")
                    .replace("%prefix%", Data.getPrefix())
                    .replace("%name%", name)
                    .replace("%operator%", getName(sender)));
        }
        banHandler.setBanned(false);
        banHandler.setIPBanned(false);
        banHandler.setDuration(0);
        banHandler.setReason("Unknown");
        banHandler.setModerator("Unknown");
        banHandler.setDate("0");
        banHandler.setBanInfo("Unknown");
        banHandler.setID(0);
        ProxyServer.getInstance().getPluginManager().callEvent(new PlayerUnBanEvent(name, sender.getName()));
    }

    public void reduceBan(String mod, int days) {
        ProxiedPlayer moderator = ProxyServer.getInstance().getPlayer(mod);
        BanHandler banHandler = new BanHandler(name);
        if (!banHandler.isBanned()) {
            moderator.sendMessage(Config.getConfiguration().getString("Message.NotBanned").replace("%prefix%", Data.getPrefix()));
            return;
        }
        if (banHandler.getBanInfo().contains("Reduziert")) {
            moderator.sendMessage(Config.getConfiguration().getString("Message.BanReduce").replace("%prefix%", Data.getPrefix()));
            return;
        }
        long seconds;
        seconds = getDays(days);
        banHandler.setBanned(true);
        banHandler.setIPBanned(true);
        banHandler.setDuration(seconds);
        banHandler.setBanInfo("Reduziert");
        banHandler.setModerator(mod);
        banHandler.setID(Integer.valueOf(getBanID()));
        String date = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());
        banHandler.setDate(date);
        for (UUID uuid : Data.notify) {
            ProxiedPlayer a = ProxyServer.getInstance().getPlayer(uuid);
            if (a.hasPermission(Config.getConfiguration().getString("Command.Notify.Permission"))) {
                a.sendMessage(Config.getConfiguration().getString("Message.Notify.BanReduce.First")
                        .replace("%prefix%", Data.getPrefix())
                        .replace("%name%", name)
                        .replace("%operator%", moderator.getDisplayName()));
                a.sendMessage(Config.getConfiguration().getString("Message.Notify.BanReduce.Second")
                        .replace("%prefix%", Data.getPrefix())
                        .replace("%duration%", getRemainingTime(banHandler.getDuration())));
            }
        }
        ProxyServer.getInstance().getPluginManager().callEvent(new BanReduceEvent(name, moderator.getName(), seconds));
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

    private long getDays(int days) {
        return (System.currentTimeMillis() + ((TimeUnit.DAYS.toMillis(days))));
    }

    private long getTimeWhereReason(String string) {
        String format = "-";
        long duration = 0;
        long time = 0;
        for (Reason reason : Data.reasons) {
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
        for (Reason reason : Data.reasons) {
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
        for (Reason reason : Data.reasons) {
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
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM ban WHERE NAME = ?");
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
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM ban WHERE NAME = ?");
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

    public String getBannedAccount(String address) {
        try {
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM ban WHERE ADDRESS = ? AND BANNED = ?");
            preparedStatement.setString(1, address);
            preparedStatement.setString(2, "true");
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getString("NAME");
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getDate() {
        try {
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM ban WHERE NAME = ?");
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
            BanSystem.getInstance().getMySQL().update("UPDATE ban SET BANNED= '" + state + "' WHERE NAME= '" + name + "';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIPBanned(boolean state) {
        if (Config.getConfiguration().getBoolean("IpBan.enable")) {
            try {
                BanSystem.getInstance().getMySQL().update("UPDATE ban SET IPBANNED= '" + state + "' WHERE NAME= '" + name + "';");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setAddress(String address) {
        try {
            BanSystem.getInstance().getMySQL().update("UPDATE ban SET ADDRESS= '" + address + "' WHERE NAME= '" + name + "';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isIpBanned(String address) {
        try {
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM ban WHERE ADDRESS = ? AND BANNED = ?");
            preparedStatement.setString(1, address);
            preparedStatement.setString(2, "true");
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                if (rs.getString("IPBANNED").equalsIgnoreCase("true")) {
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

    public String getBanInfo() {
        try {
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM ban WHERE NAME = ?");
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

    public String getModerator() {
        try {
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM ban WHERE NAME = ?");
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

    public Long getDuration() {
        try {
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM ban WHERE NAME = ?");
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
            BanSystem.getInstance().getMySQL().update("UPDATE ban SET NAME= '" + name + "' WHERE UUID= '" + BanSystem.getInstance().getUuidFetcher().getUUID(name) + "';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setID(int id) {
        try {
            BanSystem.getInstance().getMySQL().update("UPDATE ban SET ID= '" + id + "' WHERE NAME= '" + name + "';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBanInfo(String info) {
        try {
            BanSystem.getInstance().getMySQL().update("UPDATE ban SET INFO= '" + info + "' WHERE NAME= '" + name + "';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDate(String date) {
        try {
            BanSystem.getInstance().getMySQL().update("UPDATE ban SET DATE= '" + date + "' WHERE NAME= '" + name + "';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDuration(long time) {
        try {
            BanSystem.getInstance().getMySQL().update("UPDATE ban SET DURATION= '" + time + "' WHERE NAME= '" + name + "';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setModerator(String name) {
        try {
            BanSystem.getInstance().getMySQL().update("UPDATE ban SET USER= '" + name + "' WHERE NAME= '" + this.name + "';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setHistory(String history) {
        try {
            BanSystem.getInstance().getMySQL().update("UPDATE ban SET HISTORY= '" + history + "' WHERE NAME= '" + this.name + "';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setReason(String reason) {
        try {
            BanSystem.getInstance().getMySQL().update("UPDATE ban SET REASON= '" + reason + "' WHERE NAME= '" + name + "';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getHistory() {
        try {
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM ban WHERE NAME = ?");
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

    public String getAddress() {
        try {
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM ban WHERE NAME = ?");
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getString("ADDRESS");
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getID() {
        try {
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM ban WHERE NAME = ?");
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
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM ban WHERE NAME = ?");
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
            BanSystem.getInstance().getMySQL().update("INSERT INTO ban (UUID, NAME, ADDRESS, IPBANNED, BANNED, REASON, INFO, HISTORY, DURATION, DATE, ID, USER) VALUES ('" + BanSystem.getInstance().getUuidFetcher().getUUID(name) + "', '" + name + "', 'Unknown', 'false', 'false', 'Unknown', 'Unknown', 'Unknown' , '0', '0', '0', 'Unknown');");
        }
    }

    public String getIPBanScreen(String connection) {
        BanHandler banHandler = new BanHandler(name);
        StringBuilder stringBuilder = new StringBuilder();
        for (String screen : Data.ipBanScreen) {
            stringBuilder.append(screen + "\n");
        }
        return stringBuilder.toString()
                .replace("%account%", name)
                .replace("%banned-account%", banHandler.getBannedAccount(connection))
                .replace("%address%", connection);
    }

    public String getVPNScreen() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String screen : Data.vpnScreen) {
            stringBuilder.append(screen + "\n");
        }
        return stringBuilder.toString()
                .replace("%reason%", "VPN")
                .replace("%server%", Config.getConfiguration().getString("Message.Server"));
    }


    public String getBanScreen() {
        BanHandler banHandler = new BanHandler(name);
        StringBuilder stringBuilder = new StringBuilder();
        for (String screen : Data.banScreen) {
            stringBuilder.append(screen + "\n");
        }
        return stringBuilder.toString()
                .replace("%duration%", String.valueOf(banHandler.getRemainingTime(banHandler.getDuration())))
                .replace("%id%", String.valueOf(banHandler.getID()))
                .replace("%reason%", banHandler.getReason())
                .replace("%operator%", banHandler.getModerator())
                .replace("%server%", Config.getConfiguration().getString("Message.Server"));
    }
}
