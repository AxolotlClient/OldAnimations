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
import io.github.axolotlclient.oldanimations.ducks.Sneaky;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

	@Inject(method = "render(Lnet/minecraft/entity/LivingEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;translate(FFF)V"))
    private void axolotlclient$addSneakingTranslation(LivingEntity livingEntity, double d, double e, double f, float g, float h, CallbackInfo ci) {
        /* in order to match 1.7, we need to elevate the player model while sneaking */
		if (OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().sneaking.get() &&
			livingEntity instanceof PlayerEntity && livingEntity.getName().equals(MinecraftClient.getInstance().player.getName())) {
			if (livingEntity.isSneaking()) {
				/* we need to remove the already existing sneaking offset */
				/* which is present in BiPedModel#render, PlayerEntityModel#render, and related classes */
				GlStateManager.translate(0.0F, -0.2F, 0.0F);
			}
			float eyeHeightOffset = 1.62F - ((Sneaky) MinecraftClient.getInstance().gameRenderer).axolotlclient$getEyeHeight();
			/* the elevation will be the difference between the player's sneaking eyeheight and their actual eyeheight (1.62 meters) */
			/* the player model should now move 1:1 with the crosshair */
			GlStateManager.translate(0.0F, eyeHeightOffset, 0.0F);
		}
    }
}
