package net.cozystudios.spawnfilter;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpawnFilterConfig {
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("spawnfilter.toml");

	private static List<String> blockedBlocks = new ArrayList<>();
	private static List<String> blockedTags = new ArrayList<>();
	private static List<String> blockedBiomes = new ArrayList<>();
	private static List<String> blockedBiomeTags = new ArrayList<>();
	private static int maxSearchRadius = 1024;
	private static boolean buildSafetyPlatform = true;
	private static String safetyPlatformBlock = "minecraft:stone";

	public static void load() {
		if (!CONFIG_PATH.toFile().exists()) {
			createDefaultConfig();
		}

		CommentedFileConfig config = CommentedFileConfig.builder(CONFIG_PATH)
				.sync()
				.build();
		config.load();

		blockedBlocks = config.getOrElse("spawnfilter.blocked_blocks", new ArrayList<>(Arrays.asList(
				"minecraft:water", "minecraft:lava", "minecraft:magma_block"
		)));
		blockedTags = config.getOrElse("spawnfilter.blocked_tags", new ArrayList<>(Arrays.asList(
				"#minecraft:fire", "#minecraft:wool", "#c:fluids", "#minecraft:leaves"
		)));
		blockedBiomes = config.getOrElse("spawnfilter.blocked_biomes", new ArrayList<String>());
		blockedBiomeTags = config.getOrElse("spawnfilter.blocked_biome_tags", new ArrayList<>(Arrays.asList(
				"#minecraft:is_ocean", "#minecraft:is_deep_ocean", "#minecraft:is_river"
		)));
		Number radiusNum = config.getOrElse("spawnfilter.max_search_radius", (Number) 1024);
		maxSearchRadius = radiusNum.intValue();
		buildSafetyPlatform = config.getOrElse("spawnfilter.build_safety_platform", true);
		safetyPlatformBlock = config.getOrElse("spawnfilter.safety_platform_block", "minecraft:stone");

		config.close();

		SpawnFilterMod.LOGGER.info(
				"Loaded SpawnFilter config: {} blocked blocks, {} blocked block tags, {} blocked biomes, {} blocked biome tags, radius {}, platform {}",
				blockedBlocks.size(), blockedTags.size(), blockedBiomes.size(), blockedBiomeTags.size(),
				maxSearchRadius, buildSafetyPlatform);
	}

	private static void createDefaultConfig() {
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			String content = "[spawnfilter]\n"
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
					+ "]\n"
					+ "\n"
					+ "# Specific biomes (by id) the player cannot spawn in\n"
					+ "blocked_biomes = []\n"
					+ "\n"
					+ "# Biome tags whose member biomes the player cannot spawn in\n"
					+ "# Defaults block every variant of ocean + rivers so no one lands in open water\n"
					+ "blocked_biome_tags = [\n"
					+ "    \"#minecraft:is_ocean\",\n"
					+ "    \"#minecraft:is_deep_ocean\",\n"
					+ "    \"#minecraft:is_river\"\n"
					+ "]\n"
					+ "\n"
					+ "# How far (in blocks) to spiral outward searching for a safe spawn before giving up\n"
					+ "max_search_radius = 1024\n"
					+ "\n"
					+ "# If true, when no safe spawn is found within max_search_radius, place a tiny\n"
					+ "# platform under the player so they do not spawn in water or lava. If false, the\n"
					+ "# player is left where vanilla put them.\n"
					+ "build_safety_platform = true\n"
					+ "\n"
					+ "# Block used for the last-resort safety platform\n"
					+ "safety_platform_block = \"minecraft:stone\"\n";
			Files.writeString(CONFIG_PATH, content);
		} catch (IOException e) {
			SpawnFilterMod.LOGGER.error("Failed to create default SpawnFilter config", e);
		}
	}

	public static List<String> getBlockedBlocks() {
		return blockedBlocks;
	}

	public static List<String> getBlockedTags() {
		return blockedTags;
	}

	public static List<String> getBlockedBiomes() {
		return blockedBiomes;
	}

	public static List<String> getBlockedBiomeTags() {
		return blockedBiomeTags;
	}

	public static int getMaxSearchRadius() {
		return maxSearchRadius;
	}

	public static boolean getBuildSafetyPlatform() {
		return buildSafetyPlatform;
	}

	public static String getSafetyPlatformBlock() {
		return safetyPlatformBlock;
	}
}
