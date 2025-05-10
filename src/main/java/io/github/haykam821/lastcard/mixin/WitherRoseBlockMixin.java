package io.github.haykam821.lastcard.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.haykam821.lastcard.Main;
import net.minecraft.block.WitherRoseBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.GameSpaceManager;
import xyz.nucleoid.stimuli.event.EventResult;

@Mixin(WitherRoseBlock.class)
public class WitherRoseBlockMixin {
	@Redirect(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInvulnerableTo(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;)Z"))
	private boolean modifyWitherRoseInvulnerability(LivingEntity entity, ServerWorld world, DamageSource source) {
		if (entity instanceof ServerPlayerEntity player) {
			GameSpace gameSpace = GameSpaceManager.get().byPlayer(player);
			if (gameSpace != null && gameSpace.getBehavior().testRule(Main.WITHER_ROSE_WITHER_EFFECT) == EventResult.DENY) {
				return true;
			}
		}

		return entity.isInvulnerableTo(world, source);
	}
}
