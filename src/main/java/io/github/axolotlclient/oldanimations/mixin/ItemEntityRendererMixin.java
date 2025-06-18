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

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.oldanimations.OldAnimations;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin extends EntityRenderer<ItemEntity> {

	protected ItemEntityRendererMixin(EntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@ModifyArg(method = "applyItemBobbing", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;rotatef(FFFF)V"), index = 0)
	private float axolotlclient$itemFacePlayer(float angle, @Local boolean bl) {
		if (OldAnimations.isEnabled() && OldAnimations.getInstance().fastItems.get() && !bl) {
			return 180.0F - dispatcher.cameraYaw;
		}
		return angle;
	}

	@ModifyArg(method = "render(Lnet/minecraft/entity/ItemEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/block/ModelTransformations;apply(Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V", ordinal = 1))
	private ModelTransformations.Type axolotlclient$replaceTransform(ModelTransformations.Type type) {
		return OldAnimations.isEnabled() && OldAnimations.getInstance().fastItems.get() ? ModelTransformations.Type.GUI : type;
	}

	@Inject(method = "render(Lnet/minecraft/entity/ItemEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/resource/model/BakedModel;)V", ordinal = 1))
	private void axolotlclient$applyItemEntityPosition(ItemEntity itemEntity, double d, double e, double f, float g, float h, CallbackInfo ci) {
		if (OldAnimations.isEnabled() && OldAnimations.getInstance().fastItems.get()) {
			/* half of a pixel, matches 1.7's sprite rendering */
			GlStateManager.translatef(0.0F, 0.0F, 0.03125F);
		}
	}
}
