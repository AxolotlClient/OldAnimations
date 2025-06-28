/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.oldanimations.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.oldanimations.OldAnimations;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.client.entity.particle.ParticleManager;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.MobType;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.HitResult;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, priority = 2050 /* priority needed for custom window title */)
public abstract class MinecraftMixin {

	@Shadow
	public LocalClientPlayerEntity player;

	@Shadow
	public HitResult crosshairTarget;

	@Shadow
	public ParticleManager particleManager;

	@Shadow
	private int attackCooldown;

	@Shadow
	public ClientWorld world;

	@Shadow
	public GameOptions options;

	@Unique
	private String axolotlclient$lastTitle = null;

	@Inject(method = "tickBlockMining", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/living/player/LocalClientPlayerEntity;isUsingItem()Z"))
	private void axolotlclient$useAndMine(CallbackInfo ci, @Local(argsOnly = true) boolean bl) {
		if (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.useAndMine.get()) {
			return;
		}

		final Minecraft minecraft = (Minecraft) (Object) this;

		/* mimics the conditions used in 1.7/1.8 and plays a fake swing animation when the player is using an item and punching */
		if (attackCooldown <= 0 && bl && player.isUsingItem() && crosshairTarget != null && crosshairTarget.type == HitResult.Type.BLOCK) {
			BlockPos blockPos = crosshairTarget.getPos();
			if (!world.isAir(blockPos)) {
				if (OldAnimationsConfig.instance.useAndMineDestroyVisual.get()) {
					PlayerUtil.INSTANCE.fakeDestroyBlock(minecraft, blockPos);
				}
				PlayerUtil.INSTANCE.fakeSwing(player);
				if (OldAnimationsConfig.instance.useAndMineParticles.get()) {
					particleManager.addBlockMiningParticles(blockPos, crosshairTarget.face);
				}
			}
		} else {
			/* we need to stop the mining progress if the player is not using and mining */
			PlayerUtil.INSTANCE.stopFakeMiningBlock(minecraft);
		}

		if (OldAnimationsConfig.instance.useAndMineDestroyVisual.get() && !options.attackKey.isPressed() && player.isUsingItem()) {
			/* we need to stop the mining progress if the player stops mining but continues using */
			PlayerUtil.INSTANCE.stopFakeMiningBlock(minecraft);
		}
	}

	@Inject(method = "tickBlockMining", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/living/player/LocalClientPlayerEntity;swingHand()V", shift = At.Shift.AFTER))
	private void axolotlclient$wswsws(CallbackInfo ci, @Local(argsOnly = true) boolean bl) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.useAndMineDestroyVisual.get()) {
			/* if the player just mined or is mining, then sync the mining progress */
			PlayerUtil.INSTANCE.setSyncMiningProgress(true);
		}
	}

	@ModifyExpressionValue(method = "doUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/ClientPlayerInteractionManager;isMiningBlock()Z"))
	private boolean axolotlclient$allowMiningCancel(boolean original) {
		return (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.allowMiningCancel.get()) && original;
	}

	@Inject(method = "doAttack", at = @At("TAIL"))
	private void axolotlclient$oldSwingVisual(CallbackInfo ci) {
		if (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.oldSwingVisual.get()) {
			return;
		}
		/* mimics the conditions used in 1.7/1.8 and plays a fake swing animation when the player has an attack cooldown */
		if (attackCooldown > 0) {
			PlayerUtil.INSTANCE.fakeSwing(player);
			if (OldAnimationsConfig.instance.oldSwingVisualParticles.get() && crosshairTarget != null) {
				Entity entity = crosshairTarget.entity;
				if (crosshairTarget.type == HitResult.Type.ENTITY && !entity.onPunched(player)) {
					if (player.fallDistance > 0.0F && !player.onGround && !player.isClimbing() && !player.isInWater() && !player.hasStatusEffect(StatusEffect.BLINDNESS) && player.vehicle == null && entity instanceof LivingEntity) {
						particleManager.addEmitter(entity, ParticleType.CRIT);
					}
					float g;
					if (entity instanceof LivingEntity) {
						g = EnchantmentHelper.modifyDamage(player.getStackInHand(), ((LivingEntity) entity).getMobType());
					} else {
						g = EnchantmentHelper.modifyDamage(player.getStackInHand(), MobType.UNDEFINED);
					}
					if (g > 0.0F) {
						particleManager.addEmitter(entity, ParticleType.CRIT_MAGIC);
					}
				}
			}
		}
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void axolotlclient$spoofTitleVersion(CallbackInfo ci) {
		/* nostalgia! */
		if (!OldAnimationsConfig.isEnabled() || Display.getTitle() == null) {
			return;
		}

		String title;
		if (OldAnimationsConfig.instance.show1_7_10.get()) {
			if (OldAnimations.AXOLOTLCLIENT && AxolotlClient.CONFIG.customWindowTitle.get()) {
				title = "AxolotlClient 1.7.10";
			} else {
				/* hell yeah */
				title = "Minecraft 1.7.10";
			}
		} else {
			/* might as well ensure the custom title gets updated even when the show1_7_10 feature is disabled! */
			if (AxolotlClient.CONFIG.customWindowTitle.get()) {
				title = "AxolotlClient 1.8.9";
			} else {
				title = "Minecraft 1.8.9";
			}
		}

		/* :p */
		if (!title.equals(axolotlclient$lastTitle)) {
			Display.setTitle(title);
			axolotlclient$lastTitle = title;
		}
	}
}
