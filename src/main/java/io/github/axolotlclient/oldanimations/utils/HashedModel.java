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

package io.github.axolotlclient.oldanimations.utils;

import net.minecraft.client.resource.model.BakedModel;
import net.minecraft.client.resource.model.BakedQuad;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.stream.Collectors;

public final class HashedModel {
    private final List<Integer> data;

    public HashedModel(List<Integer> data) {
        this.data = Objects.requireNonNull(data, "data cannot be null");
    }

    public HashedModel(BakedModel model) {
        List<BakedQuad> allQuads = new ArrayList<>();

        for (Direction face : Direction.values()) {
            List<BakedQuad> faceQuads = model.getQuads(face);
            if (faceQuads != null) {
                allQuads.addAll(faceQuads);
            }
        }

        List<BakedQuad> generalQuads = model.getQuads();
        if (generalQuads != null) {
            allQuads.addAll(generalQuads);
        }

        this.data = allQuads.stream()
                .flatMap(quad -> Arrays.stream(quad.getVertices()).limit(3).boxed())
                .collect(Collectors.toList());
    }

	public HashedModel copy(List<Integer> newData) {
        return new HashedModel(newData);
    }

    @Override
    public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof HashedModel)) {
			return false;
		} else {
			return Objects.equals(data, ((HashedModel) o).data);
		}
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    @Override
    public String toString() {
        return "HashedModel(data=" + data + ")";
    }
}
