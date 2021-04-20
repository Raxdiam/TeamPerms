package com.raxdiam.teamperms;

import com.raxdiam.teamperms.config.Config;
import com.raxdiam.teamperms.util.PermissionManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TeamPerms implements ModInitializer {
	public static final Config CONFIG = Config.load();
	public static final String MOD_NAME = "TeamPerms";

	private static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	public static PermissionManager PERM_MANAGER;

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStart);
	}

	private void onServerStart(MinecraftServer minecraftServer) {
		PERM_MANAGER = new PermissionManager(CONFIG.teamCommands, minecraftServer);
		PERM_MANAGER.apply();
	}
}
