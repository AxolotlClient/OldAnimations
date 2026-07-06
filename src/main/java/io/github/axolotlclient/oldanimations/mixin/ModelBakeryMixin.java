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
import net.minecraft.block.Blocks;
import net.minecraft.client.render.model.block.*;
import net.minecraft.client.resource.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.Direction;
import org.lwjgl.util.vector.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.function.Predicate;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

	//TODO: this whole mixin class can be improved

    @Shadow
    private Map<Item, List<String>> itemVariants;

	@Shadow
	protected abstract Identifier getModelsJsonLocation(Identifier identifier);

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
		//TODO: There HAS to be a better way of doing this T_T
		List<String> originalSkulls = itemVariants.get(Items.SKULL);
		List<String> oldSkulls = Arrays.asList("old_skull_skeleton", "old_skull_wither", "old_skull_zombie", "old_skull_char", "old_skull_creeper");
		originalSkulls.addAll(oldSkulls);
		itemVariants.put(Items.SKULL, originalSkulls);


		/* i really dont want to do this */
		/* in order for 1.7 fast graphics leaves blocks, we need to have them on standby */
		/* aka they need to actually exist as their own block*/
		List<String> originalLeaves = itemVariants.get(Item.byBlock(Blocks.LEAVES));
		List<String> opaqueLeaves = Arrays.asList("oak_leaves_opaque", "spruce_leaves_opaque", "birch_leaves_opaque", "jungle_leaves_opaque");
		originalLeaves.addAll(opaqueLeaves);
		itemVariants.put(Item.byBlock(Blocks.LEAVES), originalLeaves);
		List<String> originalLeaves2 = itemVariants.get(Item.byBlock(Blocks.LEAVES2));
		List<String> opaqueLeaves2 = Arrays.asList("acacia_leaves_opaque", "dark_oak_leaves_opaque");
		originalLeaves2.addAll(opaqueLeaves2);
		itemVariants.put(Item.byBlock(Blocks.LEAVES2), originalLeaves2);
    }

	@ModifyReturnValue(method = "loadBlockModel", at = @At("RETURN"))
	private BlockModel axolotlclient$interceptModelLoading(BlockModel original, @Local(argsOnly = true) Identifier identifier) {
		if (!OldAnimationsConfig.isEnabled() || identifier.getPath().startsWith("builtin/")) {
			return original;
		}

		/* OptiFine does some trolling. luckily we can extract the model location fine from here */
		String model = getModelsJsonLocation(identifier).toString();
		if (OldAnimationsConfig.instance.fastGrass.get() && model.contains("models/block/grass.json")) {
			/* removes overlay element if found */
			original.getElements().removeIf(element -> {
				if (element.faces.isEmpty()) return true;
				element.faces.entrySet().removeIf(entry -> "#overlay".equals(entry.getValue().texture));
				return element.faces.isEmpty();
			});
		}

		/* this was an absolute nightmare. the potted flowers, redstone components, and tripwire hook have AO on certain parts of the block. */
		/* it's not entirely possible to port that into 1.8's model system as AO is applied to the entire model as a whole */
		/* AO will be disabled if shade is disabled then. i think this will sell the illusion better :> */
		/* hoppers have a different geometrical composition in 1.8+ so the model shading may look different */
		/* shout out to MC-68302 for rounding up the affected models. */
		if (OldAnimationsConfig.instance.modelShadeAndAmbientOcclusion.get()) {
			if (model.contains("models/block/comparator") && model.endsWith(".json")
				|| model.contains("models/block/repeater") && model.endsWith(".json")) {
				return axolotlclient$filterBlockModel(original,
					element -> element.faces.values().stream().noneMatch(face -> "#lit".equals(face.texture) || "#unlit".equals(face.texture)) && element.shade,
					original.usesAmbientOcclusion());
			}
			if (model.contains("models/block/wall_gate") && model.endsWith(".json")) {
				/* this one is interesting. AO is off intentionally?? - MC-72469 */
				return BlockModelAccessor.createBlockModel(
					original.getParentLocation(),
					original.getElements(),
					((BlockModelAccessor) original).getTextures(),
					true,
					original.isGui3d(),
					((BlockModelAccessor) original).getTransformations()
				);
			}
			if (model.contains("models/block/lever") && model.endsWith(".json")) {
				return axolotlclient$filterBlockModel(original,
					element -> element.faces.values().stream().noneMatch(face -> "#lever".equals(face.texture)) && element.shade,
					original.usesAmbientOcclusion());
			}
			if (model.contains("models/block/brewing_stand") && model.endsWith(".json")) {
				return axolotlclient$filterBlockModel(original,
					element -> element.faces.values().stream().noneMatch(face -> "#stand".equals(face.texture)) && element.shade,
					false);
			}
			if (model.contains("models/block/stem_") && model.endsWith(".json")) {
				return axolotlclient$filterBlockModel(original, element -> false, original.usesAmbientOcclusion());
			}
			if (model.contains("models/block/tripwire_hook") && model.endsWith(".json")) {
				return axolotlclient$filterBlockModel(original,
					element -> element.faces.values().stream().anyMatch(face -> face.cullFace != null) && element.shade,
					false
				);
			}
			if (model.contains("models/block/cauldron") && model.endsWith(".json") ||
				model.contains("models/block/hopper") && model.endsWith(".json") ||
				model.contains("models/block/flower_pot.json")) {
				return BlockModelAccessor.createBlockModel(
					original.getParentLocation(),
					original.getElements(),
					((BlockModelAccessor) original).getTextures(),
					true,
					original.isGui3d(),
					((BlockModelAccessor) original).getTransformations()
				);
			}
			if (model.contains("models/block/flower_pot_cross.json")) {
				return axolotlclient$filterBlockModel(original,
					element -> element.faces.values().stream().noneMatch(face -> "#plant".equals(face.texture)) && element.shade,
					original.usesAmbientOcclusion());
			}
			if (model.contains("models/block/flower_pot_cactus.json")) {
				return axolotlclient$filterBlockModel(original,
					element -> element.faces.values().stream().noneMatch(face -> "#cactus".equals(face.texture)) && element.shade,
					original.usesAmbientOcclusion());
			}
		}

		if (OldAnimationsConfig.instance.fire.get() && model.contains("models/block/fire_floor.json")) {
			/* in 1.7, the fire rendering is actually wildly different */
			/* i've hard coded the model for fun... i can definitely provide json compatibility some day :) */
			//TODO: Fire can render on the sides/underside of blocks... yeah this will be a massive pain to port
			return axolotlclient$tesselateFire();
		}

		if (OldAnimationsConfig.instance.skullModel.get()) {
			/* although possible through a resource pack, i would like to include this feature */
			/* the implementation should be flexible enough too */
			/* defines our 1.7 skull model! */
			String search = "models/item/skull_";
			String suffix = ".json";
			int typeStart = model.indexOf(search);
			if (typeStart >= 0) {
				int begin = typeStart + search.length();
				int end = model.indexOf(suffix, begin);
				if (end > begin) {
					/* we needed to extract "char" or "zombie" from the identifier location */
					String type = model.substring(begin, end);
					BlockModelAccessor blockModel = (BlockModelAccessor) original;
					blockModel.setParentLocation(BUILTIN_GENERATED);
					blockModel.getTextures().put("layer0", SKULL_TEXTURES.get(type));
				}
			}
		}

		/* these two models below have some tomfoolery going on. sorry */
		if (OldAnimationsConfig.instance.fenceGateItemModel.get()) {
			/* thought it would be nice to hardcode this in */
			/* this model is only used for the held item/inventory/dropped item */
			String search = "models/item/";
			String suffix = "_fence_gate.json";
			int typeStart = model.indexOf(search);
			if (typeStart >= 0) {
				int begin = typeStart + search.length();
				int end = model.indexOf(suffix, begin);
				if (end > begin) {
					/* we needed to extract the wood type from the identifier location */
					String type = model.substring(begin, end);
					((BlockModelAccessor) original).setParentLocation(new Identifier("minecraft:block/" + type + "_fence_gate_inventory"));
				}
			}
		}

		//todo: uhm..
//		if (OldAnimationsConfig.instance.oldFastLeavesTextures.get()) {
//			String search = "models/block/";
//			String suffix = "_leaves.json";
//			int typeStart = model.indexOf(search);
//			if (typeStart >= 0) {
//				int begin = typeStart + search.length();
//				int end = model.indexOf(suffix, begin);
//				if (end > begin) {
//					String type = model.substring(begin, end);
//					if (type.equals("dark_oak")) {
//						/* silly naming scheme */
//						type = "big_oak";
//					}
//					/* only replacing the texture here. i doubt id need to replace the parent model too :p */
//					((BlockModelAccessor) original).getTextures().put("all", "blocks/leaves_" + type + "_opaque");
//				}
//			}
//		}

		return original;
	}

	@Unique
	private BlockModel axolotlclient$filterBlockModel(BlockModel original, Predicate<BlockElement> shadePredicate, boolean ambientOcclusion) {
		List<BlockElement> filteredElements = original.getElements().stream()
			.map(element -> new BlockElement(
				element.from,
				element.to,
				element.faces,
				element.rotation,
				shadePredicate.test(element)
			)).toList();

		return BlockModelAccessor.createBlockModel(
			original.getParentLocation(),
			filteredElements,
			((BlockModelAccessor) original).getTextures(),
			ambientOcclusion,
			original.isGui3d(),
			((BlockModelAccessor) original).getTransformations()
		);
	}

	@Unique
	private BlockModel axolotlclient$tesselateFire() {
		/* there is no practical reason for me hardcoding this model in. i just thought it would be funny to do this by hand :p */
		Map<String, String> map = Map.of(
			"fire0", "blocks/fire_layer_0",
			"fire1", "blocks/fire_layer_1"
		);

		final BlockElementTexture texture_uv = new BlockElementTexture(new float[]{0, 0, 16, 16}, 0);
		final Vector3f origin = (Vector3f) (new Vector3f(8, 8, 8).scale(0.0625F));

		List<BlockElement> list = new ArrayList<>(8);

		/* diagonal inner planes */
		list.add(new BlockElement(
			new Vector3f(0, 0, 8), new Vector3f(16, 22.4f, 8),
			Collections.singletonMap(Direction.SOUTH, new BlockElementFace(null, -1, "#fire1", texture_uv)),
			new BlockElementRotation(origin, Direction.Axis.X, -22.5f, true),
			false
		));
		list.add(new BlockElement(
			new Vector3f(0, 0, 8), new Vector3f(16, 22.4f, 8),
			Collections.singletonMap(Direction.NORTH, new BlockElementFace(null, -1, "#fire1", texture_uv)),
			new BlockElementRotation(origin, Direction.Axis.X, 22.5f, true),
			false
		));
		list.add(new BlockElement(
			new Vector3f(8, 0, 0), new Vector3f(8, 22.4f, 16),
			Collections.singletonMap(Direction.WEST, new BlockElementFace(null, -1, "#fire0", texture_uv)),
			new BlockElementRotation(origin, Direction.Axis.Z, -22.5f, true),
			false
		));
		list.add(new BlockElement(
			new Vector3f(8, 0, 0), new Vector3f(8, 22.4f, 16),
			Collections.singletonMap(Direction.EAST, new BlockElementFace(null, -1, "#fire0", texture_uv)),
			new BlockElementRotation(origin, Direction.Axis.Z, 22.5f, true),
			false
		));

		/* vertical outer planes */
		list.add(new BlockElement(
			new Vector3f(0, 0, 16), new Vector3f(16, 22.4f, 16),
			Collections.singletonMap(Direction.SOUTH, new BlockElementFace(null, -1, "#fire0", texture_uv)),
			null,
			false
		));
		list.add(new BlockElement(
			new Vector3f(0, 0, 0), new Vector3f(16, 22.4f, 0),
			Collections.singletonMap(Direction.NORTH, new BlockElementFace(null, -1, "#fire0", texture_uv)),
			null,
			false
		));
		list.add(new BlockElement(
			new Vector3f(0, 0, 0), new Vector3f(0, 22.4f, 16),
			Collections.singletonMap(Direction.WEST, new BlockElementFace(null, -1, "#fire1", texture_uv)),
			null,
			false
		));
		list.add(new BlockElement(
			new Vector3f(16, 0, 0), new Vector3f(16, 22.4f, 16),
			Collections.singletonMap(Direction.EAST, new BlockElementFace(null, -1, "#fire1", texture_uv)),
			null,
			false

		));

		return BlockModelAccessor.createBlockModel(list, map, false, true, ModelTransformations.NONE);
	}
}
