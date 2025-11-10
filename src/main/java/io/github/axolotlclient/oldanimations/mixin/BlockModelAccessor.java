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

import net.minecraft.client.render.model.block.BlockElement;
import net.minecraft.client.render.model.block.BlockModel;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.resource.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;

@Mixin(BlockModel.class)
public interface BlockModelAccessor {

	/* saw this in the spongepowered discord server :p */
	@Invoker("<init>")
	static BlockModel createBlockModel(List<BlockElement> list, Map<String, String> map, boolean bl, boolean bl2, ModelTransformations modelTransformations) {
		throw new AssertionError();
	}

	@Invoker("<init>")
	static BlockModel createBlockModel(Identifier identifier, List<BlockElement> list, Map<String, String> map, boolean bl, boolean bl2, ModelTransformations modelTransformations) {
		throw new AssertionError();
	}

	@Accessor
	ModelTransformations getTransformations();

	@Accessor
	Map<String, String> getTextures();

	@Accessor
	void setParentLocation(Identifier identifier);
}
