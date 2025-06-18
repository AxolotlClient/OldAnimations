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

import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.managers.VersionedJsonConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;

import java.util.ArrayList;
import java.util.List;

public class OldAnimations implements ClientModInitializer {

	public static final String MODID = "axolotlclient-oldanimations";
	public static boolean AXOLOTLCLIENT;

	private static OldAnimations instance;

	@Getter
	private final OptionCategory category = OptionCategory.create(MODID).includeInParentTree(false);

	@Getter
	private final OptionCategory categoryBlocking = OptionCategory.create("Blocking");

	@Getter
	private final OptionCategory categorySneaking = OptionCategory.create("Sneaking");

	@Getter
	private final OptionCategory categoryItems = OptionCategory.create("Items");

	@Getter
	private final OptionCategory categoryCombat = OptionCategory.create("Combat");

	@Getter
	private final OptionCategory categoryGUI = OptionCategory.create("GUI");

	@Getter
	private final OptionCategory categoryEnchantmentGlint = OptionCategory.create("Enchantment Glint");

	@Getter
	private final OptionCategory categoryMisc = OptionCategory.create("Misc");

	public final BooleanOption enabled = new BooleanOption("enabled", true);

	public final BooleanOption useAndMine = new BooleanOption("useAndMine", true);
	public final BooleanOption useAndMineParticles = new BooleanOption("useAndMineParticles", true); /* use and mine particles */
	public final BooleanOption blocking = new BooleanOption("blocking", true);
	public final BooleanOption eatingAndDrinking = new BooleanOption("eatingAndDrinking", true);
	public final BooleanOption itemPositions = new BooleanOption("itemPositions", true);
	public final BooleanOption secondLayerDamageTint = new BooleanOption("secondLayerDamageTint", true);
	public final BooleanOption smoothSneaking = new BooleanOption("smoothSneaking", true);
	public final BooleanOption heartFlashing = new BooleanOption("heartFlashing", true);
	public final BooleanOption debugOverlay = new BooleanOption("debugOverlay", true);

	public final BooleanOption thirdPersonSmoothSneaking = new BooleanOption("thirdPersonSmoothSneaking", true);
	public final BooleanOption allowMiningCancel = new BooleanOption("allowMiningCancel", true);

	public final BooleanOption damageColor = new BooleanOption("damageColor", true);
	public final BooleanOption stickRod = new BooleanOption("stickRod", true);
	public final BooleanOption blockingArm = new BooleanOption("blockingArm", true);
	public final BooleanOption fastItems = new BooleanOption("fastItems", true);
	public final BooleanOption mirroredProjectiles = new BooleanOption("mirroredProjectiles", true);
	public final BooleanOption disableAlexModel = new BooleanOption("disableAlexModel", true);
	public final BooleanOption disableSkinLayers = new BooleanOption("disableSkinLayers", true);
	public final BooleanOption flameOffset = new BooleanOption("flameOffset", true);
	public final BooleanOption disableTitles = new BooleanOption("disableTitles", true);
	public final BooleanOption oldItemPickup = new BooleanOption("oldItemPickup", true);
	public final BooleanOption oldPickupArm = new BooleanOption("oldPickupArm", true);
	public final BooleanOption oldGlint = new BooleanOption("oldGlint", true);
	public final BooleanOption oldGuiGlint = new BooleanOption("oldGuiGlint", true);
	public final BooleanOption oldGlintColor = new BooleanOption("oldGlintColor", true);
	public final BooleanOption centeredSelectionMenus = new BooleanOption("centeredSelectionMenus", true);

	public final BooleanOption oldDamageTick = new BooleanOption("oldDamageTick", true);
	public final BooleanOption oldSwingVisual = new BooleanOption("oldSwingVisual", true); /* use and mine particles */
	public final BooleanOption oldSwingVisualParticles = new BooleanOption("oldSwingVisualParticles", true); /* use and mine particles */

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

	public static OldAnimations getInstance() {
		return OldAnimations.instance;
	}

	public static boolean isEnabled() {
		return OldAnimations.instance.enabled.get();
	}

	public static void runAfterFabricLoad(Runnable task) {
		if (loadedByFabric) {
			task.run();
		} else tasks.add(task);
	}

	@Override
	public void initClient() {
		category.add(getCategoryBlocking());
		categoryBlocking.add(
			blocking,
			useAndMine,
			allowMiningCancel,
			useAndMineParticles,
			blockingArm,
			eatingAndDrinking
		);
		category.add(getCategorySneaking());
		categorySneaking.add(
			smoothSneaking,
			thirdPersonSmoothSneaking
		);
		category.add(getCategoryItems());
		categoryItems.add(
			itemPositions,
			mirroredProjectiles,
			oldItemPickup,
			fastItems,
			stickRod
		);
		category.add(getCategoryCombat());
		categoryCombat.add(
			oldSwingVisual,
			oldSwingVisualParticles
		);
		category.add(getCategoryGUI());
		categoryGUI.add(
			heartFlashing,
			debugOverlay,
			centeredSelectionMenus,
			disableTitles
		);
		category.add(getCategoryEnchantmentGlint());
		categoryEnchantmentGlint.add(
			oldGlint,
			oldGuiGlint,
			oldGlintColor
		);
		category.add(getCategoryMisc());
		categoryMisc.add(
			secondLayerDamageTint,
			damageColor,
			disableAlexModel,
			disableSkinLayers,
			flameOffset,
			oldPickupArm,
			oldDamageTick
		);
		category.add(
			enabled
		);

		AXOLOTLCLIENT = FabricLoader.getInstance().isModLoaded("axolotlclient");

		ConfigManager configManager = new VersionedJsonConfigManager(FabricLoader.getInstance().getConfigDir().resolve(MODID + ".json"),
			category, 1, (configVersion, configVersion1, optionCategory, jsonObject) -> jsonObject);
		AxolotlClientConfig.getInstance().register(configManager);
		configManager.load();
	}
}
