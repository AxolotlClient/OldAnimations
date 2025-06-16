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
import io.github.axolotlclient.oldanimations.OldAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

	//TODO: Sync with smooth sneaking like in 1.7

	@ModifyExpressionValue(method = "renderOnFire", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/Entity;y:D"))
	private double axolotlclient$includeEyeHeight$Y(double original, @Local(argsOnly = true) Entity entity) {
		if (OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().flameOffset.get() && isSelf(entity)) {
			/* taken from 1.7 */
			original += entity.getEyeHeight();
		}
		return original;
	}

	@WrapOperation(method = "postRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;renderOnFire(Lnet/minecraft/entity/Entity;DDDF)V"))
	private void axolotlclient$includeEyeHeight$renderFire(EntityRenderer<?> instance, Entity entity, double dx, double dy, double dz, float tickDelta, Operation<Void> original) {
		boolean oldFlameHeight = OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().flameOffset.get() && isSelf(entity);
		if (oldFlameHeight) {
			GlStateManager.pushMatrix();
			/* taken from 1.7 */
			GlStateManager.translatef(0.0F, entity.getEyeHeight(), 0.0F);
		}
		original.call(instance, entity, dx, dy, dz, tickDelta);
		if (oldFlameHeight) {
			GlStateManager.popMatrix();
		}
	}

	@Unique
	private boolean isSelf(Entity entity) {
		return entity instanceof LocalClientPlayerEntity && Minecraft.getInstance().player.getNetworkId() == entity.getNetworkId();
	}
}
