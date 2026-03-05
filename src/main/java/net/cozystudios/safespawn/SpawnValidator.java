package net.cozystudios.safespawn;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

import java.util.HashSet;
import java.util.Set;

public class SpawnValidator {
	private static final int MAX_SEARCH_RADIUS = 128;
	private static Set<Block> blockedBlockSet = new HashSet<>();

	public static void resolveBlockedBlocks() {
		Set<Block> resolved = new HashSet<>();

		for (String blockId : SafeSpawnConfig.getBlockedBlocks()) {
			//? if <1.21 {
			Identifier id = new Identifier(blockId);
			//?} else {
			/*Identifier id = Identifier.of(blockId);
			*///?}
			Block block = Registries.BLOCK.get(id);
			resolved.add(block);
		}

		for (String tagStr : SafeSpawnConfig.getBlockedTags()) {
			String tagId = tagStr.startsWith("#") ? tagStr.substring(1) : tagStr;
			//? if <1.21 {
			Identifier id = new Identifier(tagId);
			//?} else {
			/*Identifier id = Identifier.of(tagId);
			*///?}
			TagKey<Block> tagKey = TagKey.of(RegistryKeys.BLOCK, id);
			for (Block block : Registries.BLOCK) {
				if (block.getDefaultState().isIn(tagKey)) {
					resolved.add(block);
				}
			}
		}

		blockedBlockSet = resolved;
		SafeSpawnMod.LOGGER.info("Resolved {} blocked blocks (including tag members)", resolved.size());
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

	public static BlockPos findSafeSpawn(ServerWorld world, BlockPos origin) {
		for (int radius = 1; radius <= MAX_SEARCH_RADIUS; radius++) {
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					if (Math.abs(x) != radius && Math.abs(z) != radius) continue;

					int worldX = origin.getX() + x;
					int worldZ = origin.getZ() + z;

					BlockPos surfacePos = world.getTopPosition(
							Heightmap.Type.MOTION_BLOCKING,
							new BlockPos(worldX, 0, worldZ)
					);

					if (isValidSpawnPosition(world, surfacePos)) {
						return surfacePos;
					}
				}
			}
		}

		return null;
	}
}
