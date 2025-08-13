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

	/*
	TODO LIST
	blocks item positions (rotations) + fix trapdoors and pressure plates and other crap
	held pressure plates look a bit different?? idk why THEYRE BIGGER THATS WHY
	old fence fate model
	old fast smooth lighting shadows
	tops of certain blocks have switch uvs
	tripwire texture and model changes
	cauldrons model and texture
	improve fake block mining believability - paneblock and tripwireblock are still not accurate
	rod and bow and block positions and swing position.. can be separate options LOL
	fix weird fuzzy texture sizes of swords and stuff
	fix weird inconsistencies with block item positions between 1.7 and my ported 1.8 code :/
	certain mob hitboxes should be visually changed
	fast grass always showns in gui/dropped?
	is sprint flying different??
	need to revamp the sneaking options.. third person sneaking position should be separated from smooth sneaking :p
	oh my god. the custom models/textures are a damn nightmare of a mess
	need to verify if other players have a different sneaking position
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
