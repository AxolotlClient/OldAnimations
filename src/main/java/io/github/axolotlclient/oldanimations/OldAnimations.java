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

import java.util.List;

import com.google.common.collect.Lists;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.common.ConfigHolder;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory;
import io.github.axolotlclient.oldanimations.mixin.LivingEntityAccessor;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;

public class OldAnimations implements ClientModInitializer {

	public static boolean AXOLOTLCLIENT;

	@Getter
	private final static OldAnimations instance = new OldAnimations();

	@Getter
	private final OptionCategory category = new OptionCategory("oldAnimations");

	public final BooleanOption enabled = new BooleanOption("enabled", true);
	public final BooleanOption useAndMine = new BooleanOption("useAndMine", true);
	public final BooleanOption particles = new BooleanOption("particles", true);
	public final BooleanOption blocking = new BooleanOption("blocking", true);
	public final BooleanOption blockingArm = new BooleanOption("blockingArm", true);
	public final BooleanOption eatingAndDrinking = new BooleanOption("eatingAndDrinking", true);
	public final BooleanOption itemPositions = new BooleanOption("itemPositions", true);
	public final BooleanOption itemPositionsThird = new BooleanOption("itemPositionsThird", true);
	public final BooleanOption replaceRodWithStick = new BooleanOption("replaceRodWithStick", true);
	public final BooleanOption mirrorProjectiles = new BooleanOption("mirrorProjectiles", true);
	public final BooleanOption oldGlint = new BooleanOption("oldGlint", true);
	public final BooleanOption oldPotionGlint = new BooleanOption("oldPotionGlint", true);
	public final BooleanOption armourDamage = new BooleanOption("armorDamage", true);
	public final BooleanOption sneaking = new BooleanOption("sneaking", true);
	public final BooleanOption sneakingThird = new BooleanOption("sneakingThird", true);
	public final BooleanOption flameOffset = new BooleanOption("flameOffset", true);
	public final BooleanOption oldSkinRendering = new BooleanOption("oldSkinRendering", false);
	public final BooleanOption oldItemPickup = new BooleanOption("oldItemPickup", true);
	public final BooleanOption fixArmItemRotation = new BooleanOption("fixArmItemRotation", true);
	public final BooleanOption oldRenderTickDelay = new BooleanOption("oldRenderTickDelay", true);
	public final BooleanOption heartFlashing = new BooleanOption("heartFlashing", true);
	public final BooleanOption disableTitles = new BooleanOption("disableTitles", false);
	public final BooleanOption debugOverlay = new BooleanOption("debugOverlay", true);
	public final BooleanOption tabOverlay = new BooleanOption("tabOverlay", false);
	public final BooleanOption centerGuiSelection = new BooleanOption("centerGuiSelection", true);

	public static final String MODID = "axolotlclient-oldanimations";

	private MinecraftClient mc;

	public OldAnimations() {
		category.add(
			enabled,
			useAndMine,
			particles,
			blocking,
			blockingArm,
			eatingAndDrinking,
			itemPositions,
			itemPositionsThird,
			replaceRodWithStick,
			mirrorProjectiles,
			oldGlint,
			oldPotionGlint,
			armourDamage,
			sneaking,
			sneakingThird,
			flameOffset,
			oldSkinRendering,
			oldItemPickup,
			fixArmItemRotation,
			oldRenderTickDelay,
			heartFlashing,
			disableTitles,
			debugOverlay,
			tabOverlay,
			centerGuiSelection
		);
		AXOLOTLCLIENT = FabricLoader.getInstance().isModLoaded("axolotlclient");

		if (!AXOLOTLCLIENT) {
			AxolotlClientConfigManager.getInstance().registerConfig(MODID, new ConfigHolder() {
				@Override
				public List<io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory> getCategories() {
					return Lists.newArrayList(category);
				}
			});
			AxolotlClientConfigManager.getInstance().save(MODID);
		} else {
			AxolotlClient.CONFIG.rendering.add(category);
		}
	}

	@Override
	public void onInitializeClient() {
		// Hi
	}

	public void tick() {
		if (mc == null) {
			mc = MinecraftClient.getInstance();
		}
		if (mc.player != null && mc.player.abilities.allowModifyWorld && enabled.get() && useAndMine.get() && mc.result != null
				&& mc.result.type == BlockHitResult.Type.BLOCK && mc.player != null && mc.options.attackKey.isPressed()
				&& mc.options.useKey.isPressed() && mc.player.getItemUseTicks() > 0) {
			if ((!mc.player.handSwinging || mc.player.handSwingTicks >= ((LivingEntityAccessor) mc.player).getArmSwingAnimationEnd() / 2 || mc.player.handSwingTicks < 0)) {
				mc.player.handSwingTicks = -1;
				mc.player.handSwinging = true;
			}

			if (particles.get()) {
				mc.particleManager.addBlockBreakingParticles(mc.result.getBlockPos(), mc.result.direction);
			}
		}
	}
}
