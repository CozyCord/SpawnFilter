package net.cozystudios.spawnfilter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.HashSet;
import java.util.Set;

public class SpawnValidator {
	private static final int MAX_SEARCH_RADIUS = 128;
	private static Set<Block> blockedBlockSet = new HashSet<>();

	public static void resolveBlockedBlocks() {
		Set<Block> resolved = new HashSet<>();

		for (String blockId : SpawnFilterConfig.getBlockedBlocks()) {
			Identifier id = Identifier.parse(blockId);
			Block block = BuiltInRegistries.BLOCK.getValue(id);
			resolved.add(block);
		}

		for (String tagStr : SpawnFilterConfig.getBlockedTags()) {
			String tagId = tagStr.startsWith("#") ? tagStr.substring(1) : tagStr;
			Identifier id = Identifier.parse(tagId);
			TagKey<Block> tagKey = TagKey.create(Registries.BLOCK, id);
			for (Block block : BuiltInRegistries.BLOCK) {
				if (block.defaultBlockState().is(tagKey)) {
					resolved.add(block);
				}
			}
		}

		blockedBlockSet = resolved;
		SpawnFilterMod.LOGGER.info("Resolved {} blocked blocks (including tag members)", resolved.size());
	}

	public static boolean isBlockSafe(ServerLevel level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();
		return !blockedBlockSet.contains(block);
	}

	public static boolean isValidSpawnPosition(ServerLevel level, BlockPos pos) {
		BlockPos below = pos.below();
		if (!isBlockSafe(level, below)) return false;
		BlockState belowState = level.getBlockState(below);
		if (!belowState.isFaceSturdy(level, below, Direction.UP)) return false;
		if (!level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()) return false;
		if (!level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()) return false;
		return true;
	}

	public static BlockPos findSafeSpawn(ServerLevel level, BlockPos origin) {
		for (int radius = 1; radius <= MAX_SEARCH_RADIUS; radius++) {
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					if (Math.abs(x) != radius && Math.abs(z) != radius) continue;

					int worldX = origin.getX() + x;
					int worldZ = origin.getZ() + z;

					BlockPos surfacePos = level.getHeightmapPos(
							Heightmap.Types.MOTION_BLOCKING,
							new BlockPos(worldX, 0, worldZ)
					);

					if (isValidSpawnPosition(level, surfacePos)) {
						return surfacePos;
					}
				}
			}
		}

		return null;
	}
}
