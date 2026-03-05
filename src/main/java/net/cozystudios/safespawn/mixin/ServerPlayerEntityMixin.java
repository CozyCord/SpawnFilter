package net.cozystudios.safespawn.mixin;

import net.cozystudios.safespawn.SafeSpawnPlayer;
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
public abstract class ServerPlayerEntityMixin implements SafeSpawnPlayer {
	@Unique
	private boolean safespawn_hasSpawned = false;

	@Override
	public boolean safespawn_hasSpawned() {
		return safespawn_hasSpawned;
	}

	@Override
	public void safespawn_setHasSpawned(boolean value) {
		this.safespawn_hasSpawned = value;
	}

	//? if <1.21.5 {
	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	private void safespawn_writeNbt(NbtCompound nbt, CallbackInfo ci) {
		nbt.putBoolean("safespawn:hasSpawned", safespawn_hasSpawned);
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	private void safespawn_readNbt(NbtCompound nbt, CallbackInfo ci) {
		safespawn_hasSpawned = nbt.getBoolean("safespawn:hasSpawned");
	}
	//?} else if <1.21.11 {
	/*@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	private void safespawn_writeNbt(NbtCompound nbt, CallbackInfo ci) {
		nbt.putBoolean("safespawn:hasSpawned", safespawn_hasSpawned);
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	private void safespawn_readNbt(NbtCompound nbt, CallbackInfo ci) {
		safespawn_hasSpawned = nbt.getBoolean("safespawn:hasSpawned").orElse(false);
	}
	*///?} else {
	/*@Inject(method = "writeCustomData", at = @At("TAIL"))
	private void safespawn_writeNbt(WriteView view, CallbackInfo ci) {
		view.putBoolean("safespawn:hasSpawned", safespawn_hasSpawned);
	}

	@Inject(method = "readCustomData", at = @At("TAIL"))
	private void safespawn_readNbt(ReadView view, CallbackInfo ci) {
		safespawn_hasSpawned = view.getBoolean("safespawn:hasSpawned", false);
	}
	*///?}
}
