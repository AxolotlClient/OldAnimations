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

import java.util.function.BooleanSupplier;

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

public class OldAnimationsConfig {

	public static OldAnimationsConfig instance = new OldAnimationsConfig();
	private final BooleanOption enabled = new BooleanOption("enabled", true);

	/* reloading resources does not reload certain niche things like the potion color cache */
	public boolean reloadPotionColors;

	@Getter
	private final OptionCategory category = OptionCategory.create(OldAnimations.MODID);
	private final OptionCategory blockingItemUsing = OptionCategory.create("blockingItemUsing");
	private final OptionCategory categorySneaking = OptionCategory.create("sneaking");
	private final OptionCategory categoryItems = OptionCategory.create("items");
	private final OptionCategory categoryResources = OptionCategory.create("resources");
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
	public final BooleanOption show1_7_10 = new BooleanOption("show1_7_10", true);
	public final BooleanOption debugInfo = new BooleanOption("debugInfo", true);
	public final BooleanOption disableDebugBackground = new BooleanOption("disableDebugBackground", true);
	public final BooleanOption alwaysShowCrosshair = new BooleanOption("alwaysShowCrosshair", true);
	public final BooleanOption debugTextSpacing = new BooleanOption("debugTextSpacing", true);
	public final BooleanOption debugTextColorScheme = new BooleanOption("debugTextColorScheme", true);
	public final BooleanOption debugTextShadow = new BooleanOption("debugTextShadow", true);
	public final BooleanOption thirdPersonSneaking = new BooleanOption("thirdPersonSneaking", true);
	public final BooleanOption allowMiningCancel = new BooleanOption("allowMiningCancel", true);
	public final BooleanOption damageTintColor = new BooleanOption("damageTintColor", true);
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
	public final BooleanOption tabDimensions = new BooleanOption("tabDimensions", true);
	public final BooleanOption disableTabPlayerHeads = new BooleanOption("disableTabPlayerHeads", true);
	public final BooleanOption disableTabHeader = new BooleanOption("disableTabHeader", true);
	public final BooleanOption disableTabFooter = new BooleanOption("disableTabFooter", true);
	public final BooleanOption equipLogic = new BooleanOption("equipLogic", true);
	public final BooleanOption oldDamageTick = new BooleanOption("oldDamageTick", true);
	public final BooleanOption oldSwingVisual = new BooleanOption("oldSwingVisual", true);
	public final BooleanOption oldSwingVisualParticles = new BooleanOption("oldSwingVisualParticles", true);
	public final BooleanOption slowUpSneak = new BooleanOption("slowUpSneak", true);
	public final BooleanOption stopLineTranslateSneak = new BooleanOption("stopLineTranslateSneak", true);
	public final BooleanOption oldGlintLayer = new BooleanOption("oldGlintLayer", true);
	public final BooleanOption skullModel = new BooleanOption("skullModel", true);
	public final BooleanOption xpOrbPosition = new BooleanOption("xpOrbPosition", true);
	public final BooleanOption disableServerSelectionButtons = new BooleanOption("disableServerSelectionButtons", true);
	public final BooleanOption disableUnknownServerIcon = new BooleanOption("disableUnknownServerIcon", true);
	public final BooleanOption disableSkinCustomizationButton = new BooleanOption("disableSkinCustomizationButton", true);
	public final BooleanOption oldMultiplayerSettingsPage = new BooleanOption("oldMultiplayerSettingsPage", true);
	public final BooleanOption fixThirdPersonHeldItemSneakDeSync = new BooleanOption("fixThirdPersonHeldItemSneakDeSync", true);
	public final BooleanOption oldBowRotation = new BooleanOption("oldBowRotation", true);
	public final BooleanOption swordBlockThirdPerson = new BooleanOption("swordBlockThirdPerson", true);
	public final BooleanOption fastGrass = new BooleanOption("fastGrass", true);
	public final BooleanOption difficultyLogic = new BooleanOption("difficultyLogic", true);
	public final BooleanOption moveSprintKeybind = new BooleanOption("moveSprintKeybind", true);
	public final BooleanOption oldFogGrayScale = new BooleanOption("oldFogGrayScale", true);
	public final BooleanOption oldDamageTintLighting = new BooleanOption("oldDamageTintLighting", true);
	public final BooleanOption refreshResourcesRegardless = new BooleanOption("refreshResourcesRegardless", true);
	public final BooleanOption removeHitBoxEyeLines = new BooleanOption("removeHitBoxEyeLines", true);
	public final BooleanOption hitboxOffset = new BooleanOption("hitboxOffset", true);
	public final BooleanOption disableGlintOnBlocks = new BooleanOption("disableGlintOnBlocks", true);
	public final BooleanOption separateDamageTintFromGlint = new BooleanOption("separateDamageTintFromGlint", true);
	public final BooleanOption doubleTapSneak = new BooleanOption("doubleTapSneak", true);
	public final BooleanOption rotationVecYawFix = new BooleanOption("rotationVecYawFix", true);
	public final BooleanOption framedItemLighting = new BooleanOption("framedItemLighting", true);
	public final BooleanOption oldMapArms = new BooleanOption("oldMapArms", true);
	public final BooleanOption oldGameModeCommand = new BooleanOption("oldGameModeCommand", true);
	public final BooleanOption alwaysShowOutline = new BooleanOption("alwaysShowOutline", true);
	public final BooleanOption blockEntityMiningProgress = new BooleanOption("blockEntityMiningProgress", true);
	public final BooleanOption clientSideEntityMovement = new BooleanOption("clientSideEntityMovement", true);
	public final BooleanOption oldFramerateChunkRendering = new BooleanOption("oldFramerateChunkRendering", true);
	public final BooleanOption voidFog = new BooleanOption("voidFog", true);
	public final BooleanOption dontSortTabEntries = new BooleanOption("dontSortTabEntries", true);
	public final BooleanOption hideScoreboardHearts = new BooleanOption("hideScoreboardHearts", true);
	public final BooleanOption oldObjectivesPosition = new BooleanOption("oldObjectivesPosition", true);
	public final BooleanOption miningProgressResetLogic = new BooleanOption("miningProgressResetLogic", true);
	public final BooleanOption rowBasedEntryOrder = new BooleanOption("rowBasedEntryOrder", true);
	public final BooleanOption dontUpdateEffectsHud = new BooleanOption("dontUpdateEffectsHud", true);
	public final BooleanOption inventoryTextLighting = new BooleanOption("inventoryTextLighting", true);
	public final BooleanOption oldJumpBoostPotionColor = new BooleanOption("oldJumpBoostPotionColor", true);
	public final BooleanOption beaconRendering = new BooleanOption("beaconRendering", true);
	public final BooleanOption controlsListButtonHeight = new BooleanOption("controlsListButtonHeight", true);
	public final BooleanOption fire = new BooleanOption("fire", true);
	public final BooleanOption eggEntityCollisionParticles = new BooleanOption("eggEntityCollisionParticles", true);
	public final BooleanOption fallParticles = new BooleanOption("fallParticles", true);
	public final BooleanOption skullLayerRendering = new BooleanOption("skullLayerRendering", true);
	public final BooleanOption noSkullLayerDamageTint = new BooleanOption("noSkullLayerDamageTint", true);
	public final BooleanOption fireChargeSound = new BooleanOption("fireChargeSound", true);
	public final BooleanOption oldFlightSpeed = new BooleanOption("oldFlightSpeed", true);
	public final BooleanOption skullBlockRendering = new BooleanOption("skullBlockRendering", true);
	public final BooleanOption creeperOffset = new BooleanOption("creeperOffset", true);
	public final BooleanOption hopperSound = new BooleanOption("hopperSound", true);
	public final BooleanOption fireSound = new BooleanOption("fireSound", true);
	public final BooleanOption disconnectScreen = new BooleanOption("disconnectScreen", true);
	public final BooleanOption disconnectServerToTitleScreen = new BooleanOption("disconnectServerToTitleScreen", true);
	public final BooleanOption customizeWorldPresetIcons = new BooleanOption("customizeWorldPresetIcons", true);
	public final BooleanOption packFormatWarning = new BooleanOption("packFormatWarning", true);
	public final BooleanOption goldenCarrotCreativeTab = new BooleanOption("goldenCarrotCreativeTab", true);
	public final BooleanOption fastSmoothLighting = new BooleanOption("fastSmoothLighting", true);
	public final BooleanOption skyAndCloudPerspective = new BooleanOption("skyAndCloudPerspective", true);
	public final BooleanOption litFurnaceMinecart = new BooleanOption("litFurnaceMinecart", true);
	public final BooleanOption tallBlockBreakSync = new BooleanOption("tallBlockBreakSync", true);
	public final BooleanOption buggedChatOpacity = new BooleanOption("buggedChatOpacity", true);
	public final BooleanOption modelShadeAndAmbientOcclusion = new BooleanOption("modelShadeAndAmbientOcclusion", true);
	public final BooleanOption fenceGateItemModel = new BooleanOption("fenceGateItemModel", true);
	public final BooleanOption fenceGateWallMode = new BooleanOption("fenceGateWallMode", true);
	public final BooleanOption defaultWolfCollarColor = new BooleanOption("defaultWolfCollarColor", true);
	public final BooleanOption itemModelSideQuadRendering = new BooleanOption("itemModelSideQuadRendering", true);
	public final BooleanOption oldFastLeaves = new BooleanOption("oldFastLeaves", true);
	public final BooleanOption opaqueLeavesTextures = new BooleanOption("opaqueLeavesTextures", true);

