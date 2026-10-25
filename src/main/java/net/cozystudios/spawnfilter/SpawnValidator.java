package net.cozystudios.spawnfilter;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SpawnValidator {
	private static final int BIOME_STEP = 16;
	private static final int FINE_STEP = 2;

	private static Set<Block> blockedBlockSet = new HashSet<>();
	private static Set<Identifier> blockedBiomeIds = new HashSet<>();
	private static List<TagKey<Biome>> blockedBiomeTags = new ArrayList<>();

	public static void resolveBlockedBlocks() {
		Set<Block> resolvedBlocks = new HashSet<>();

		for (String blockId : SpawnFilterConfig.getBlockedBlocks()) {
			//? if <1.21 {
			Identifier id = new Identifier(blockId);
			//?} else {
			/*Identifier id = Identifier.of(blockId);
			*///?}
			resolvedBlocks.add(Registries.BLOCK.get(id));
		}

		for (String tagStr : SpawnFilterConfig.getBlockedTags()) {
			String tagId = tagStr.startsWith("#") ? tagStr.substring(1) : tagStr;
			//? if <1.21 {
			Identifier id = new Identifier(tagId);
			//?} else {
			/*Identifier id = Identifier.of(tagId);
			*///?}
			TagKey<Block> tagKey = TagKey.of(RegistryKeys.BLOCK, id);
			for (Block block : Registries.BLOCK) {
				if (block.getDefaultState().isIn(tagKey)) {
					resolvedBlocks.add(block);
				}
			}
		}

		blockedBlockSet = resolvedBlocks;

		Set<Identifier> resolvedBiomes = new HashSet<>();
		for (String biomeId : SpawnFilterConfig.getBlockedBiomes()) {
			//? if <1.21 {
			Identifier id = new Identifier(biomeId);
			//?} else {
			/*Identifier id = Identifier.of(biomeId);
			*///?}
			resolvedBiomes.add(id);
		}
		blockedBiomeIds = resolvedBiomes;

		List<TagKey<Biome>> resolvedBiomeTags = new ArrayList<>();
		for (String tagStr : SpawnFilterConfig.getBlockedBiomeTags()) {
			String tagId = tagStr.startsWith("#") ? tagStr.substring(1) : tagStr;
			//? if <1.21 {
			Identifier id = new Identifier(tagId);
			//?} else {
			/*Identifier id = Identifier.of(tagId);
			*///?}
			resolvedBiomeTags.add(TagKey.of(RegistryKeys.BIOME, id));
		}
		blockedBiomeTags = resolvedBiomeTags;

		SpawnFilterMod.LOGGER.info(
				"Resolved {} blocked blocks (incl. tag members), {} blocked biomes, {} blocked biome tags",
				blockedBlockSet.size(), blockedBiomeIds.size(), blockedBiomeTags.size());
	}

	public static boolean isBlockSafe(ServerWorld world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		return !blockedBlockSet.contains(block);
	}

	public static boolean isValidSpawnPosition(ServerWorld world, BlockPos pos) {
		BlockPos below = pos.down();
		if (!isBlockSafe(world, below)) return false;
		BlockState belowState = world.getBlockState(below);
		if (!belowState.isSolidBlock(world, below)) return false;
		if (!world.getBlockState(pos).getCollisionShape(world, pos).isEmpty()) return false;
		if (!world.getBlockState(pos.up()).getCollisionShape(world, pos.up()).isEmpty()) return false;
		return true;
	}

	public static boolean isBiomeBlocked(RegistryEntry<Biome> biome) {
		if (!blockedBiomeIds.isEmpty()) {
			Optional<RegistryKey<Biome>> keyOpt = biome.getKey();
			if (keyOpt.isPresent() && blockedBiomeIds.contains(keyOpt.get().getValue())) {
				return true;
			}
		}
		for (TagKey<Biome> tag : blockedBiomeTags) {
			if (biome.isIn(tag)) return true;
		}
		return false;
	}

	public static boolean isBiomeBlockedAt(ServerWorld world, BlockPos pos) {
		return isBiomeBlocked(world.getBiome(pos));
	}

	private static RegistryEntry<Biome> sampleBiome(ServerWorld world,
			BiomeSource biomeSource, MultiNoiseUtil.MultiNoiseSampler sampler,
			int blockX, int blockY, int blockZ) {
		if (biomeSource != null && sampler != null) {
			return biomeSource.getBiome(blockX >> 2, blockY >> 2, blockZ >> 2, sampler);
		}
		return world.getBiome(new BlockPos(blockX, blockY, blockZ));
	}

	public static BlockPos findSafeSpawn(ServerWorld world, BlockPos origin) {
		int maxRadius = Math.max(BIOME_STEP, SpawnFilterConfig.getMaxSearchRadius());

		BiomeSource biomeSource = null;
		MultiNoiseUtil.MultiNoiseSampler sampler = null;
		try {
			biomeSource = world.getChunkManager().getChunkGenerator().getBiomeSource();
			sampler = world.getChunkManager().getNoiseConfig().getMultiNoiseSampler();
		} catch (Throwable t) {
			SpawnFilterMod.LOGGER.debug("[SpawnFilter] biome source unavailable, falling back to chunk-loading biome lookup", t);
		}

		for (int radius = BIOME_STEP; radius <= maxRadius; radius += BIOME_STEP) {
			for (int x = -radius; x <= radius; x += BIOME_STEP) {
				for (int z = -radius; z <= radius; z += BIOME_STEP) {
					if (Math.abs(x) != radius && Math.abs(z) != radius) continue;

					int worldX = origin.getX() + x;
					int worldZ = origin.getZ() + z;

					RegistryEntry<Biome> biome = sampleBiome(world, biomeSource, sampler, worldX, origin.getY(), worldZ);
					if (isBiomeBlocked(biome)) continue;

					int half = BIOME_STEP / 2;
					for (int dx = -half; dx < half; dx += FINE_STEP) {
						for (int dz = -half; dz < half; dz += FINE_STEP) {
							BlockPos surfacePos = world.getTopPosition(
									Heightmap.Type.MOTION_BLOCKING,
									new BlockPos(worldX + dx, 0, worldZ + dz)
							);
							if (isValidSpawnPosition(world, surfacePos)) {
								return surfacePos;
							}
						}
					}
				}
			}
		}

		return null;
	}

	public static BlockPos buildSafetyPlatform(ServerWorld world, BlockPos origin) {
		BlockPos surface = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, origin);

		//? if <1.21 {
		Identifier blockId = new Identifier(SpawnFilterConfig.getSafetyPlatformBlock());
		//?} else {
		/*Identifier blockId = Identifier.of(SpawnFilterConfig.getSafetyPlatformBlock());
		*///?}
		Block platformBlock = Registries.BLOCK.get(blockId);
		if (platformBlock == Blocks.AIR) {
			platformBlock = Blocks.STONE;
		}

		world.setBlockState(surface.down(), platformBlock.getDefaultState());
		world.setBlockState(surface, Blocks.AIR.getDefaultState());
		world.setBlockState(surface.up(), Blocks.AIR.getDefaultState());
		return surface;
	}
}
