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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.ModelUtil;
import net.minecraft.client.render.item.ItemModelShaper;
import net.minecraft.client.resource.model.BakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemModelShaper.class)
public abstract class ItemModelShaperMixin {

	@ModifyReturnValue(method = "getModel(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/resource/model/BakedModel;", at = @At("RETURN"))
	private BakedModel axolotlclient$useCustomModel$skull(BakedModel original, @Local(argsOnly = true) ItemStack stack) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.replaceSkullModel.get() &&
			stack.getItem() instanceof SkullItem) {
			return ModelUtil.getSkullModel(stack);
		}
		return original;
	}
}
