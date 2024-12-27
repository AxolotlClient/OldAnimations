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

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.managers.VersionedJsonConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.oldanimations.mixin.LivingEntityAccessor;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.HitResult;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;

public class OldAnimations implements ClientModInitializer {

	public static final String MODID = "axolotlclient-oldanimations";
	public static boolean AXOLOTLCLIENT;

	@Getter
	private final static OldAnimations instance = new OldAnimations();

	@Getter
	private final OptionCategory category = OptionCategory.create(MODID);

	public final BooleanOption enabled = new BooleanOption("enabled", true);
	public final BooleanOption useAndMine = new BooleanOption("useAndMine", true);
	public final BooleanOption particles = new BooleanOption("particles", true); /* use and mine particles */
	public final BooleanOption blocking = new BooleanOption("blocking", true);
	public final BooleanOption eatingAndDrinking = new BooleanOption("eatingAndDrinking", true);
	public final BooleanOption itemPositions = new BooleanOption("itemPositions", true);
	public final BooleanOption bow = new BooleanOption("bow", true);
	public final BooleanOption rod = new BooleanOption("rod", true);
	public final BooleanOption armourDamage = new BooleanOption("armorDamage", true);
	public final BooleanOption sneaking = new BooleanOption("sneaking", true);
	public final BooleanOption heartFlashing = new BooleanOption("heartFlashing", true);
	public final BooleanOption debugOverlay = new BooleanOption("debugOverlay", true);


	private Minecraft mc;

	public OldAnimations() {
		category.add(
			enabled,
			useAndMine,
			particles,
			blocking,
			eatingAndDrinking,
			itemPositions,
			bow,
			rod,
			armourDamage,
			sneaking,
			heartFlashing,
			debugOverlay
		);
		category.includeInParentTree(false);
		AXOLOTLCLIENT = FabricLoader.getInstance().isModLoaded("axolotlclient");


		if (AXOLOTLCLIENT) {
			// TODO once we have 3.1.0 on the maven this can be uncommented again
			//AxolotlClient.CONFIG.rendering.add(category);
		}
	}

	@Override
	public void initClient() {
		AxolotlClientConfig.getInstance().register(new VersionedJsonConfigManager(FabricLoader.getInstance().getConfigDir().resolve(MODID + ".json"),
			category, 1, (configVersion, configVersion1, optionCategory, jsonObject) -> jsonObject));
	}

	public void tick() {
		if (mc == null) {
			mc = Minecraft.getInstance();
		}
		if (mc.player != null && mc.player.abilities.canModifyWorld && enabled.get() && useAndMine.get() && mc.crosshairTarget != null
			&& mc.crosshairTarget.type == HitResult.Type.BLOCK && mc.player != null && mc.options.attackKey.isPressed()
			&& mc.options.usekey.isPressed() && mc.player.getItemUseTimer() > 0) {
			if ((!mc.player.handSwinging
				|| mc.player.handSwingTicks >= ((LivingEntityAccessor) mc.player).getArmSwingAnimationEnd()
				/ 2
				|| mc.player.handSwingTicks < 0)) {
				mc.player.handSwingTicks = -1;
				mc.player.handSwinging = true;
			}

			if (particles.get()) {
				mc.particleManager.addBlockMiningParticles(mc.crosshairTarget.getPos(), mc.crosshairTarget.face);
			}
		}
	}
}
