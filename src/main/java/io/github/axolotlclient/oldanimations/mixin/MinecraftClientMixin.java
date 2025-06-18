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
import io.github.axolotlclient.oldanimations.OldAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.client.entity.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.MobType;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

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

	@Inject(method = "handleBlockMining", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/living/player/LocalClientPlayerEntity;isHoldingItem()Z"))
	private void axolotlclient$useAndMine(CallbackInfo ci, @Local(argsOnly = true) boolean bl) {
		if (!OldAnimations.isEnabled() || !OldAnimations.getInstance().useAndMine.get()) {
			return;
		}
		/* mimics the conditions used in 1.7/1.8 and plays a fake swing animation when the player is using an item and punching */
		if (attackCooldown <= 0 && bl && player.isHoldingItem() && crosshairTarget != null && crosshairTarget.type == HitResult.Type.BLOCK) {
			BlockPos blockPos = crosshairTarget.getPos();
			if (!world.isAir(blockPos)) {
				axolotlclient$fakeSwing();
				if (OldAnimations.getInstance().useAndMineParticles.get()) {
					particleManager.addBlockMiningParticles(blockPos, crosshairTarget.face);
				}
			}
		}
	}

	@ModifyExpressionValue(
		method = "doUse",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/ClientPlayerInteractionManager;isMiningBlock()Z"
		)
	)
	private boolean axolotlclient$allowMiningCancel(boolean original) {
		return (!OldAnimations.isEnabled() || !OldAnimations.getInstance().allowMiningCancel.get()) && original;
	}

	@Inject(method = "doAttack", at = @At("TAIL"))
	private void axolotlclient$oldSwingVisual(CallbackInfo ci) {
		if (!OldAnimations.isEnabled() || !OldAnimations.getInstance().oldSwingVisual.get()) {
			return;
		}
		/* mimics the conditions used in 1.7/1.8 and plays a fake swing animation when the player has an attack cooldown */
		if (attackCooldown > 0) {
			axolotlclient$fakeSwing();
			if (OldAnimations.getInstance().oldSwingVisualParticles.get() && crosshairTarget != null) {
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

	@Unique
	private void axolotlclient$fakeSwing() {
		int armSwingAnimationEnd = ((LivingEntityAccessor) player).getArmSwingAnimationEnd();
		if ((!player.handSwinging || player.handSwingTicks >= armSwingAnimationEnd / 2 || player.handSwingTicks < 0)) {
			player.handSwingTicks = -1;
			player.handSwinging = true;
		}
	}
}
