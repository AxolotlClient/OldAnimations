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

import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.*;
import net.minecraft.item.*;

import java.util.HashMap;
import java.util.Map;

public final class ItemUtil {

	/* there was no better way of doing this sadly */
	@Getter
	@Setter
	private static ItemStack heldItemStack = null;
	@Getter
	@Setter
	private static ItemStack guiItemStack = null;

	/* some items are not quite compatible with 1.7's item position */
	private static final Map<Class<?>, Boolean> blacklistedItems = new HashMap<>() {{
		put(SkullItem.class, true);
		put(BannerItem.class, true);
	}};

	/* i fr thought pressure plates and trapdoors were affected by this.. i was wrong */
	private static final Map<Class<?>, Boolean> thinBlocks = new HashMap<>() {{
		put(CarpetBlock.class, true);
		put(SnowLayerBlock.class, true);
		put(DaylightDetectorBlock.class, true);
	}};

	/* for some reason, some blocks have alternative rotations in 1.7! */
	private static final Map<Class<?>, Boolean> rotatableBlocks = new HashMap<>() {{
		put(DispenserBlock.class, true);
		put(FurnaceBlock.class, true);
		put(PumpkinBlock.class, true);
		put(ChestBlock.class, true);
	}};

	public static boolean isBlacklisted(ItemStack stack) {
		if (stack == null) return false;
		/* exclude SkullItem from blacklist based on config condition */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.replaceSkullModel.get() && stack.getItem() instanceof SkullItem) {
			return false;
		}
		return blacklistedItems.containsKey(stack.getItem().getClass());
	}

	public static boolean isBlazeRod(ItemStack stack) {
		if (stack == null) return false;
		/* this guy is a con artist */
		return stack.getItem() == Items.BLAZE_ROD;
	}

	/* thank you animatium, very cool! */
	public static boolean isThinBlockItem(ItemStack stack) {
		if (stack != null) {
			final Block block = Block.byItem(stack.getItem());
			if (block == null) return false;
			return thinBlocks.containsKey(block.getClass());
		} else {
			return false;
		}
	}

	public static boolean shouldRotateBlock(ItemStack stack) {
		if (stack != null) {
			final Block block = Block.byItem(stack.getItem());
			if (block == null) return false;
			return rotatableBlocks.containsKey(block.getClass());
		} else {
			return false;
		}
	}
}
