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
import io.github.axolotlclient.oldanimations.util.ItemUtil;
import net.minecraft.client.render.model.block.ModelTransformations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelTransformations.class)
public abstract class ModelTransformationsMixin {

	/* this may need to be rewritten in the future, but it serves it's purpose well */
	@Inject(method = "apply", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;rotatef(FFFF)V", ordinal = 0, shift = At.Shift.AFTER))
	private void axolotlclient$modifyResourcePackTransformations(ModelTransformations.Type type, CallbackInfo ci) {
		if (OldAnimationsConfig.isEnabled() && !OldAnimationsConfig.instance.disableResourcePackItemTransformations.get() && ItemUtil.itemStack != null) {
			/* these transformations will normalize the few special item positions  */
			if (OldAnimationsConfig.instance.oldRodRotation.get() && ItemUtil.itemStack.getItem().shouldRotate() &&
				type == ModelTransformations.Type.FIRST_PERSON) {
				GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
			}
			if (OldAnimationsConfig.instance.itemPositions.get()) {
				if (ItemUtil.itemStack.getItem().shouldRotate() && type == ModelTransformations.Type.THIRD_PERSON) {
					GlStateManager.rotatef(-180.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.translatef(0.0F, -1.25F * 0.0625F, 0.0F);
				}
				if (ItemUtil.isThinBlockItem(ItemUtil.itemStack)) {
					if (type == ModelTransformations.Type.FIRST_PERSON) {
						GlStateManager.translatef(0.0F, -5.25F * 0.0625F, 0.0F);
					} else if (type == ModelTransformations.Type.THIRD_PERSON) {
						GlStateManager.translatef(0.0F, 1.25F * 0.0625F, 0.0F);
					}
				}
			}
		}
	}
}
