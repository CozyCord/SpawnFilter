package net.cozystudios.safespawn;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SafeSpawnConfig {
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("safespawn.toml");

	private static List<String> blockedBlocks = new ArrayList<>();
	private static List<String> blockedTags = new ArrayList<>();

	public static void load() {
		if (!CONFIG_PATH.toFile().exists()) {
			createDefaultConfig();
		}

		CommentedFileConfig config = CommentedFileConfig.builder(CONFIG_PATH)
				.sync()
				.build();
		config.load();

		blockedBlocks = config.getOrElse("safespawn.blocked_blocks", new ArrayList<>(Arrays.asList(
				"minecraft:water", "minecraft:lava", "minecraft:magma_block"
		)));
		blockedTags = config.getOrElse("safespawn.blocked_tags", new ArrayList<>(Arrays.asList(
				"#minecraft:fire", "#minecraft:wool", "#c:fluids", "#minecraft:leaves"
		)));

		config.close();

		SafeSpawnMod.LOGGER.info("Loaded SafeSpawn config: {} blocked blocks, {} blocked tags",
				blockedBlocks.size(), blockedTags.size());
	}

	private static void createDefaultConfig() {
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			String content = "[safespawn]\n"
					+ "# Individual blocks the player cannot spawn on\n"
					+ "blocked_blocks = [\n"
					+ "    \"minecraft:water\",\n"
					+ "    \"minecraft:lava\",\n"
					+ "    \"minecraft:magma_block\"\n"
					+ "]\n"
					+ "\n"
					+ "# Block tags whose member blocks the player cannot spawn on\n"
					+ "# Use the # prefix convention to denote tags, mirroring Minecraft tag syntax\n"
					+ "blocked_tags = [\n"
					+ "    \"#minecraft:fire\",\n"
					+ "    \"#minecraft:wool\",\n"
					+ "    \"#c:fluids\",\n"
					+ "    \"#minecraft:leaves\"\n"
					+ "]\n";
			Files.writeString(CONFIG_PATH, content);
		} catch (IOException e) {
			SafeSpawnMod.LOGGER.error("Failed to create default SafeSpawn config", e);
		}
	}

	public static List<String> getBlockedBlocks() {
		return blockedBlocks;
	}

	public static List<String> getBlockedTags() {
		return blockedTags;
	}
}
