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

package io.github.axolotlclient.oldanimations;

import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;

public class OldAnimations implements ClientModInitializer {

	/* TODO LIST

	--- Project ---
	add readme and a proper to-do list
	add more categories (particles, sounds, blocks, etc...)
	Some Mixins could benefit from using @Slice instead of multiple injectors

	--- Sneaking ---
	i really want to switch over to the 1.7 code instead of using the 1.13 code :/
	need to verify if other players have a different sneaking position

	--- Items ---
	blocks item positions (rotations) + fix trapdoors and pressure plates and other crap
	MC-57895 - stairs are wrong direction

	--- Textures/Models ---
	fix weird fuzzy texture sizes of swords and stuff - THIS WAS MIP MAP
	fast grass always shown in gui/dropped?
	held pressure plates look a bit different?? idk why. THEY'RE BIGGER THAT'S WHY
	MC-262869 - top texture of blocks need to be rotated as noted above
	MC-262173 - tripwire hook using wrong wood texture
	MC-277768 && MC-57574 && MC-195505 - mipmap items/blocks
	improve old skull stuff?
	anisotrophic filtering on items in 1.7

	--- World ---
	figure out why hurtTime is off by 1 tick in 1.7... this might also impact other aspects of the game
	1.7 world gen speed??
	block destroy progress whiteness
	figure out how to separate certain models (flowers from flower pot, etc)

	--- Mobs ---
	squids make footstep noises MC-4934
	witch bugged potion bottle
	mobs instant item pick up

	-- Version Number --
	serverlistentry 1.8.9
	minecraftserver 1.8.9
	integrated server 1.8.9

	--- Misc ---
	MC-73162 - breaking a painting breaks block behind it?
	framed items are not centered - MC-8662
	mobentity blockentity? WHAT THE HELL IS THIS SUPPOSED TO MEAN RAHHH
	MC-5270 - suffocation screen order (buggy lava)

	*/

	public static final String MODID = "axolotlclient-oldanimations";
	public static boolean AXOLOTLCLIENT = FabricLoader.getInstance().isModLoaded("axolotlclient");

	@Override
	public void initClient() {
		OldAnimationsConfig.instance.initConfig();
	}
}
