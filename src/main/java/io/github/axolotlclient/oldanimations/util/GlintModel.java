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

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlintModel {

	private static final Map<HashedModel, BakedModel> glintMap = new HashMap<>();

	public static BakedModel getModel(BakedModel model) {
		return glintMap.computeIfAbsent(new HashedModel(model), k -> {
			BasicBakedModel.Builder builder = new BasicBakedModel.Builder(model, new CustomTextureAtlasSprite());
			return builder.build();
		});
	}

	public static class HashedModel {
		private final List<Integer> data = new ArrayList<>();

		public HashedModel(BakedModel model) {
			for (Direction face : Direction.values()) {
				for (BakedQuad quad : model.getByDirection(face)) {
					int[] vertexData = quad.getVertexData();
					for (int i = 0; i < 3; i++) {
						data.add(vertexData[i]);
					}
				}
			}
			for (BakedQuad quad : model.getQuads()) {
				int[] vertexData = quad.getVertexData();
				for (int i = 0; i < 3; i++) {
					data.add(vertexData[i]);
				}
			}
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			HashedModel that = (HashedModel) o;
			return data.equals(that.data);
		}

		@Override
		public int hashCode() {
			return data.hashCode();
		}
	}

	public static class CustomTextureAtlasSprite extends Sprite {
		private CustomTextureAtlasSprite() {
			super(null);
		}

		@Override
		public float getFrameU(double u) {
			return (float) (-u / 16);
		}

		@Override
		public float getFrameV(double v) {
			return (float) (v / 16);
		}
	}
}
