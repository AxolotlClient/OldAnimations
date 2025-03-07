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
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlyingItemEntityRenderer.class)
public abstract class FlyingItemEntityRendererMixin {

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;rotate(FFFF)V", ordinal = 0), index = 0)
    private float axolotlclient$rotateProjectile(float angle) {
        return angle + (isMirroredProjectileEnabled() ? 180.0F : 0.0F);
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;rotate(FFFF)V", ordinal = 1), index = 0)
    private float axolotlclient$useProperCameraView(float angle) {
        return angle * (isMirroredProjectileEnabled() ? -1 : 1);
    }

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;)V"))
	private void axolotlclient$applyProjectilePosition(Entity entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
		if (isMirroredProjectileEnabled()) {
			GlStateManager.translate(0.0F, 0.25F, 0.0F);
		}
	}

	@Unique
	private static boolean isMirroredProjectileEnabled() {
		return OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().mirrorProjectiles.get();
	}
}
