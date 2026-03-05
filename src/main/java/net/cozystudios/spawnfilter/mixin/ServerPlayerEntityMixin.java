package net.cozystudios.spawnfilter.mixin;

import net.cozystudios.spawnfilter.SpawnFilterPlayer;
//? if <1.21.11 {
import net.minecraft.nbt.NbtCompound;
//?} else {
/*import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
*///?}
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
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

	//? if <1.21.5 {
	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	private void spawnfilter_writeNbt(NbtCompound nbt, CallbackInfo ci) {
		nbt.putBoolean("spawnfilter:hasSpawned", spawnfilter_hasSpawned);
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	private void spawnfilter_readNbt(NbtCompound nbt, CallbackInfo ci) {
		spawnfilter_hasSpawned = nbt.getBoolean("spawnfilter:hasSpawned");
	}
	//?} else if <1.21.11 {
	/*@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	private void spawnfilter_writeNbt(NbtCompound nbt, CallbackInfo ci) {
		nbt.putBoolean("spawnfilter:hasSpawned", spawnfilter_hasSpawned);
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	private void spawnfilter_readNbt(NbtCompound nbt, CallbackInfo ci) {
		spawnfilter_hasSpawned = nbt.getBoolean("spawnfilter:hasSpawned").orElse(false);
	}
	*///?} else {
	/*@Inject(method = "writeCustomData", at = @At("TAIL"))
	private void spawnfilter_writeNbt(WriteView view, CallbackInfo ci) {
		view.putBoolean("spawnfilter:hasSpawned", spawnfilter_hasSpawned);
	}

	@Inject(method = "readCustomData", at = @At("TAIL"))
	private void spawnfilter_readNbt(ReadView view, CallbackInfo ci) {
		spawnfilter_hasSpawned = view.getBoolean("spawnfilter:hasSpawned", false);
	}
	*///?}
}
