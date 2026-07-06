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

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.client.render.model.block.BlockElement;
import net.minecraft.client.render.model.block.BlockElementFace;
import net.minecraft.client.render.model.block.BlockElementTexture;
import net.minecraft.client.render.model.block.ItemModelGenerator;
import net.minecraft.client.render.texture.TextureAtlasSprite;
import net.minecraft.util.math.Direction;
import org.lwjgl.util.vector.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;

@Mixin(ItemModelGenerator.class /* unmapped - C_4311621 */)
public abstract class ItemModelGeneratorMixin {

	@Shadow
	protected abstract List<BlockElement> addSideElements(TextureAtlasSprite textureAtlasSprite, String string, int i);

	@WrapOperation(method = "processFrames", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/block/ItemModelGenerator;addSideElements(Lnet/minecraft/client/render/texture/TextureAtlasSprite;Ljava/lang/String;I)Ljava/util/List;"))
	private List<BlockElement> axolotlclient$itemModelSideQuadRendering(ItemModelGenerator instance, TextureAtlasSprite textureAtlasSprite, String string, int i, Operation<List<BlockElement>> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemModelSideQuadRendering.get()) {
			return axolotlclient$oldAddSideElements(textureAtlasSprite, string, i);
		}
		return addSideElements(textureAtlasSprite, string, i);
	}

	@Unique
	private List<BlockElement> axolotlclient$oldAddSideElements(TextureAtlasSprite textureAtlasSprite, String string, int i) {
		/* this was all manually ported/adapted from 1.7's held item renderer */

		List<BlockElement> list = Lists.newArrayList();

		int width = textureAtlasSprite.getWidth();
		int height = textureAtlasSprite.getHeight();
		float uMin = 0.0F;
		float vMin = 0.0F;
		float uMax = 16.0F;
		float vMax = 16.0F;

		float f = 0.5F * (uMin - uMax) / width;
		float g = 0.5F * (vMax - vMin) / height;

		for (int i10 = 0; i10 < width; i10++) {
			float h = (float) i10 / width;
			float l = uMin + (uMax - uMin) * ((float) i10 / width) - f;
			list.add(new BlockElement(
				new Vector3f(h * 16.0F, 0.0F, 7.5F), new Vector3f(h * 16.0F, 16.0F, 8.5F),
				Collections.singletonMap(
					Direction.WEST,
					new BlockElementFace(null, i, string, new BlockElementTexture(new float[]{l, vMin, l, vMax}, 0))
				), null, true
			));
		}

		for (int var14 = 0; var14 < width; var14++) {
			float i12 = (float) var14 / width;
			float m = uMin + (uMax - uMin) * i12 - f;
			float q = i12 + 1.0F / width;
			list.add(new BlockElement(
				new Vector3f(q * 16.0F, 0.0F, 7.5F), new Vector3f(q * 16.0F, 16.0F, 8.5F),
				Collections.singletonMap(
					Direction.EAST,
					new BlockElementFace(null, i, string, new BlockElementTexture(new float[]{m, vMin, m, vMax}, 0))
				), null, true
			));
		}

		for (int var15 = 0; var15 < height; var15++) {
			float j = (float) var15 / height;
			float n = vMax + (vMin - vMax) * j - g;
			float r = j + 1.0F / height;
			list.add(new BlockElement(
				new Vector3f(0.0F, r * 16.0F, 7.5F), new Vector3f(16.0F, r * 16.0F, 8.5F),
				Collections.singletonMap(
					Direction.UP,
					new BlockElementFace(null, i, string, new BlockElementTexture(new float[]{uMin, n, uMax, n}, 0))
				), null, true
			));
		}

		for (int var16 = 0; var16 < height; var16++) {
			float k = (float) var16 / height;
			float o = vMax + (vMin - vMax) * ((float) var16 / height) - g;
			list.add(new BlockElement(
				new Vector3f(0.0F, k * 16.0F, 7.5F), new Vector3f(16.0F, k * 16.0F, 8.5F),
				Collections.singletonMap(
					Direction.DOWN,
					new BlockElementFace(null, i, string, new BlockElementTexture(new float[]{uMin, o, uMax, o}, 0))
				), null, true
			));
		}

		return list;
	}
}
