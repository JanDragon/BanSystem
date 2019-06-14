package de.einjava.bansystem.utils;

import de.einjava.bansystem.BanSystem;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Leon on 17.08.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class NotifyHandler {

    private String name;
    private UUID uuid;

    public NotifyHandler(String name, UUID uuid) {
        this.uuid = uuid;
        this.name = name;
    }

    public void setState(int state) {
        try {
            BanSystem.getInstance().getMySQL().update("UPDATE notify SET STATE= '" + state + "' WHERE NAME= '" + name + "';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer getState() {
        try {
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM notify WHERE NAME = ?");
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("STATE");
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private boolean playerExists() {
        try {
            PreparedStatement preparedStatement = BanSystem.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM notify WHERE NAME = ?");
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
            BanSystem.getInstance().getMySQL().update("INSERT INTO notify (UUID, NAME, STATE) VALUES ('" + uuid.toString() + "', '" + name + "', '1');");
        }
    }
}
