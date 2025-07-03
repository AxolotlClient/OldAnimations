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
import net.minecraft.client.render.model.block.BlockModel;
import net.minecraft.client.resource.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.resource.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

	@Unique
	private static final Map<String, String> SKULL_TEXTURES = Map.of(
		"char", "items/skull_steve",
		"creeper", "items/skull_creeper",
		"skeleton", "items/skull_skeleton",
		"wither", "items/skull_wither",
		"zombie", "items/skull_zombie"
	);

	@Unique
	private static final Identifier BUILTIN_GENERATED = new Identifier("minecraft:builtin/generated");

    @Inject(method = "registerItemVariants", at = @At("TAIL"))
    private void axolotlclient$registerCustomModels(CallbackInfo ci) {
		/* register our custom models */
		/* potions */
		List<String> originalPotions = itemVariants.get(Items.POTION);
		/* these dummy models are using the original models as a base, so resource packs can still edit them :) */
		List<String> potionComponents = Arrays.asList("bottle_drinkable_empty", "bottle_overlay", "bottle_splash_empty");
		originalPotions.addAll(potionComponents);
		itemVariants.put(Items.POTION, originalPotions);
    }

	@ModifyReturnValue(method = "loadBlockModel", at = @At("RETURN"))
	private BlockModel axolotlclient$removeGrassSideOverlay(BlockModel original, @Local(argsOnly = true) Identifier identifier) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.fastGrass.get() &&
			"minecraft:models/block/grass.json".equals(identifier.toString())) {
			/* removes overlay element if found */
			original.getElements().removeIf(element -> {
				if (element.faces.isEmpty()) return true;
				element.faces.entrySet().removeIf(entry -> "#overlay".equals(entry.getValue().texture));
				return element.faces.isEmpty();
			});
		}

		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.replaceSkullModel.get()) {
			/* defines our 1.7 skull model! */
			String path = identifier.getPath();
			if (path.startsWith("models/item/skull_") && path.endsWith(".json")) {
				String skullType = path.substring(18, path.length() - 5);
				String skullTexture = SKULL_TEXTURES.get(skullType);
				if (skullTexture != null) {
					BlockModelAccessor model = (BlockModelAccessor) original;
					model.setParentLocation(BUILTIN_GENERATED);
					model.getTextures().put("layer0", skullTexture);
				}
			}
		}

		//TODO: This could probably be rewritten
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldDoorTextures.get()) {
			/* we just need to swap out the textures lmaooo */
			String path = identifier.getPath();
			if ("models/item/iron_door.json".equals(path)) {
				BlockModelAccessor model = (BlockModelAccessor) original;
				model.getTextures().put("layer0", "items/old_door_iron");
			}
			if ("models/item/oak_door.json".equals(path)) {
				BlockModelAccessor model = (BlockModelAccessor) original;
				model.getTextures().put("layer0", "items/old_door_wood");
			}
		}
		return original;
	}
}
