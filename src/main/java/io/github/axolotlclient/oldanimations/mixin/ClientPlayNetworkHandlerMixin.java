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

import io.github.axolotlclient.oldanimations.OldAnimations;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

	@ModifyConstant(method = "onChunkRenderDistanceCenter", constant = @Constant(floatValue = 0.5F))
	private float axolotlclient$oldItemPickup(float original) {
		return OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().oldItemPickup.get() ? -0.5F : original;
	}

	@Inject(method = "onTitle", at = @At("HEAD"), cancellable = true)
	private void axolotlclient$disableTitlesPacket(TitleS2CPacket titleS2CPacket, CallbackInfo ci) {
		if (OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().oldItemPickup.get()) {
			ci.cancel();
		}
	}
}
