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

import java.util.ArrayList;
import java.util.List;

public class OldAnimations implements ClientModInitializer {

	/* TODO LIST

	--- Project ---
	add readme and a proper to-do list
	set up modrinth page / submit for review
	add sane and safe defaults

	--- Sneaking ---
	i really want to switch over to the 1.7 code instead of using the 1.13 code :/
	need to verify if other players have a different sneaking position

	--- Items ---
	blocks item positions (rotations) + fix trapdoors and pressure plates and other crap
	rod and bow and block positions and swing position.. can be separate options LOL
	split layer glint support for armor/spawn egg/firework charges

	--- Textures/Models ---
	add better compatibility for old textures -> make them variants
	fix weird fuzzy texture sizes of swords and stuff
	fast grass always shown in gui/dropped?
	held pressure plates look a bit different?? idk why. THEY'RE BIGGER THAT'S WHY
	old fence fate model
	oh my god. the custom models/textures are a damn nightmare of a mess
	tripwire texture and model changes
	cauldrons model and texture
	tops of certain blocks have switch uvs
	MC-262869 - top texture of blocks need to be rotated as noted above
	MC-262173 - tripwire hook using wrong wood texture
	MC-277768 - mipmap items

	--- World ---
	old fast smooth lighting shadows
	figure out why hurtTime is off by 1 tick in 1.7... this might also impact other aspects of the game
	1.7 world gen speed
	MC-195505 - mipmap short grass
	1.7 fire is different than 1.8... wtf
	armor stands don't exist in 1.7... hmmm
	fog is possibly different with the perspective code being added in 1.8... but im not sure to what extent it matters

	--- Misc ---
	certain mob hitboxes should be visually changed?? perhaps
	perhaps check if mining progress changed
	MC-73162 - breaking a painting breaks block behind it?
	MC-58120 - mob lag??? idk
	inventory text is lighter in 1.7 ??? interesting must look into :p

	*/

	public static final String MODID = "axolotlclient-oldanimations";
	public static boolean AXOLOTLCLIENT;

	private static OldAnimations instance;

	// Since AxolotlClient may initialize this class as a module before it gets loaded as a mod by fabric we have to defer the former to run after the latter.
	// But since the load order is non-deterministic this may not always be the case
	private static boolean loadedByFabric;
	private static final List<Runnable> tasks = new ArrayList<>();

	public OldAnimations() {
		if (instance != null) {
			throw new IllegalStateException();
		}
		loadedByFabric = true;
		instance = this;
		tasks.forEach(Runnable::run);
		tasks.clear();
	}

	public static void runAfterFabricLoad(Runnable task) {
		if (loadedByFabric) {
			task.run();
		} else tasks.add(task);
	}

	@Override
	public void initClient() {
		OldAnimationsConfig.instance.initConfig();
		AXOLOTLCLIENT = FabricLoader.getInstance().isModLoaded("axolotlclient");
	}
}
