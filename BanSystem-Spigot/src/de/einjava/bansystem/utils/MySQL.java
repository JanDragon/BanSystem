package de.einjava.bansystem.utils;

import de.einjava.bansystem.Data;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * Created by Leon on 24.05.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class MySQL {

    private Connection connection;

    private void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + Config.getConfiguration().getString("MySQL.Host") + ":3306/" + Config.getConfiguration().getString("MySQL.Database") + "?autoReconnect=true", Config.getConfiguration().getString("MySQL.User"), Config.getConfiguration().getString("MySQL.Password"));
            Bukkit.getConsoleSender().sendMessage(Data.getPrefix() + " §aEine Verbindung zur MySQL war erfolgreich");
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(Data.getPrefix() + " §cDie Verbindung zum MySQL-Server ist fehlgeschlagen, da deine Daten leider falsch sind");
            e.printStackTrace();
        }
    }

    public void createTable() {
        connect();
        update("CREATE TABLE IF NOT EXISTS ban (UUID VARCHAR(64), NAME VARCHAR(64), ADDRESS VARCHAR(64), IPBANNED VARCHAR(64), BANNED VARCHAR(64), REASON VARCHAR(64), INFO VARCHAR(64), HISTORY TEXT(20000), DURATION LONG, DATE VARCHAR(64), ID INT, USER VARCHAR(64));");
        update("CREATE TABLE IF NOT EXISTS mute (UUID VARCHAR(64), NAME VARCHAR(64), BANNED VARCHAR(64), REASON VARCHAR(64), INFO VARCHAR(64), HISTORY TEXT(20000), DURATION LONG, DATE VARCHAR(64), ID INT, USER VARCHAR(64));");
        update("CREATE TABLE IF NOT EXISTS notify (UUID VARCHAR(64), NAME VARCHAR(64), STATE INT);");
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (Exception e) {
            }
        }
    }

    private boolean isConnected() {
        return connection != null;
    }

    public Connection getConnection() {
        return connection;
    }

    public void update(String qry) {
        if (connection != null) {
            try {
                PreparedStatement ps = connection.prepareStatement(qry);
                ps.executeUpdate();
                ps.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
