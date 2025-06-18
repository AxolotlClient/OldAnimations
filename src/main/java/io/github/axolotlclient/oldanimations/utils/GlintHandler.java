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

package io.github.axolotlclient.oldanimations.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tessellator;
import io.github.axolotlclient.oldanimations.OldAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.TextureAtlas;
import net.minecraft.client.render.texture.TextureManager;
import net.minecraft.resource.Identifier;

import java.awt.*;

public final class GlintHandler {

	public static void renderEnchantmentGlintPre(TextureManager textureManager, Identifier glintTexture, int color) {
		GlStateManager.enableRescaleNormal();
	    GlStateManager.depthFunc(518);
	    GlStateManager.disableLighting();
	    GlStateManager.depthMask(false);
	    textureManager.bind(glintTexture);
	    GlStateManager.enableAlphaTest();
	    GlStateManager.alphaFunc(516, 0.1F);
	    GlStateManager.enableBlend();
	    GlStateManager.blendFuncSeparate(772, 1, 0, 0);

	    /* the glint color of the gui in 1.7 is what is used for the glint color in 1.8 coincidentally */
		/* might as well add this override option anyway */
	    if (OldAnimations.isEnabled() && OldAnimations.getInstance().oldGlintColor.get()) {
		    GlStateManager.color4f(0.5F, 0.25F, 0.8F, 1.0F);
	    } else {
			Color rgba = new Color(color);
		    GlStateManager.color4f(rgba.getRed() / 255.0F, rgba.getGreen() / 255.0F, rgba.getBlue() / 255.0F, rgba.getAlpha() / 255.0F);
	    }

	    GlStateManager.pushMatrix();
	}

    public static void renderEnchantmentGlintPost(TextureManager textureManager) {
		GlStateManager.translatef(-0.25F, -0.25F, -0.25F);
        GlStateManager.scalef(0.5F, 0.5F, 0.5F);
      	renderFace();
      	GlStateManager.popMatrix();
      	GlStateManager.blendFuncSeparate(770, 771, 1, 0);
      	GlStateManager.depthMask(true);
      	GlStateManager.enableLighting();
      	GlStateManager.depthFunc(515);
      	GlStateManager.disableAlphaTest();
      	GlStateManager.disableRescaleNormal();
      	GlStateManager.disableLighting();
      	textureManager.bind(TextureAtlas.BLOCKS_LOCATION);
    }
	private static void renderFace() {
	   Tessellator tessellator = Tessellator.getInstance();
	   BufferBuilder builder = tessellator.getBuilder();
	   builder.begin(7, DefaultVertexFormat.POSITION_TEX);
	   drawGlint(builder, (float)(Minecraft.getTime() % 3000L) / 3000.0F);
	   drawGlint(builder, (float)(Minecraft.getTime() % 4873L) / 4873.0F - 0.0625F);
	   tessellator.end();
	}

	private static void drawGlint(BufferBuilder builder, double width) {
	   double height = 0.0625F;
	   builder.vertex(0.0F, 0.0F, 0.0F).texture(width + height * (double)4.0F, height).nextVertex();
	   builder.vertex(1.0F, 0.0F, 0.0F).texture(width + height * (double)5.0F, height).nextVertex();
	   builder.vertex(1.0F, 1.0F, 0.0F).texture(width + height, 0.0F).nextVertex();
	   builder.vertex(0.0F, 1.0F, 0.0F).texture(width, 0.0F).nextVertex();
	}
}
