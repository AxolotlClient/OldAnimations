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

import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.OpaqueLeavesHandler;
import net.minecraft.block.*;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.client.render.block.BlockModelShaper;
import net.minecraft.client.resource.ModelIdentifier;
import net.minecraft.client.resource.model.BakedModel;
import net.minecraft.client.resource.model.ModelManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockModelShaper.class)
public abstract class BlockModelShaperMixin {

	@Shadow
	public abstract ModelManager getManager();

	@Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
	private void axolotlclient$useOpaqueLeavesModel(BlockState state, CallbackInfoReturnable<BakedModel> cir) {
		Block block = state.getBlock();
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.opaqueLeavesTextures.get() &&
			OpaqueLeavesHandler.isOpaqueLeavesBlock(block)) {
			cir.setReturnValue(getManager().getModel(new ModelIdentifier(
				OpaqueLeavesHandler.getVariantName(block, state) + "_leaves_opaque", "inventory"))
			);
		}
	}
}
