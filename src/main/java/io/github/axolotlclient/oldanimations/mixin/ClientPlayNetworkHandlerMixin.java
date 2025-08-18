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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.client.network.handler.ClientPlayNetworkHandler;
import net.minecraft.client.options.GameOptions;
import net.minecraft.network.packet.s2c.play.TitlesS2CPacket;
import net.minecraft.world.Difficulty;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

	@Unique
	private static final String[] axolotlclient$EVENT_MESSAGES = new String[]{"tile.bed.notValid", null, null, "gameMode.changed"};

	@ModifyExpressionValue(method = "handleAddXpOrb", at = @At(value = "CONSTANT", args = "doubleValue=32"))
	private double axolotlclient$oldOrbRendering(double original) {
		return original / (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.xpOrbPosition.get() ?
			32.0D : 1.0D /* renders the xp orbs similar to 1.7 by oddly offsetting them */
		);
	}

	@ModifyExpressionValue(method = "handleEntityPickup", at = @At(value = "CONSTANT", args = "floatValue=0.5"))
	private float axolotlclient$oldItemPickup(float original) {
		/* taken from 1.7 */
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldItemPickup.get() ? -0.5F : original;
	}

	@Inject(method = "handleTitles", at = @At("HEAD"), cancellable = true)
	private void axolotlclient$disableTitlesPacket(TitlesS2CPacket packet, CallbackInfo ci) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.disableTitles.get()) {
			/* 1.7 doesn't have titles */
			ci.cancel();
		}
	}

	@WrapWithCondition(method = "handleLogin", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/options/GameOptions;difficulty:Lnet/minecraft/world/Difficulty;"))
	private boolean axolotlclient$dontUsePacketDifficulty(GameOptions instance, Difficulty value) {
		/* we're going to set the options difficulty elsewhere, so let's remove this as it's not needed */
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.oldDifficultyButtonLogic.get();
	}

	@WrapOperation(method = "handleGameEvent", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnet/minecraft/network/packet/s2c/play/GameEventS2CPacket;EVENT_MESSAGES:[Ljava/lang/String;"))
	private String[] axolotlclient$oldEventMessages(Operation<String[]> original) {
		/* taken from 1.7 */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldGameModeCommand.get()) {
			/* the server still notifies us on game mode changes, so we can easily just adapt this 1.7 code */
			return axolotlclient$EVENT_MESSAGES;
		}
		return original.call();
	}
}
