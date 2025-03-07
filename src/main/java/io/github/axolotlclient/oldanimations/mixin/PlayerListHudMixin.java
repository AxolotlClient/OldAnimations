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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.text.Text;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

import java.util.List;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	private Text header;

	@Shadow
	private Text footer;

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 1))
	public int axolotlclient$replace(List<?> instance) {
		return isTabOverlayEnabled() ? client.getNetworkHandler().maxPlayers : instance.size();
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I", ordinal = 1))
	public int axolotlclient$staticSlotWidth(int a, int b) {
		return isTabOverlayEnabled() ? 300 : Math.min(a, b);
	}

	@ModifyVariable(method = "render", at = @At("STORE"), index = 13)
	private int axolotlclient$capSlotWidth(int value) {
		if (isTabOverlayEnabled() && value > 150) {
			value = 150;
		}
		return value;
	}

	@ModifyConstant(
		method = "render",
		constant = @Constant(intValue = 5),
		slice = @Slice(
			from = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardObjective;getRenderType()Lnet/minecraft/scoreboard/ScoreboardCriterion$RenderType;", ordinal = 1),
			to = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;fill(IIIII)V", ordinal = 2)
		)
	)
	private int axolotlclient$removeBackgroundSpace(int constant) {
		return isTabOverlayEnabled() ? 0 : constant;
	}

	@Redirect(method = "render", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/gui/hud/PlayerListHud;header:Lnet/minecraft/text/Text;", ordinal = 0))
	public Text axolotlclient$disableHeaderElement(PlayerListHud instance) {
		return isTabOverlayEnabled() ? null : header;
	}

	@Redirect(method = "render", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/gui/hud/PlayerListHud;footer:Lnet/minecraft/text/Text;", ordinal = 0))
	public Text axolotlclient$disableFooterElement(PlayerListHud instance) {
		return isTabOverlayEnabled() ? null : footer;
	}

	@ModifyVariable(method = "render", at = @At("STORE"), index = 11)
	private boolean axolotlclient$disablePlayerHeads(boolean original) {
		return !isTabOverlayEnabled() && original;
	}

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;fill(IIIII)V", ordinal = 2), index = 2)
	private int axolotlclient$removeExtraPixels(int par1) {
		return par1 - (isTabOverlayEnabled() ? 1 : 0);
	}

	@Unique
	private static boolean isTabOverlayEnabled() {
		return OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().tabOverlay.get();
	}
}
