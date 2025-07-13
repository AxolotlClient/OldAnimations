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
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.render.entity.layer.CapeLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public abstract class CapeLayerMixin {

	@Inject(method = "render(Lnet/minecraft/client/entity/living/player/ClientPlayerEntity;FFFFFFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;translatef(FFF)V"))
	private void axolotlClient$addSneakingTranslation(ClientPlayerEntity clientPlayerEntity, float f, float g, float h, float i, float j, float k, float l, CallbackInfo ci) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.thirdPersonCapePosition.get() && clientPlayerEntity.isSneaking()) {
			/* honestly, idk why the cape is positioned differently in 1.8 versus 1.7.... weird lol */
			/* this is a rough estimation of where it should be positioned... not ideal, but its better than nothing lol */
			GlStateManager.translatef(0.0F, 0.0125F, 0.0F);
		}
	}
}
