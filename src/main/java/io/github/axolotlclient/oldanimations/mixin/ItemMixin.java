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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.CreativeModeTab;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public abstract class ItemMixin {

	@Shadow
	private static void register(Block block) {
	}

	@Inject(method = "init", at = @At("HEAD"))
	private static void axolotlclient$addRemovedBlocks(CallbackInfo ci) {
		/* adds in the blocks that 14w25a removed */
		/* this will allow us to actually give ourselves the item with commands in game like 1.7 ;) */
		register(Blocks.WATER);
		register(Blocks.LAVA);
		register(Blocks.FLOWING_WATER);
		register(Blocks.FLOWING_LAVA);
		register(Blocks.FIRE);
		//TODO: models
//		register(Blocks.NETHER_PORTAL);
//		register(Blocks.END_PORTAL);

		/* //TODO: one day...
		register(Blocks.DOUBLE_STONE_SLAB);
		register(Blocks.DOUBLE_WOODEN_SLAB);
		register(Blocks.COCOA);
		register(Blocks.POTATOES);
		register(Blocks.CARROTS);
		*/
	}

	@WrapOperation(
		method = "init",
		slice = @Slice(
			from = @At(value = "CONSTANT", args = "intValue=396"),
			to = @At(value = "CONSTANT", args = "intValue=397")
		),
		at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;setCreativeModeTab(Lnet/minecraft/item/CreativeModeTab;)Lnet/minecraft/item/Item;")
	)
	private static Item axolotlclient$goldenCarrotCreativeTab(Item instance, CreativeModeTab creativeModeTab, Operation<Item> original) {
		/* such a sus injection point.. oh well lol */
		/* MC-3664 */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.goldenCarrotCreativeTab.get()) {
			return instance;
		}
		return original.call(instance, creativeModeTab);
	}
}
