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

package io.github.axolotlclient.oldanimations.config;

import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.managers.VersionedJsonConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.oldanimations.OldAnimations;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.ornithemc.osl.lifecycle.api.client.MinecraftClientEvents;

import java.util.function.Supplier;

public class OldAnimationsConfig {

	public static OldAnimationsConfig instance = new OldAnimationsConfig();
	private final BooleanOption enabled = new BooleanOption("enabled", true);

	@Getter
	private final OptionCategory category = OptionCategory.create(OldAnimations.MODID);
	private final OptionCategory categoryBlocking = OptionCategory.create("blockingItemUsing");
	private final OptionCategory categorySneaking = OptionCategory.create("sneaking");
	private final OptionCategory categoryItems = OptionCategory.create("items");
	private final OptionCategory categoryCombat = OptionCategory.create("combat");
	private final OptionCategory categoryGUI = OptionCategory.create("gui");
	private final OptionCategory categoryDebugOverlay = OptionCategory.create("debugOverlay");
	private final OptionCategory categoryTabOverlay = OptionCategory.create("tabOverlay");
	private final OptionCategory categoryEnchantmentGlint = OptionCategory.create("enchantmentGlint");
	private final OptionCategory categoryMisc = OptionCategory.create("misc");

	public final BooleanOption useAndMine = new BooleanOption("useAndMine", true);
	public final BooleanOption useAndMineParticles = new BooleanOption("useAndMineParticles", true);
	public final BooleanOption blockHitting = new BooleanOption("blockHitting", true);
	public final BooleanOption itemPositions = new BooleanOption("itemPositions", true);
	public final BooleanOption secondLayerDamageTint = new BooleanOption("secondLayerDamageTint", true);
	public final BooleanOption smoothSneaking = new BooleanOption("smoothSneaking", true);
	public final BooleanOption heartFlashing = new BooleanOption("heartFlashing", true);
	public final BooleanOption show1_7_10 = new BooleanOption("show1_7_10", false);
	public final BooleanOption debugInfo = new BooleanOption("debugInfo", true);
	public final BooleanOption disableDebugBackground = new BooleanOption("disableDebugBackground", true);
	public final BooleanOption debugCrosshair = new BooleanOption("debugCrosshair", true);
	public final BooleanOption debugTextSpacing = new BooleanOption("debugTextSpacing", true);
	public final BooleanOption debugTextColorScheme = new BooleanOption("debugTextColorScheme", true);
	public final BooleanOption debugTextShadow = new BooleanOption("debugTextShadow", true);
	public final BooleanOption thirdPersonSmoothSneaking = new BooleanOption("thirdPersonSmoothSneaking", true);
	public final BooleanOption allowMiningCancel = new BooleanOption("allowMiningCancel", true);
	public final BooleanOption damageColor = new BooleanOption("damageColor", true);
	public final BooleanOption stickRod = new BooleanOption("stickRod", false);
	public final BooleanOption blockingArm = new BooleanOption("blockingArm", true);
	public final BooleanOption fastItems = new BooleanOption("fastItems", false);
	public final BooleanOption mirroredProjectiles = new BooleanOption("mirroredProjectiles", true);
	public final BooleanOption disableAlexModel = new BooleanOption("disableAlexModel", false);
	public final BooleanOption disableSkinLayers = new BooleanOption("disableSkinLayers", false);
	public final BooleanOption flameOffset = new BooleanOption("flameOffset", false);
	public final BooleanOption disableTitles = new BooleanOption("disableTitles", false);
	public final BooleanOption oldItemPickup = new BooleanOption("oldItemPickup", true);
	public final BooleanOption oldPickupArm = new BooleanOption("oldPickupArm", true);
	public final BooleanOption oldGlint = new BooleanOption("oldGlint", true);
	public final BooleanOption oldGuiGlint = new BooleanOption("oldGuiGlint", true);
	public final BooleanOption oldGlintColor = new BooleanOption("oldGlintColor", true);
	public final BooleanOption centeredSelectionMenus = new BooleanOption("centeredSelectionMenus", true);
	public final BooleanOption tabDimensions = new BooleanOption("tabDimensions", false);
	public final BooleanOption disableTabPlayerHeads = new BooleanOption("disableTabPlayerHeads", false);
	public final BooleanOption disableTabHeader = new BooleanOption("disableTabHeader", false);
	public final BooleanOption disableTabFooter = new BooleanOption("disableTabFooter", false);
	public final BooleanOption equipLogic = new BooleanOption("equipLogic", true);
	public final BooleanOption oldDamageTick = new BooleanOption("oldDamageTick", true);
	public final BooleanOption oldSwingVisual = new BooleanOption("oldSwingVisual", true);
	public final BooleanOption oldSwingVisualParticles = new BooleanOption("oldSwingVisualParticles", true);
	public final BooleanOption slowUpSneak = new BooleanOption("slowUpSneak", true);
	public final BooleanOption stopLineTranslateSneak = new BooleanOption("stopLineTranslateSneak", true);
	public final BooleanOption fixCameraPitch = new BooleanOption("fixCameraPitch", true);
	public final BooleanOption oldPotionGlint = new BooleanOption("oldPotionGlint", true);
	public final BooleanOption replaceSkullModel = new BooleanOption("replaceSkullModel", false);
	public final BooleanOption xpOrbPosition = new BooleanOption("xpOrbPosition", false);
	public final BooleanOption disableServerSelectionButtons = new BooleanOption("disableServerSelectionButtons", false);
	public final BooleanOption disableUnknownServerIcon = new BooleanOption("disableUnknownServerIcon", false);
	public final BooleanOption disableSkinCustomizationButton = new BooleanOption("disableSkinCustomizationButton", false);
	public final BooleanOption oldMultiplayerSettingsPage = new BooleanOption("oldMultiplayerSettingsPage", false);
	public final BooleanOption disableResourcePackItemTransformations = new BooleanOption("disableResourcePackItemTransformations", false);
	public final BooleanOption fixThirdPersonHeldItemSneakDeSync = new BooleanOption("fixThirdPersonHeldItemSneakDeSync", true);
	public final BooleanOption oldRodRotation = new BooleanOption("oldRodRotation", true);
	public final BooleanOption oldBowRotation = new BooleanOption("oldBowRotation", true);
	public final BooleanOption swordBlockThirdPerson = new BooleanOption("swordBlockThirdPerson", true);
	public final BooleanOption useAndMineDestroyVisual = new BooleanOption("useAndMineDestroyVisual", false);
	public final BooleanOption fastGrass = new BooleanOption("fastGrass", false);

