package net.cozystudios.safespawn;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SafeSpawnMod implements ModInitializer {
	public static final String MOD_ID = "safespawn";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("SafeSpawn Initialized!");
		SafeSpawnConfig.load();
		SpawnEventHandler.register();
	}
}
