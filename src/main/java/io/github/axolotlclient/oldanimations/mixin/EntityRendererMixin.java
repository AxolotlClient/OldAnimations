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
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.modules.freelook.Perspective;
import io.github.axolotlclient.oldanimations.OldAnimations;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.ducks.Sneaky;
import io.github.axolotlclient.oldanimations.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

	@Shadow
	@Final
	protected EntityRenderDispatcher dispatcher;

	@ModifyExpressionValue(method = "renderOnFire", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/Entity;y:D"))
	private double axolotlclient$includeEyeHeight$Y(double original, @Local(argsOnly = true) Entity entity) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.flameOffset.get() && PlayerUtil.isSelf(entity)) {
			/* taken from 1.7 */
			/* we must make sure the flame is synced with the interpolated player model position */
			original += (OldAnimationsConfig.instance.thirdPersonSneaking.get() ? ((Sneaky) Minecraft.getInstance().gameRenderer).axolotlclient$getEyeHeight() : entity.getEyeHeight());
		}
		return original;
	}

	@WrapOperation(method = "postRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;renderOnFire(Lnet/minecraft/entity/Entity;DDDF)V"))
	private void axolotlclient$includeEyeHeight$renderFire(EntityRenderer<?> instance, Entity entity, double dx, double dy, double dz, float tickDelta, Operation<Void> original) {
		boolean oldFlameHeight = OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.flameOffset.get() && PlayerUtil.isSelf(entity);
		if (oldFlameHeight) {
			GlStateManager.pushMatrix();
			/* taken from 1.7 */
			/* we must make sure the flame is synced with the interpolated player model position */
			float eyeHeight = (OldAnimationsConfig.instance.thirdPersonSneaking.get() ? ((Sneaky) Minecraft.getInstance().gameRenderer).axolotlclient$getEyeHeight() : entity.getEyeHeight());
			GlStateManager.translatef(0.0F, eyeHeight, 0.0F);
		}
		original.call(instance, entity, dx, dy, dz, tickDelta);
		if (oldFlameHeight) {
			GlStateManager.popMatrix();
		}
	}

	@Inject(method = "renderNameTag(Lnet/minecraft/entity/Entity;Ljava/lang/String;DDDI)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;rotatef(FFFF)V", ordinal = 1))
	private void axolotlclient$reverseNameplateRotation(Entity entity, String string, double d, double e, double f, int i, CallbackInfo ci) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.fixCameraPitch.get() &&
			OldAnimations.AXOLOTLCLIENT && Minecraft.getInstance().options.perspective == Perspective.THIRD_PERSON_FRONT.ordinal()) {
			/* we need to disable the axolotlclient nameplate rotation in order to use our own! */
			GlStateManager.rotatef(dispatcher.cameraPitch * 2, 1.0F, 0.0F, 0.0F);
		}
	}
}
