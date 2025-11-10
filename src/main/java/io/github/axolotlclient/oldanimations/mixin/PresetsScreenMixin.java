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
import net.minecraft.block.Blocks;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.client.gui.screen.PresetsScreen;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PresetsScreen.class)
public class PresetsScreenMixin {

	@ModifyVariable(method = "addFlatWorldPreset(Ljava/lang/String;Lnet/minecraft/item/Item;ILnet/minecraft/world/biome/Biome;Ljava/util/List;[Lnet/minecraft/world/gen/chunk/FlatWorldLayer;)V", at = @At("HEAD"), argsOnly = true)
	private static Item axolotlclient$waterWorldPresetIcon(Item original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.customizeWorldPresetIcons.get() && original == Items.WATER_BUCKET) {
			/* this should be flowing water, however, still water uses the same model in 1.7 sooooo */
			return Item.byBlock(Blocks.FLOWING_WATER);
		}
		return original;
	}

	@ModifyVariable(method = "addFlatWorldPreset(Ljava/lang/String;Lnet/minecraft/item/Item;ILnet/minecraft/world/biome/Biome;Ljava/util/List;[Lnet/minecraft/world/gen/chunk/FlatWorldLayer;)V", at = @At("HEAD"), argsOnly = true)
	private static int axolotlclient$useDeadBushId(int original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.customizeWorldPresetIcons.get() && original == TallPlantBlock.Type.GRASS.getId()) {
			return TallPlantBlock.Type.DEAD_BUSH.getId();
		}
		return original;
	}
}
