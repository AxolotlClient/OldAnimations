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

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.Tessellator;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.ducks.Sneaky;
import io.github.axolotlclient.oldanimations.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

	@Shadow
	private PlayerRenderer defaultPlayerRenderer;

	@Shadow
	public float cameraPitch;

	//TODO: This should be merged into AxolotlClient as they already do a translation to fix the nametags which interferes with this
	@Inject(
		method = "prepare",
		at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;cameraYaw:F", ordinal = 0, shift = At.Shift.AFTER),
		slice = @Slice(from = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/options/GameOptions;perspective:I"))
	)
	private void axolotlclient$fixCameraRotation(World world, TextRenderer textRenderer, Entity camera, Entity targetEntity, GameOptions options, float tickDelta, CallbackInfo ci) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.fixCameraPitch.get()) {
			/* camera rotation bug. originated in 1.8 and is fixed in 1.9 */
			cameraPitch *= -1;
		}
	}

	@Inject(method = "getRenderer(Lnet/minecraft/entity/Entity;)Lnet/minecraft/client/render/entity/EntityRenderer;", at = @At("HEAD"), cancellable = true)
	private void axolotlclient$defaultToSteve(Entity entity, CallbackInfoReturnable<PlayerRenderer> cir) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.disableAlexModel.get() && entity instanceof ClientPlayerEntity) {
			/* 1.7 doesn't have Alex skins! */
			cir.setReturnValue(defaultPlayerRenderer);
		}
	}

	@Definition(id = "LivingEntity", type = LivingEntity.class)
	@Expression("? instanceof LivingEntity")
	@ModifyExpressionValue(method = "renderHitbox", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
	private boolean axolotlclient$disableEyeBox(boolean original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.removeHitBoxEyeLines.get()) {
			/* this doesn't exist in 1.7 */
			return false;
		}
		return original;
	}

	@WrapOperation(method = "renderHitbox", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/Tessellator;end()V"))
	private void axolotlclient$cancelDraw(Tessellator instance, Operation<Void> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.removeHitBoxEyeLines.get()) {
			/* this is a neat trick to cancel rendering... although maybe i should remove the unused code at that */
			instance.getBuilder().end();
		} else {
			original.call(instance);
		}
	}

	@ModifyArgs(method = "renderHitbox", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;<init>(DDDDDD)V"))
	private void axolotlclient$oldHitBoxBehavior(Args args, @Local(argsOnly = true) Entity entity) {
		if (OldAnimationsConfig.isEnabled() && PlayerUtil.INSTANCE.isSelf(entity)) {
			/* sneaking compatibility! */
			double eyeHeightOffset = OldAnimationsConfig.instance.thirdPersonSneaking.get() ? ((Sneaky) Minecraft.getInstance().gameRenderer).axolotlclient$getEyeHeight() - 1.62F : 0.0F;
			/* man there were a lot of eyeheight bugs back in the day LOOOL */
			double hitBoxOffset = OldAnimationsConfig.instance.hitboxOffset.get() ? 1.62F : 0.0F;
			args.set(1, (double) args.get(1) + eyeHeightOffset + hitBoxOffset);
			args.set(4, (double) args.get(4) + eyeHeightOffset + hitBoxOffset);
		}
	}
}
