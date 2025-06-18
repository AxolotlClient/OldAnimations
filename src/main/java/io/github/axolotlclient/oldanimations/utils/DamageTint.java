package io.github.axolotlclient.oldanimations.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;

import java.nio.FloatBuffer;

public class DamageTint {

	public static void setDamageTint(FloatBuffer buffer) {
		Minecraft.getInstance().gameRenderer.disableLightMap();
		GlStateManager.disableTexture();
		GlStateManager.disableAlphaTest();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(770, 771);
		GlStateManager.depthFunc(514);
		GlStateManager.color4f(buffer.get(0), buffer.get(1), buffer.get(2), buffer.get(3) + 0.1f /* matches 1.7 */);
	}

	public static void unsetDamageTint() {
		GlStateManager.depthFunc(515);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
		GlStateManager.enableTexture();
		Minecraft.getInstance().gameRenderer.enableLightMap();
	}
}
