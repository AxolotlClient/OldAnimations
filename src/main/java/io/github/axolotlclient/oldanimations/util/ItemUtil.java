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

package io.github.axolotlclient.oldanimations.util;

import net.minecraft.block.*;
import net.minecraft.item.ItemStack;

public final class ItemUtil {

	/* there was no better way of doing this sadly */
	public static ItemStack itemStack = null;

	/* thank you animatium, very cool! */
	public static boolean isThinBlockItem(ItemStack stack) {
		if (stack != null) {
			final Block block = Block.byItem(stack.getItem());
			return block instanceof CarpetBlock ||
				block instanceof TrapdoorBlock || block instanceof PressurePlateBlock ||
				block instanceof SnowLayerBlock || block instanceof DaylightDetectorBlock;
		} else {
			return false;
		}
	}
}
