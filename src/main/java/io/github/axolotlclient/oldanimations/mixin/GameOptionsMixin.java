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

import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

	@Shadow
	public KeyBinding[] horbarKeyBindings;

	@Shadow
	public KeyBinding fullscreenKey;

	@Shadow
	public KeyBinding streamToggleMicKey;

	@Shadow
	public KeyBinding streamCommercialKey;

	@Shadow
	public KeyBinding streamPauseKey;

	@Shadow
	public KeyBinding streamStartStopKey;

	@Shadow
	public KeyBinding smoothCameraKey;

	@Shadow
	public KeyBinding togglePerspectiveKey;

	@Shadow
	public KeyBinding screenshotKey;

	@Shadow
	public KeyBinding commandKey;

	@Shadow
	public KeyBinding pickItemKey;

	@Shadow
	public KeyBinding playerListKey;

	@Shadow
	public KeyBinding chatKey;

	@Shadow
	public KeyBinding inventoryKey;

	@Shadow
	public KeyBinding dropKey;

	@Shadow
	public KeyBinding sprintKey;

	@Shadow
	public KeyBinding sneakKey;

	@Shadow
	public KeyBinding jumpKey;

	@Shadow
	public KeyBinding rightKey;

	@Shadow
	public KeyBinding backKey;

	@Shadow
	public KeyBinding leftKey;

	@Shadow
	public KeyBinding forwardKey;

	@Shadow
	public KeyBinding useKey;

	@Shadow
	public KeyBinding attackKey;

	//TODO: This needs to be toggleable... but how...
	@SuppressWarnings("unused") /* this IS the modification LOOOL. */
	/* i removed spectatorOutlinesKey from the array to match 1.7.10 */
	@Shadow
	public KeyBinding[] keyBindings = ArrayUtils.addAll(
		new KeyBinding[]{
			attackKey,
			useKey,
			forwardKey,
			leftKey,
			backKey,
			rightKey,
			jumpKey,
			sneakKey,
			sprintKey,
			dropKey,
			inventoryKey,
			chatKey,
			playerListKey,
			pickItemKey,
			commandKey,
			screenshotKey,
			togglePerspectiveKey,
			smoothCameraKey,
			streamStartStopKey,
			streamPauseKey,
			streamCommercialKey,
			streamToggleMicKey,
			fullscreenKey
		},
		horbarKeyBindings
	);
}
