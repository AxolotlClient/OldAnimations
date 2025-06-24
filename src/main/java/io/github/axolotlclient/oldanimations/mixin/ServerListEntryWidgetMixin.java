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
import net.minecraft.client.gui.widget.ServerListEntryWidget;
import net.minecraft.resource.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerListEntryWidget.class)
public abstract class ServerListEntryWidgetMixin {

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ServerListEntryWidget;m_5818390(IILnet/minecraft/resource/Identifier;)V", ordinal = 1))
	private boolean axolotlclient$disableUnknownServerIcon(ServerListEntryWidget instance, int i, int j, Identifier identifier) {
		return false;
	}

	@ModifyExpressionValue(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;touchscreen:Z"))
	private boolean axolotlclient$disableSelectionButtons(boolean original) {
		return false;
	}

	@ModifyVariable(method = "render", at = @At(value = "LOAD"), ordinal = 0, index = 8, argsOnly = true)
	private boolean axolotlclient$disableSelectionButtons2(boolean value) {
		return false;
	}

	@ModifyVariable(method = "mouseClicked", at = @At(value = "LOAD", ordinal = 0), ordinal = 4, index = 5, argsOnly = true)
	private int axolotlclient$disableSelectionButtons3(int value) {
		return value + 32;
	}
}
