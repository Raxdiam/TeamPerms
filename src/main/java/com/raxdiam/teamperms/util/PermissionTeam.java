package com.raxdiam.teamperms.util;

import java.util.ArrayList;
import java.util.List;

public class PermissionTeam {
    public String name;
    public List<PermissionCommand> commands;
    public int level;

    public PermissionTeam(PermissionManager manager, String name, int level, List<String> commands) {
        this.name = name;
        this.level = level;
        //this.commands = commands;
        this.commands = new ArrayList<>();
        for (String command : commands) {
            this.commands.add(new PermissionCommand(manager, this, command, level));
        }
    }
}
