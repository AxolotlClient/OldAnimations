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

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.oldanimations.OldAnimations;
import io.github.axolotlclient.oldanimations.utils.PlayerUtils;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

	@Redirect(method = "renderFire", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/Entity;y:D"))
	private double axolotlclient$includeEyeHeight$Y(Entity instance, Entity entity, double d, double e, double f, float g) {
		double original = instance.y;
		if (isOldFlameEnabled(entity)) {
			original += entity.getEyeHeight();
		}
		return original;
	}

	@Inject(method = "postRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;renderFire(Lnet/minecraft/entity/Entity;DDDF)V"))
	private void axolotlclient$includeEyeHeight$renderFirePre(Entity entity, double d, double e, double f, float g, float h, CallbackInfo ci) {
		if (isOldFlameEnabled(entity)) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, entity.getEyeHeight(), 0.0F);
		}
	}

	@Inject(method = "postRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;renderFire(Lnet/minecraft/entity/Entity;DDDF)V", shift = At.Shift.AFTER))
	private void axolotlclient$includeEyeHeight$renderFirePost(Entity entity, double d, double e, double f, float g, float h, CallbackInfo ci) {
		if (isOldFlameEnabled(entity)) {
			GlStateManager.popMatrix();
		}
	}

	@Unique
	private static boolean isOldFlameEnabled(Entity entity) {
		return OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().flameOffset.get() && PlayerUtils.isSelf(entity);
	}
}
