package de.einjava.bansystem;

import de.einjava.bansystem.utils.Reason;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Leon on 23.05.2018.
 * development with love.
 * © Copyright by Papiertuch
 */

public class Data {

    private static String prefix;
    private static String perms;

    public static ArrayList<UUID> notify = new ArrayList<>();
    public static ArrayList<Reason> reasons = new ArrayList<>();
    public static ArrayList<Reason> muteReasons = new ArrayList<>();
    public static List<String> banScreen = new ArrayList<>();
    public static List<String> ipBanScreen = new ArrayList<>();
    public static List<String> kickScreen = new ArrayList<>();
    public static List<String> vpnScreen = new ArrayList<>();
    public static List<String> teamGroups = new ArrayList<>();
    public static List<String> commands = new ArrayList<>();

    private static String email;

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        Data.email = email;
    }

    public static String getPrefix() {
        return prefix;
    }



    public static String getPerms() {
        return perms;
    }

    public static void setPerms(String perms) {
        Data.perms = perms;
    }


    public static void setPrefix(String prefix) {
        Data.prefix = prefix;
    }
}