	private final Supplier<Boolean>[] suppliers = new Supplier[] {
		replaceSkullModel::get,
		fastGrass::get
	};
	private final boolean[] previousStates = {
		replaceSkullModel.get(),
		fastGrass.get()
	};

	public static boolean isEnabled() {
		return instance.enabled.get();
	}

	public void initConfig() {
		category.add(
			enabled
		);
		category.add(categoryBlocking);
		categoryBlocking.add(
			blockHitting,
			useAndMine,
			allowMiningCancel,
			useAndMineParticles,
			useAndMineDestroyVisual,
			blockingArm,
			swordBlockThirdPerson
		);
		category.add(categorySneaking);
		categorySneaking.add(
			smoothSneaking,
			slowUpSneak,
			thirdPersonSmoothSneaking
		);
		category.add(categoryItems);
		categoryItems.add(
			itemPositions,
			oldRodRotation,
			oldBowRotation,
			mirroredProjectiles,
			oldItemPickup,
			fastItems,
			stickRod,
			stopLineTranslateSneak,
			equipLogic,
			replaceSkullModel,
			disableResourcePackItemTransformations,
			fixThirdPersonHeldItemSneakDeSync
		);
		category.add(categoryCombat);
		categoryCombat.add(
			oldSwingVisual,
			oldSwingVisualParticles,
			secondLayerDamageTint,
			damageColor,
			oldDamageTick
		);
		category.add(categoryGUI);
		categoryGUI.add(
			show1_7_10,
			heartFlashing,
			centeredSelectionMenus,
			disableServerSelectionButtons,
			disableUnknownServerIcon,
			disableSkinCustomizationButton,
			oldMultiplayerSettingsPage,
			disableTitles
		);
		categoryGUI.add(categoryDebugOverlay);
		categoryDebugOverlay.add(
			debugInfo,
			disableDebugBackground,
			debugCrosshair,
			debugTextSpacing,
			debugTextColorScheme,
			debugTextShadow
		);
		categoryGUI.add(categoryTabOverlay);
		categoryTabOverlay.add(
			tabDimensions,
			disableTabHeader,
			disableTabFooter
		);
		//TODO: fix compat with axolotlclient
		categoryTabOverlay.add(disableTabPlayerHeads);
		category.add(categoryEnchantmentGlint);
		categoryEnchantmentGlint.add(
			oldGlint,
			oldGuiGlint,
			oldGlintColor,
			oldPotionGlint
		);
		category.add(categoryMisc);
		categoryMisc.add(
			disableAlexModel,
			disableSkinLayers,
			flameOffset,
			oldPickupArm,
			fixCameraPitch,
			xpOrbPosition,
			fastGrass
		);

		/* reload the resources upon toggling certain options */
		reloadResources();

		ConfigManager configManager = new VersionedJsonConfigManager(FabricLoader.getInstance().getConfigDir().resolve(OldAnimations.MODID + ".json"),
			category, 1, (configVersion, configVersion1, optionCategory, jsonObject) -> jsonObject);
		AxolotlClientConfig.getInstance().register(configManager);
		configManager.load();
	}

	private void reloadResources() {
		MinecraftClientEvents.TICK_END.register(client -> {
			boolean needsReload = false;
			for (int i = 0; i < suppliers.length; i++) {
				boolean current = suppliers[i].get();
				if (current != previousStates[i]) {
					previousStates[i] = current;
					needsReload = true;
				}
			}
			if (needsReload) {
				Minecraft.getInstance().reloadResources();
			}
		});
	}
}
