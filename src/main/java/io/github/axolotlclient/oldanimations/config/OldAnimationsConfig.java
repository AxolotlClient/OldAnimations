package io.github.axolotlclient.oldanimations.config;

import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.managers.VersionedJsonConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.oldanimations.OldAnimations;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;

public class OldAnimationsConfig {

	public static OldAnimationsConfig instance = new OldAnimationsConfig();
	private final BooleanOption enabled = new BooleanOption("enabled", true);

	@Getter
	private final OptionCategory category = OptionCategory.create(OldAnimations.MODID).includeInParentTree(false);
	private final OptionCategory categoryBlocking = OptionCategory.create("Blocking/Item Using");
	private final OptionCategory categorySneaking = OptionCategory.create("Sneaking");
	private final OptionCategory categoryItems = OptionCategory.create("Items");
	private final OptionCategory categoryCombat = OptionCategory.create("Combat");
	private final OptionCategory categoryGUI = OptionCategory.create("GUI");
	private final OptionCategory categoryDebugOverlay = OptionCategory.create("Debug Overlay");
	private final OptionCategory categoryTabOverlay = OptionCategory.create("Tab Overlay");
	private final OptionCategory categoryEnchantmentGlint = OptionCategory.create("Enchantment Glint");
	private final OptionCategory categoryMisc = OptionCategory.create("Misc");

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
	public final BooleanOption debugCrosshair = new BooleanOption("debugCrosshair", true);
	public final BooleanOption debugTextSpacing = new BooleanOption("debugTextSpacing", true);
	public final BooleanOption debugTextColorScheme = new BooleanOption("debugTextColorScheme", true);
	public final BooleanOption debugTextShadow = new BooleanOption("debugTextShadow", true);
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
			blockingArm
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
			mirroredProjectiles,
			oldItemPickup,
			fastItems,
			stickRod,
			stopLineTranslateSneak,
			equipLogic
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
		/* feature exists in axolotlclient :p */
		if (!OldAnimations.AXOLOTLCLIENT) {
			categoryTabOverlay.add(
				disableTabPlayerHeads
			);
		}
		category.add(categoryEnchantmentGlint);
		categoryEnchantmentGlint.add(
			oldGlint,
			oldGuiGlint,
			oldGlintColor
		);
		category.add(categoryMisc);
		categoryMisc.add(
			disableAlexModel,
			disableSkinLayers,
			flameOffset,
			oldPickupArm
		);

		ConfigManager configManager = new VersionedJsonConfigManager(FabricLoader.getInstance().getConfigDir().resolve(OldAnimations.MODID + ".json"),
			category, 1, (configVersion, configVersion1, optionCategory, jsonObject) -> jsonObject);
		AxolotlClientConfig.getInstance().register(configManager);
		configManager.load();
	}
}
