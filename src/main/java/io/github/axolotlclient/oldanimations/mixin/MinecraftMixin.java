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
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.oldanimations.OldAnimations;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.PlayerUtil;
import io.github.axolotlclient.oldanimations.util.ducks.IClientPlayerInteractionManager;
import net.minecraft.client.ClientPlayerInteractionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.client.entity.particle.ParticleManager;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.item.UseAction;
import net.minecraft.util.crash.CrashReportCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.HitResult;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Callable;

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

	@Shadow
	public ClientPlayerInteractionManager interactionManager;

	@Unique
	private String axolotlclient$lastTitle = null;

	@Inject(method = "tickBlockMining", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/living/player/LocalClientPlayerEntity;isUsingItem()Z"))
	private void axolotlclient$useAndMine(CallbackInfo ci, @Local(argsOnly = true) boolean bl) {
		if (!OldAnimationsConfig.isEnabled()) {
			return;
		}

		//TODO: improve this code... it's horrid... it's like spaghetti...

		/* mimics the conditions used in 1.7/1.8 */
		if (OldAnimationsConfig.instance.useAndMine.get() && attackCooldown <= 0 && player.isUsingItem()) {
			if (bl && crosshairTarget != null && crosshairTarget.type == HitResult.Type.BLOCK) {
				BlockPos blockPos = crosshairTarget.getPos();
				if (!world.isAir(blockPos)) {
					/* plays a packet-less swing animation when the player is using an item and punching */
					PlayerUtil.INSTANCE.fakeSwing(player);
					if (OldAnimationsConfig.instance.useAndMineParticles.get()) {
						/* hell yeah */
						particleManager.addBlockMiningParticles(blockPos, crosshairTarget.face);
					}
				}
			}
		}

		/* once again, mimics the conditions used in 1.7/1.8 */
		if (OldAnimationsConfig.instance.miningProgressResetLogic.get() &&
			attackCooldown <= 0 && player.isUsingItem() &&
			(!bl || crosshairTarget == null || crosshairTarget.type != HitResult.Type.BLOCK)) {

			/* in 1.7, if the player is using an item but not actively mining, then the mining progress will be reset. */
			((IClientPlayerInteractionManager) interactionManager).axolotlclient$fakeStopMiningBlock();
		}
	}

	@WrapWithCondition(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/ClientPlayerInteractionManager;startMiningBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z"))
	private boolean axolotlclient$dontUseAndMine(ClientPlayerInteractionManager instance, BlockPos blockPos, Direction direction) {
		/* this is NOT taken from 1.7 */
		/* honestly it's been a pain in the ass trying to get the method below to actually not flag grimac's packet order checks */
		/* so this was my solution. wait for the tickBlockMining method to start mining. */
		/* this actually allows the use item packet to be sent before start mining packet if you spam click both mouse buttons fast enough */
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.allowMiningCancel.get() || !axolotlclient$hasUseAction() || !interactionManager.hasAttackCooldown();
	}

	@ModifyExpressionValue(method = "doUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/ClientPlayerInteractionManager;isMiningBlock()Z"))
	private boolean axolotlclient$allowMiningCancel(boolean original) {
		/* this may flag an anticheat... but so do the other clients, so we should be as safe as them */
		/* MC-70359 massacred my boy */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.allowMiningCancel.get() && axolotlclient$hasUseAction()) {
			return false;
		}
		return original;
	}

	@Inject(method = "doAttack", at = @At("TAIL"))
	private void axolotlclient$oldSwingVisual(CallbackInfo ci) {
		if (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.oldSwingVisual.get()) {
			return;
		}
		/* mimics the conditions used in 1.7/1.8 and plays a fake swing animation when the player has an attack cooldown */
		if (attackCooldown > 0) {
			PlayerUtil.INSTANCE.fakeSwing(player);
			if (OldAnimationsConfig.instance.oldSwingVisualParticles.get() &&
				crosshairTarget != null && crosshairTarget.type == HitResult.Type.ENTITY) {
				PlayerUtil.INSTANCE.fakeAttackEntity(player, crosshairTarget.entity);
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
			if (OldAnimations.AXOLOTLCLIENT && AxolotlClient.config().customWindowTitle.get()) {
				title = "AxolotlClient 1.7.10";
			} else {
				/* hell yeah */
				title = "Minecraft 1.7.10";
			}
		} else {
			/* might as well ensure the custom title gets updated even when the show1_7_10 feature is disabled! */
			if (AxolotlClient.config().customWindowTitle.get()) {
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

	@WrapOperation(method = "populateCrashReport", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/crash/CrashReportCategory;add(Ljava/lang/String;Ljava/util/concurrent/Callable;)V", ordinal = 0))
	private void axolotlclient$spoofCrashVersionAgain(CrashReportCategory instance, String string, Callable<String> callable, Operation<Void> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.show1_7_10.get()) {
			/* we do a little trolling frfr ;) */
			/* ughhh, this injection is kinda poop */
			callable = () -> "1.7.10";
		}
		original.call(instance, string, callable);
	}

	@ModifyExpressionValue(method = "initSnooper", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;gameVersion:Ljava/lang/String;"))
	private String axolotlclient$spoofSnooperVersionAgain(String original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.show1_7_10.get()) {
			return "1.7.10";
		}
		return original;
	}

	@Unique
	private boolean axolotlclient$hasUseAction() {
		/* unironically, sk1er's old animations mod was on to something wtf */
		return player.getMainHandStack() != null &&
			(player.getMainHandStack().getUseAction() != UseAction.NONE ||
				player.getMainHandStack().getItem() instanceof BlockItem);
	}
}
