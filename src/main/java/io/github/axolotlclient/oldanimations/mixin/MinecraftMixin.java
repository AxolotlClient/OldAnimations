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
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.ClientPlayerInteractionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.client.entity.particle.ParticleManager;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.sound.instance.SimpleSoundInstance;
import net.minecraft.client.sound.system.SoundManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.MobType;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.HitResult;
import net.minecraft.world.WorldSettings;
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
	public ClientPlayerInteractionManager interactionManager;

	@Shadow
	public abstract SoundManager getSoundManager();

	@Shadow
	public GameOptions options;

	@Unique
	private String axolotlclient$lastTitle = null;

	@Unique
	private float axolotlclient$miningCooldown;

	@Unique
	private float axolotlclient$miningProgress;

	@Unique
	private BlockPos axolotlclient$target = new BlockPos(-1, -1, -1);

	@Unique
	private boolean axolotlclient$syncMiningProgress;

	@Inject(method = "tickBlockMining", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/living/player/LocalClientPlayerEntity;isUsingItem()Z"))
	private void axolotlclient$useAndMine(CallbackInfo ci, @Local(argsOnly = true) boolean bl) {
		if (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.useAndMine.get()) {
			return;
		}

		/* mimics the conditions used in 1.7/1.8 and plays a fake swing animation when the player is using an item and punching */
		if (attackCooldown <= 0 && bl && player.isUsingItem() && crosshairTarget != null && crosshairTarget.type == HitResult.Type.BLOCK) {
			BlockPos blockPos = crosshairTarget.getPos();
			if (!world.isAir(blockPos)) {
				if (OldAnimationsConfig.instance.useAndMineDestroyVisual.get()) {
					axolotlclient$fakeDestroyBlock(blockPos);
				}
				axolotlclient$fakeSwing();
				if (OldAnimationsConfig.instance.useAndMineParticles.get()) {
					particleManager.addBlockMiningParticles(blockPos, crosshairTarget.face);
				}
			}
		}

		if (OldAnimationsConfig.instance.useAndMineDestroyVisual.get() && !options.attackKey.isPressed() && player.isUsingItem()) {
			/* we need to stop the mining progress if the player stops using and mining */
			axolotlclient$stopMiningBlock();
		}
	}

	@Inject(method = "tickBlockMining", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/living/player/LocalClientPlayerEntity;swingHand()V", shift = At.Shift.AFTER))
	private void axolotlclient$wswsws(CallbackInfo ci, @Local(argsOnly = true) boolean bl) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.useAndMineDestroyVisual.get()) {
			/* if the player just mined or is mining, then sync the mining progress */
			axolotlclient$syncMiningProgress = true;
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
			axolotlclient$fakeSwing();
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

	@Unique
	private void axolotlclient$fakeSwing() {
		int armSwingAnimationEnd = ((LivingEntityAccessor) player).getArmSwingAnimationEnd();
		if ((!player.handSwinging || player.handSwingTicks >= armSwingAnimationEnd / 2 || player.handSwingTicks < 0)) {
			player.handSwingTicks = -1;
			player.handSwinging = true;
		}
	}

	@Unique
	private void axolotlclient$fakeDestroyBlock(BlockPos blockPos) {
		ClientPlayerInteractionManagerAccessor accessor = ((ClientPlayerInteractionManagerAccessor) interactionManager);
		if (axolotlclient$miningCooldown > 0) {
			axolotlclient$miningCooldown--;
			return;
		}
		if (interactionManager.getGameMode() == WorldSettings.GameMode.CREATIVE && world.getWorldBorder().contains(blockPos)) {
			axolotlclient$miningCooldown = 5;
			return;
		}

		if (!blockPos.equals(axolotlclient$target)) {
			/* if the cursor is moved to another block, we need to stop the mining progress */
			axolotlclient$stopMiningBlock();
			axolotlclient$target = blockPos;
		}

		BlockState blockState = world.getBlockState(blockPos);
		Block block = blockState.getBlock();

		if (axolotlclient$syncMiningProgress) {
			/* for a seamless fake mining progress to look believable, we should sync it with the real progress */
			axolotlclient$miningProgress = accessor.getMiningProgress();
			axolotlclient$syncMiningProgress = false;
		}

		axolotlclient$miningProgress = axolotlclient$miningProgress + block.getMiningSpeed(player, player.world, blockPos);

		/* trying to sync the mining sounds :p */
		if (accessor.getMiningSoundTimer() % 4.0F == 0.0F) {
			getSoundManager().play(new SimpleSoundInstance(
				new Identifier(block.sound.getStepSound()),
				(block.sound.getVolume() + 1.0F) / 8.0F,
				block.sound.getPitch() * 0.5F,
				(float) blockPos.getX() + 0.5F,
				(float) blockPos.getY() + 0.5F,
				(float) blockPos.getZ() + 0.5F
			));
		}
		accessor.setMiningSoundTimer(accessor.getMiningSoundTimer() + 1);
		if (axolotlclient$miningProgress >= 1.0F) {
			getSoundManager().play(new SimpleSoundInstance(
				new Identifier(block.sound.getDigSound()),
				(block.sound.getVolume() + 1.0F) / 2.0F,
				block.sound.getPitch() * 0.8F,
				(float)blockPos.getX() + 0.5F,
				(float)blockPos.getY() + 0.5F,
				(float)blockPos.getZ() + 0.5F
			));
			particleManager.addBlockMiningParticles(blockPos, blockState);
			axolotlclient$target = new BlockPos(blockPos.getX(), -1, blockPos.getZ());
			axolotlclient$miningProgress = 0.0F;
			accessor.setMiningSoundTimer(0.0F);
			axolotlclient$miningCooldown = 5;
		}

		world.updateBlockMiningProgress(player.getNetworkId(), axolotlclient$target, (int) (axolotlclient$miningProgress * 10.0F) - 1);
	}

	@Unique
	private void axolotlclient$stopMiningBlock() {
		axolotlclient$miningProgress = 0.0F;
		world.updateBlockMiningProgress(player.getNetworkId(), axolotlclient$target, -1);
	}
}
