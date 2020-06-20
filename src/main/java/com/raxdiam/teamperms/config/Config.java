package com.raxdiam.teamperms.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raxdiam.teamperms.TeamPerms;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Config {
    private static final String CONFIG_PATH = "config/" + TeamPerms.MOD_NAME + ".json";
    private static final Config DEFAULT = new Config(true);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public Config() {
    }

    private Config(boolean useDefault) {
        if (useDefault) {
            teamMap = createDefaultTeamMap();
        }
    }

    public short configVersion = 1;

    public LinkedHashMap<String, List<String>> teamMap;

    public void save() {
        save(this);
    }

    public static Config load() {
        var file = new File(CONFIG_PATH);
        if (!file.exists()) save(DEFAULT);

        try (var fr = new FileReader(CONFIG_PATH)) {
            var config = GSON.fromJson(fr, Config.class);
            if (config.configVersion != DEFAULT.configVersion) {
                file.delete();
                return load();
            }
            return config;
        } catch (IOException e) {
            e.printStackTrace();
            return DEFAULT;
        }
    }

    private static void save(Config config) {
        try {
            var file = new File(CONFIG_PATH);
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (var fw = new FileWriter(CONFIG_PATH)) {
            fw.write(GSON.toJson(config));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static LinkedHashMap<String, List<String>> createDefaultTeamMap() {
        var map = new LinkedHashMap<String, List<String>>();
        map.put("Admin", List.of(
                "advancement",
                "attribute",
                "ban-ip",
                "bossbar",
                "clear",
                "clone",
                "data",
                "datapack",
                "debug",
                "defaultgamemode",
                "deop",
                "difficulty",
                "execute",
                "experience",
                "fill",
                "forceload",
                "function",
                "gamerule",
                "give",
                "kill",
                "locate",
                "locatebiome",
                "loot",
                "op",
                "pardon-ip",
                "particle",
                "playsound",
                "recipe",
                "reload",
                "replaceitem",
                "save-all",
                "save-off",
                "save-on",
                "schedule",
                "scoreboard",
                "seed",
                "setblock",
                "setidletimeout",
                "setworldspawn",
                "spawnpoint",
                "spreadplayers",
                "stop",
                "stopsound",
                "summon",
                "tag",
                "team",
                "tellraw",
                "time",
                "title",
                "weather",
                "worldborder",
                "xp"
        ));
        map.put("Moderator", List.of(
                "banlist",
                "ban",
                "effect",
                "enchant",
                "gamemode",
                "kick",
                "list",
                "pardon",
                "spectate",
                "teleport",
                "tell",
                "tp",
                "whitelist"
        ));
        map.put("Default", List.of(
                "help",
                "me",
                "msg",
                "say",
                "teammsg",
                "tm",
                "trigger",
                "w"
        ));
        return map;
    }
}
