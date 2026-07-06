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
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.OpaqueLeavesHandler;
import net.minecraft.block.AbstractLeavesBlock;
import net.minecraft.block.Block;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin extends EntityRenderer<ItemEntity> {

	@Unique
	private boolean axolotlclient$fastGraphics;

	protected ItemEntityRendererMixin(EntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@ModifyArg(method = "applyItemBobbing", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/GlStateManager;rotatef(FFFF)V"), index = 0)
	private float axolotlclient$itemFacePlayer(float angle, @Local boolean bl) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.fastItems.get() && !bl) {
			return 180.0F - dispatcher.cameraYaw;
		}
		return angle;
	}

	@Inject(method = "render(Lnet/minecraft/entity/ItemEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/resource/model/BakedModel;)V", ordinal = 1))
	private void axolotlclient$applyItemEntityPosition(ItemEntity itemEntity, double d, double e, double f, float g, float h, CallbackInfo ci) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.fastItems.get()) {
			/* half of a pixel, matches 1.7's sprite rendering */
			GlStateManager.translatef(0.0F, 0.0F, 0.03125F);
		}
	}

	@Inject(method = "render(Lnet/minecraft/entity/ItemEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/GlStateManager;blendFuncSeparate(IIII)V", shift = At.Shift.AFTER))
	private void axolotlclient$fastGraphicsLeavesPre$dropped(ItemEntity itemEntity, double d, double e, double f, float g, float h, CallbackInfo ci) {
		if (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.oldFastLeaves.get()
			|| OldAnimationsConfig.instance.opaqueLeavesTextures.get()) {
			return;
		}
		axolotlclient$fastGraphics = false;
		ItemStack itemStack = itemEntity.getItem();
		if (itemStack.getItem() instanceof BlockItem) {
			/* this is a continuation of what we see in ItemRendererMixin */
			if (OpaqueLeavesHandler.isOpaqueLeavesBlock(Block.byItem(itemStack.getItem()))) {
				axolotlclient$fastGraphics = true;
				GlStateManager.disableAlphaTest();
				GlStateManager.disableBlend();
			}
		}
	}

	@Inject(method = "render(Lnet/minecraft/entity/ItemEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/GlStateManager;disableBlend()V", shift = At.Shift.AFTER))
	private void axolotlclient$fastGraphicsLeavesPost$dropped(ItemEntity itemEntity, double d, double e, double f, float g, float h, CallbackInfo ci) {
		if (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.oldFastLeaves.get()
			|| OldAnimationsConfig.instance.opaqueLeavesTextures.get()) {
			return;
		}
		if (axolotlclient$fastGraphics) {
			GlStateManager.enableAlphaTest();
			axolotlclient$fastGraphics = false;
		}
	}
}
