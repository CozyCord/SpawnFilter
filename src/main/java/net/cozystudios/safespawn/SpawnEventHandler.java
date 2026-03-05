package net.cozystudios.safespawn;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
//? if >=1.21.11 {
/*import net.minecraft.world.WorldProperties;
*///?}

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
			//? if <1.21 {
			ServerPlayerEntity player = handler.player;
			//?} else {
			/*ServerPlayerEntity player = handler.getPlayer();
			*///?}

			SafeSpawnPlayer spawnData = (SafeSpawnPlayer) player;
			if (spawnData.safespawn_hasSpawned()) {
				return;
			}

			//? if <1.21 {
			ServerWorld world = (ServerWorld) player.getWorld();
			//?} else if <1.21.11 {
			/*ServerWorld world = player.getServerWorld();
			*///?} else {
			/*ServerWorld world = player.getEntityWorld();
			*///?}
			BlockPos playerPos = player.getBlockPos();
			BlockPos belowPos = playerPos.down();

			if (!SpawnValidator.isBlockSafe(world, belowPos)) {
				Block unsafeBlock = world.getBlockState(belowPos).getBlock();
				String blockName = Registries.BLOCK.getId(unsafeBlock).toString();

				BlockPos safePos = SpawnValidator.findSafeSpawn(world, playerPos);

				if (safePos != null) {
					SafeSpawnMod.LOGGER.info("[SafeSpawn] Relocated player spawn from {} at {},{},{} to {},{},{}",
							blockName,
							playerPos.getX(), playerPos.getY(), playerPos.getZ(),
							safePos.getX(), safePos.getY(), safePos.getZ());

					player.requestTeleport(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5);
					//? if <1.21.5 {
					player.setSpawnPoint(world.getRegistryKey(), safePos, 0.0f, true, false);
					//?} else if <1.21.11 {
					/*player.setSpawnPoint(new ServerPlayerEntity.Respawn(world.getRegistryKey(), safePos, 0.0f, true), false);
					*///?} else {
					/*player.setSpawnPoint(new ServerPlayerEntity.Respawn(
							WorldProperties.SpawnPoint.create(world.getRegistryKey(), safePos, 0.0f, 0.0f), true), false);
					*///?}
				} else {
					SafeSpawnMod.LOGGER.warn("[SafeSpawn] Could not find a safe spawn position within search radius. Leaving spawn unchanged.");
				}
			}

			spawnData.safespawn_setHasSpawned(true);
		});
	}
}
