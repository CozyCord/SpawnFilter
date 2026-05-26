package net.cozystudios.spawnfilter;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpawnFilterMod implements ModInitializer {
	public static final String MOD_ID = "spawnfilter";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("SpawnFilter Initialized!");
		SpawnFilterConfig.load();
		SpawnEventHandler.register();
	}
}