	private final BooleanSupplier[] suppliers = new BooleanSupplier[]{
		enabled::get,
		skullModel::get,
		fastGrass::get,
		fire::get,
		oldJumpBoostPotionColor::get,
		fastSmoothLighting::get,
		modelShadeAndAmbientOcclusion::get,
		fenceGateItemModel::get,
		fenceGateWallMode::get,
		itemModelSideQuadRendering::get,
		opaqueLeavesTextures::get
	};
	private final boolean[] previousStates = {
		enabled.get(),
		skullModel.get(),
		fastGrass.get(),
		fire.get(),
		oldJumpBoostPotionColor.get(),
		fastSmoothLighting.get(),
		modelShadeAndAmbientOcclusion.get(),
		fenceGateItemModel.get(),
		fenceGateWallMode.get(),
		itemModelSideQuadRendering.get(),
		opaqueLeavesTextures.get()
	};

	public static boolean isEnabled() {
		return instance.enabled.get();
	}

	public void initConfig() {
		category.add(
			enabled
		);
		category.add(blockingItemUsing);
		blockingItemUsing.add(
			blockHitting,
			useAndMine,
			allowMiningCancel,
			miningProgressResetLogic,
			useAndMineParticles,
			blockingArm,
			swordBlockThirdPerson
		);
		category.add(categorySneaking);
		categorySneaking.add(
			smoothSneaking,
			slowUpSneak,
			thirdPersonSneaking
		);
		category.add(categoryItems);
		categoryItems.add(
			itemPositions,
			oldBowRotation,
			mirroredProjectiles,
			oldItemPickup,
			fastItems,
			stickRod,
			stopLineTranslateSneak,
			equipLogic,
			fixThirdPersonHeldItemSneakDeSync,
			framedItemLighting,
			oldJumpBoostPotionColor,
			goldenCarrotCreativeTab
		);
		category.add(categoryResources);
		categoryResources.add(
			skullModel,
			fastGrass,
			fire,
			fenceGateItemModel,
			itemModelSideQuadRendering,
			oldFastLeaves,
			opaqueLeavesTextures
		);
		category.add(categoryCombat);
		categoryCombat.add(
			clientSideEntityMovement,
			oldSwingVisual,
			oldSwingVisualParticles,
			secondLayerDamageTint,
			damageTintColor,
			oldDamageTick,
			oldDamageTintLighting,
			separateDamageTintFromGlint,
			noSkullLayerDamageTint
		);
		category.add(categoryGUI);
		categoryGUI.add(
			show1_7_10,
			heartFlashing,
			alwaysShowCrosshair,
			centeredSelectionMenus,
			disableServerSelectionButtons,
			disableUnknownServerIcon,
			disableSkinCustomizationButton,
			oldMultiplayerSettingsPage,
			difficultyLogic,
			refreshResourcesRegardless,
			moveSprintKeybind,
			controlsListButtonHeight,
			disableTitles,
			dontUpdateEffectsHud,
			inventoryTextLighting,
			disconnectScreen,
			disconnectServerToTitleScreen,
			customizeWorldPresetIcons,
			packFormatWarning,
			buggedChatOpacity
		);
		categoryGUI.add(categoryDebugOverlay);
		categoryDebugOverlay.add(
			debugInfo,
			disableDebugBackground,
			debugTextSpacing,
			debugTextColorScheme,
			debugTextShadow
		);
		categoryGUI.add(categoryTabOverlay);
		categoryTabOverlay.add(
			tabDimensions,
			//TODO: compat with axolotlclient
			disableTabPlayerHeads,
			disableTabHeader,
			disableTabFooter,
			hideScoreboardHearts,
			dontSortTabEntries,
			rowBasedEntryOrder,
			oldObjectivesPosition
		);
		category.add(categoryEnchantmentGlint);
		categoryEnchantmentGlint.add(
			oldGlint,
			oldGuiGlint,
			oldGlintColor,
			oldGlintLayer,
			disableGlintOnBlocks
		);
		category.add(categoryMisc);
		categoryMisc.add(
			doubleTapSneak,
			disableAlexModel,
			disableSkinLayers,
			flameOffset,
			oldMapArms,
			oldPickupArm,
			rotationVecYawFix,
			xpOrbPosition,
			oldFogGrayScale,
			removeHitBoxEyeLines,
			hitboxOffset,
			oldGameModeCommand,
			alwaysShowOutline,
			blockEntityMiningProgress,
			oldFramerateChunkRendering,
			beaconRendering,
			eggEntityCollisionParticles,
			fallParticles,
			skullLayerRendering,
			fireChargeSound,
			oldFlightSpeed,
			skullBlockRendering,
			creeperOffset,
			hopperSound,
			fireSound,
			fastSmoothLighting,
			skyAndCloudPerspective,
			litFurnaceMinecart,
			tallBlockBreakSync,
			modelShadeAndAmbientOcclusion,
			fenceGateWallMode,
			defaultWolfCollarColor
		);

		/* reload the resources upon toggling certain options */
		reloadResources();

		ConfigManager configManager = new VersionedJsonConfigManager(FabricLoader.getInstance().getConfigDir().resolve(OldAnimations.MODID + ".json"),
			category, 1, (configVersion, configVersion1, optionCategory, jsonObject) -> jsonObject);
		AxolotlClientConfig.getInstance().register(configManager);
		configManager.load();
	}

	//todo: rewrite this to be more readable. also need to better document whats going on here
	private void reloadResources() {
		MinecraftClientEvents.TICK_END.register(client -> {
			boolean needsReload = false;
			boolean reloadPotionCache = false;
			boolean reloadWorld = false;
			for (int i = 0; i < suppliers.length; i++) {
				boolean current = suppliers[i].getAsBoolean();
				if (current != previousStates[i]) {
					previousStates[i] = current;
					if (i == 4) {
						reloadPotionCache = true;
					} else if (i == 5 || i == 8 || i == 10) {
						reloadWorld = true;
					} else {
						needsReload = true;
					}
				}
			}
			if (needsReload) {
				Minecraft.getInstance().reloadResources();
				reloadPotionColors = true;
			} else {
				if (reloadPotionCache) reloadPotionColors = true;
				if (reloadWorld) Minecraft.getInstance().worldRenderer.reload();
			}
		});
	}
}
