package com.raxdiam.teamperms.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raxdiam.teamperms.util.TeamMap;
import com.raxdiam.teamperms.TeamPerms;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Config {
    private static final String CONFIG_PATH = "config/" + TeamPerms.MOD_NAME + ".json";
    private static final Config DEFAULT = new Config(true);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public Config() {
    }

    private Config(boolean useDefault) {
        if (useDefault) {
            teamCommands = createDefaultTeamMap();
        }
    }

    public short configVersion = 1;

    public TeamMap teamCommands;

    public void save() {
        save(this);
    }

    public static Config load() {
        File file = new File(CONFIG_PATH);
        if (!file.exists()) save(DEFAULT);

        try (FileReader fr = new FileReader(CONFIG_PATH)) {
            Config config = GSON.fromJson(fr, Config.class);
            if (config.configVersion != DEFAULT.configVersion) {
                file.delete();
                save(config);
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
            File file = new File(CONFIG_PATH);
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter fw = new FileWriter(CONFIG_PATH)) {
            fw.write(GSON.toJson(config));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static TeamMap createDefaultTeamMap() {
        TeamMap map = new TeamMap();
        map.put("Admin", Arrays.asList(
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
        map.put("Moderator", Arrays.asList(
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
        map.put("Default", Arrays.asList(
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
