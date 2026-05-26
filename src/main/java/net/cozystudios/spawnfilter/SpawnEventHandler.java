package net.cozystudios.spawnfilter;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.LevelData;

public class SpawnEventHandler {

	public static void register() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			SpawnValidator.resolveBlockedBlocks();
		});

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
			if (success) {
				SpawnValidator.resolveBlockedBlocks();
			}
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer player = handler.getPlayer();

			SpawnFilterPlayer spawnData = (SpawnFilterPlayer) player;
			if (spawnData.spawnfilter_hasSpawned()) {
				return;
			}

			ServerLevel level = player.level();
			BlockPos playerPos = player.blockPosition();
			BlockPos belowPos = playerPos.below();

			if (!SpawnValidator.isBlockSafe(level, belowPos)) {
				Block unsafeBlock = level.getBlockState(belowPos).getBlock();
				String blockName = BuiltInRegistries.BLOCK.getKey(unsafeBlock).toString();

				BlockPos safePos = SpawnValidator.findSafeSpawn(level, playerPos);

				if (safePos != null) {
					SpawnFilterMod.LOGGER.info("[SpawnFilter] Relocated player spawn from {} at {},{},{} to {},{},{}",
							blockName,
							playerPos.getX(), playerPos.getY(), playerPos.getZ(),
							safePos.getX(), safePos.getY(), safePos.getZ());

					player.teleportTo(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5);
					player.setRespawnPosition(new ServerPlayer.RespawnConfig(
							LevelData.RespawnData.of(level.dimension(), safePos, 0.0f, 0.0f), true), false);
				} else {
					SpawnFilterMod.LOGGER.warn("[SpawnFilter] Could not find a safe spawn position within search radius. Leaving spawn unchanged.");
				}
			}

			spawnData.spawnfilter_setHasSpawned(true);
		});
	}
}
