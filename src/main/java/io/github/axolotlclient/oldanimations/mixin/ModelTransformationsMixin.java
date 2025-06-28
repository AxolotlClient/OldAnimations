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
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.ItemUtil;
import net.minecraft.client.render.model.block.ModelTransformation;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelTransformations.class)
public abstract class ModelTransformationsMixin {

	@Unique
	private static final float SCALE = 0.0625F;

	/* this WILL need to be rewritten in the future as it's an illogical mess, but it serves it's purpose well */
	@Inject(method = "apply", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;rotatef(FFFF)V", ordinal = 0, shift = At.Shift.AFTER))
	private void axolotlclient$modifyResourcePackTransformations(ModelTransformations.Type type, CallbackInfo ci, @Local ModelTransformation modelTransformation) {
		if (OldAnimationsConfig.isEnabled()) {
			boolean packTransformsEnabled = !OldAnimationsConfig.instance.disableResourcePackItemTransformations.get();
			ItemStack heldStack = ItemUtil.getHeldItemStack();
			ItemStack guiStack = ItemUtil.getGuiItemStack();
			ModelTransformations.Type firstPerson =  ModelTransformations.Type.FIRST_PERSON;
			ModelTransformations.Type thirdPerson =  ModelTransformations.Type.THIRD_PERSON;

			/* these transformations will normalize the few special item positions  */
			if (packTransformsEnabled && heldStack != null) {
				if (OldAnimationsConfig.instance.oldRodRotation.get() && heldStack.getItem().shouldRotate() && type == firstPerson) {
					GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
				}
				if (OldAnimationsConfig.instance.itemPositions.get()) {
					if (heldStack.getItem().shouldRotate() && type == thirdPerson) {
						GlStateManager.rotatef(-180.0F, 1.0F, 0.0F, 0.0F);
						GlStateManager.translatef(0.0F, -1.25F * SCALE, 0.0F);
					}
					if (ItemUtil.isThinBlockItem(heldStack)) {
						if (type == firstPerson) {
							GlStateManager.translatef(0.0F, -5.25F * SCALE, 0.0F);
						} else if (type == thirdPerson) {
							GlStateManager.translatef(0.0F, 1.25F * SCALE, 0.0F);
						}
					}
				}
			}
			if (OldAnimationsConfig.instance.replaceSkullModel.get() && ((guiStack != null && guiStack.getItem() instanceof SkullItem) ||
				(heldStack != null && heldStack.getItem() instanceof SkullItem && packTransformsEnabled))) {
				/* inverse translations */
				/* and make skull items have the same item transforms of like apples or diamonds */
				switch (type) {
					case FIRST_PERSON:
						GlStateManager.rotatef(180, 0.0F, 1.0F, 0.0F);
						GlStateManager.translatef(0.0F, 4.0F * SCALE, 2.0F * SCALE);
						GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
						GlStateManager.rotatef(25.0F, 0.0F, 0.0F, 1.0F);
						GlStateManager.scalef(1.7F, 1.7F, 1.7F);
						GlStateManager.scalef(1.0F / 0.55F, 1.0F / 0.55F, 1.0F / 0.55F);
						return;
					case THIRD_PERSON:
						GlStateManager.rotatef(45, 0.0F, 1.0F, 0.0F);
						GlStateManager.translatef(0.0F, -SCALE, 2.5F * SCALE);
						GlStateManager.translatef(0.0F, 0.0625F, -3.0F * SCALE);
						GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
						GlStateManager.scalef(0.55F, 0.55F, 0.55F);
						GlStateManager.scalef(1.0F / 0.25F, 1.0F / 0.25F, 1.0F / 0.25F);
						GlStateManager.rotatef(-180, 1.0F, 0.0F, 0.0F);
						return;
					case GUI:
						GlStateManager.rotatef(-180, 0.0F, 1.0F, 0.0F);
						GlStateManager.scalef(1.0F / 0.7F, 1.0F / 0.7F, 1.0F / 0.7F);
						return;
					case GROUND:
					case FIXED:
					default:
				}
			}
		}
	}
}
