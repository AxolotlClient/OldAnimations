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

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.managers.JsonConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.oldanimations.mixin.LivingEntityAccessor;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.HitResult;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;

public class OldAnimations implements ClientModInitializer {

	public static boolean AXOLOTLCLIENT;

	@Getter
	private static OldAnimations instance;

	@Getter
	final OptionCategory category = OptionCategory.create(MODID);

	public final BooleanOption enabled = new BooleanOption("enabled", true);
	public final BooleanOption useAndMine = new BooleanOption("useAndMine", true);
	public final BooleanOption particles = new BooleanOption("particles", true);
	public final BooleanOption blocking = new BooleanOption("blocking", true);
	public final BooleanOption eatingAndDrinking = new BooleanOption("eatingAndDrinking", true);
	public final BooleanOption bow = new BooleanOption("bow", true);
	public final BooleanOption rod = new BooleanOption("rod", true);
	public final BooleanOption armourDamage = new BooleanOption("armorDamage", true);
	public final BooleanOption sneaking = new BooleanOption("sneaking", true);
	public final BooleanOption debugOverlay = new BooleanOption("debugOverlay", true);

	public static final String MODID = "axolotlclient-oldanimations";

	private Minecraft mc;

	public OldAnimations() {
		if (instance != null) {
			throw new IllegalStateException("Re-Instantiation of Singleton Class!");
		}
		instance = this;
		category.add(enabled, useAndMine, particles, blocking, eatingAndDrinking, bow, rod, armourDamage, sneaking, debugOverlay);
		AXOLOTLCLIENT = FabricLoader.getInstance().isModLoaded("axolotlclient");
	}

	@Override
	public void initClient() {
		ConfigManager manager = new JsonConfigManager(
			FabricLoader.getInstance().getConfigDir().resolve(MODID + ".json"), category);
		AxolotlClientConfig.getInstance().register(manager);
		manager.load();
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

	public static void oldDrinking(ItemStack itemToRender, ClientPlayerEntity clientPlayer,
								   float partialTicks) {
		float var14 = clientPlayer.getItemUseTimer() - partialTicks + 1.0F;
		float var15 = 1.0F - var14 / itemToRender.getUseDuration();
		float var16 = 1.0F - var15;
		var16 = var16 * var16 * var16;
		var16 = var16 * var16 * var16;
		var16 = var16 * var16 * var16;
		var16 -= 0.05F;
		float var17 = 1.0F - var16;
		GlStateManager.translatef(0.0F, MathHelper.abs(MathHelper.cos(var14 / 4F * (float) Math.PI) * 0.11F)
			* (var15 > 0.2D ? 1 : 0), 0.0F);
		GlStateManager.translatef(var17 * 0.6F, -var17 * 0.5F, 0.0F);
		GlStateManager.rotatef(var17 * 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(var17 * 10.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(var17 * 30.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translatef(0, -0.0F, 0.06F);
		GlStateManager.rotatef(-4F, 1, 0, 0);
	}

	public static void oldBlocking() {
		GlStateManager.scalef(0.83F, 0.88F, 0.85F);
		GlStateManager.translatef(-0.3F, 0.1F, 0.0F);
	}

	public void transformItem(Item item) {
		if (!(bow.get() || rod.get())) {
			return;
		}

		// https://github.com/sp614x/optifine/issues/2098
		if (mc.player.isHoldingItem() && item instanceof BowItem) {
			if (bow.get())
				GlStateManager.translatef(-0.01f, 0.05f, -0.06f);
		} else if ((item instanceof FishingRodItem) && rod.get()) {
			GlStateManager.translatef(0.08f, -0.027f, -0.33f);
			GlStateManager.scalef(0.93f, 1.0f, 1.0f);
		}
	}
}
