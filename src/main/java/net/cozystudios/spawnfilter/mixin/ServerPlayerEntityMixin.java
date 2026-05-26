package net.cozystudios.spawnfilter.mixin;

import net.cozystudios.spawnfilter.SpawnFilterPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin implements SpawnFilterPlayer {
	@Unique
	private boolean spawnfilter_hasSpawned = false;

	@Override
	public boolean spawnfilter_hasSpawned() {
		return spawnfilter_hasSpawned;
	}

	@Override
	public void spawnfilter_setHasSpawned(boolean value) {
		this.spawnfilter_hasSpawned = value;
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void spawnfilter_writeNbt(ValueOutput output, CallbackInfo ci) {
		output.putBoolean("spawnfilter:hasSpawned", spawnfilter_hasSpawned);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void spawnfilter_readNbt(ValueInput input, CallbackInfo ci) {
		spawnfilter_hasSpawned = input.getBooleanOr("spawnfilter:hasSpawned", false);
	}
}
