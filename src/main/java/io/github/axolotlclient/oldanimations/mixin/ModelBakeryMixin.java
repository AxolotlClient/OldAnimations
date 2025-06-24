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

import net.minecraft.client.resource.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

    @Shadow
    private Map<Item, List<String>> itemVariants;

    @Inject(method = "registerItemVariants", at = @At("TAIL"))
    private void axolotlclient$registerCustomModels(CallbackInfo ci) {
		/* register our custom models */
		/* potions */
		List<String> originalPotions = itemVariants.get(Items.POTION);
		List<String> potionComponents = Arrays.asList("bottle_drinkable_empty", "bottle_overlay", "bottle_splash_empty");
		originalPotions.addAll(potionComponents);
		itemVariants.put(Items.POTION, originalPotions);
		/* skulls */
		List<String> originalSkulls = itemVariants.get(Items.SKULL);
		/* this feature is using custom model files to replicate the model */
		/* while using vanilla texture names meaning resource packs from 1.7 and prior are compatible */
		List<String> oldSkulls = Arrays.asList("old_skull_skeleton", "old_skull_wither", "old_skull_zombie", "old_skull_char", "old_skull_creeper");
		originalSkulls.addAll(oldSkulls);
		itemVariants.put(Items.SKULL, originalSkulls);
    }
}
