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
import net.minecraft.client.render.item.ItemModelShaper;
import net.minecraft.client.resource.ModelIdentifier;
import net.minecraft.client.resource.model.BakedModel;
import net.minecraft.client.resource.model.ModelManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemModelShaper.class)
public abstract class ItemModelShaperMixin {

	@Shadow
	public abstract ModelManager getManager();

	@Inject(method = "getModel(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/resource/model/BakedModel;", at = @At("HEAD"), cancellable = true)
 	private void axolotlclient$useCustomModel$skull(ItemStack stack, CallbackInfoReturnable<BakedModel> cir) {
 		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.replaceSkullModel.get() && stack.getItem() instanceof SkullItem) {
			String id = switch (stack.getMetadata()) {
				case 0 -> "old_skull_skeleton";
				case 1 -> "old_skull_wither";
				case 2 -> "old_skull_zombie";
				case 4 -> "old_skull_creeper";
				default -> "old_skull_char";
			};
			cir.setReturnValue(getManager().getModel(new ModelIdentifier(id, "inventory")));
 		}
 	}
}
