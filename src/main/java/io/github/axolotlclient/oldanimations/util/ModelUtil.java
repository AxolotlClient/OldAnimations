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

import net.minecraft.client.Minecraft;
import net.minecraft.client.resource.ModelIdentifier;
import net.minecraft.client.resource.model.BakedModel;
import net.minecraft.item.ItemStack;

public final class ModelUtil {

   public static BakedModel getModel(String model) {
      return Minecraft.getInstance().getBlockRenderDispatcher().getModelShaper().getManager().getModel(new ModelIdentifier(model, "inventory"));
   }

   public static BakedModel getSkullModel(ItemStack stack) {
      String model = switch (stack.getMetadata()) {
		  case 0 -> "old_skull_skeleton";
		  case 1 -> "old_skull_wither";
		  case 2 -> "old_skull_zombie";
		  case 4 -> "old_skull_creeper";
		  default -> "old_skull_char";
	  };
	   return getModel(model);
   }
}
