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

import net.minecraft.block.AbstractLeavesBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Leaves2Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.render.block.BlockLayer;

public class OpaqueLeavesHandler {

	public static boolean isOpaqueLeavesBlock(Block block) {
		/* if the block is solid and a leaves block, fast graphics is enabled lol */
		/* we can remove the first check to apply to custom blocks */
		return block instanceof AbstractLeavesBlock && block.getRenderLayer() == BlockLayer.SOLID;
	}

	public static String getVariantName(Block block, BlockState state) {
		if (block instanceof LeavesBlock) {
			return state.get(LeavesBlock.VARIANT).toString();
		} else if (block instanceof Leaves2Block) {
			return state.get(Leaves2Block.VARIANT).toString();
		}
		/* default value. hopefully this is never needed. as the above 2 classes are the only ones extending abstractleavesblock */
		/* that being said, this will cause a conflict with any mod which adds custom leaves extending the abstractleavesblock class lol */
		return "oak";
	}
}
