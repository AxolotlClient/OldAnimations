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

import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.PlayerUtil;
import net.minecraft.client.render.entity.ItemSpriteRenderer;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemSpriteRenderer.class)
public abstract class ProjectileRendererMixin {

	//TODO: Should probably separate the mirrored and position options

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/GlStateManager;translatef(FFF)V"))
	private void axolotlclient$includeEyeHeight(Entity entity, double dx, double dy, double dz, float yaw, float tickDelta, CallbackInfo ci) {
		if (axolotlclient$shouldMirrorProjectiles()) {
			/* 1.7's projectile position is suspiciously raised by this eye height value... */
			GlStateManager.translatef(0.0F, PlayerUtil.INSTANCE.getPlayerEntityEyeHeight(), 0.0F);
		}
	}

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/GlStateManager;rotatef(FFFF)V", ordinal = 0), index = 0)
	private float axolotlclient$rotateProjectile(float angle) {
		return angle + (axolotlclient$shouldMirrorProjectiles() ? 180.0F : 0.0F);
	}

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/GlStateManager;rotatef(FFFF)V", ordinal = 1), index = 0)
	private float axolotlclient$useProperCameraView(float angle) {
		return angle * (axolotlclient$shouldMirrorProjectiles() ? -1 : 1);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/ItemRenderer;renderItemInHand(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V"))
	private void axolotlclient$applyProjectilePosition(Entity entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
		if (axolotlclient$shouldMirrorProjectiles()) {
			/* item entities already have this translation which matches item rendering to 1.7 */
			GlStateManager.translatef(0.0F, 0.25F, 0.0F);
		}

		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.fastItems.get()) {
			/* half of a pixel, matches 1.7's sprite rendering */
			GlStateManager.translatef(0.0F, 0.0F, 0.03125F);
		}
	}

	@Unique
	private boolean axolotlclient$shouldMirrorProjectiles() {
		/* fast items are technically only supposed to be possible while the item's south quad is shown */
		/* that is impossible if projectiles are mirrored :p */
		return OldAnimationsConfig.isEnabled() && (OldAnimationsConfig.instance.mirroredProjectiles.get() || OldAnimationsConfig.instance.fastItems.get());
	}
}
