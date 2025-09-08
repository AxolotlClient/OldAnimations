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
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.gui.hud.vanilla.CrosshairHud;
import io.github.axolotlclient.oldanimations.OldAnimations;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.ducks.ILivingEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GameGui;
import net.minecraft.util.math.MathHelper;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GameGui.class)
public abstract class GameGuiMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@ModifyVariable(method = "renderStatusBars", at = @At(value = "STORE", ordinal = 0), index = 4)
	private boolean axolotlclient$useOldHealthLogic(boolean value) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.heartFlashing.get()) {
			/* taken straight from 1.7 */
			/* MC-2930 aimed to revert this... MC-73438 wants to bring this back.... there is no winning :/ */
			boolean i3 = minecraft.player.maxHealth / 3 % 2 == 1;
			if (minecraft.player.maxHealth < 10) {
				i3 = false;
			}
			return i3;
		}
		return value;
	}

	@ModifyExpressionValue(method = "renderStatusBars", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/gui/GameGui;displayHealth:I"))
	private int axolotlclient$useLastHealthValue(int value) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.heartFlashing.get()) {
			/* this was removed from 1.8... so i added it back :) */
			return MathHelper.ceil(((ILivingEntity) minecraft.player).axolotlclient$getLastHealth());
		}
		return value;
	}

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GameGui;hasCrosshair()Z"))
	private boolean axolotlclient$enableCrosshair(GameGui instance, Operation<Boolean> original) {
		boolean isCustomCrosshair = OldAnimations.AXOLOTLCLIENT && HudManager.getInstance().get(CrosshairHud.ID).isEnabled();
		return (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.alwaysShowCrosshair.get() && !isCustomCrosshair) || original.call(instance);
	}

	@ModifyExpressionValue(method = "render", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/gui/GameGui;titleTime:I", ordinal = 0))
	private int axolotlclient$skipTitleRendering(int original) {
		/* 1.7 doesn't have titles */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.disableTitles.get()) {
			return 0;
		}
		return original;
	}
}
