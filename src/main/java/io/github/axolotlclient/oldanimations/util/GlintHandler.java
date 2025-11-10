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

package io.github.axolotlclient.oldanimations.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tessellator;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.TextureAtlas;
import net.minecraft.client.resource.model.BakedModel;
import net.minecraft.client.resource.model.BasicBakedModel;
import net.minecraft.resource.Identifier;

import java.util.HashMap;

public final class GlintHandler {

	private static final HashMap<HashedModel, BakedModel> glintMap = new HashMap<>();

	/* custom glint model */
	public static BakedModel getModel(BakedModel model) {
		/* because we're creating new bakedmodels for the sole purpose of recreating the 1.7 enchantment glint, */
		/* we should reuse the common glint bakedmodels to reduce any crazy memory usage */
		/* redth is my hero */
		return glintMap.computeIfAbsent(
			new HashedModel(model), key ->
				new BasicBakedModel.Builder(model, CustomTextureAtlasSprite.INSTANCE).build()
		);
	}

	public static void renderEnchantmentGlintPre(Identifier glintTexture, int color) {
		GlStateManager.enableRescaleNormal();
	    GlStateManager.depthFunc(518);
	    GlStateManager.disableLighting();
	    GlStateManager.depthMask(false);
	    Minecraft.getInstance().getTextureManager().bind(glintTexture);
	    GlStateManager.enableAlphaTest();
	    GlStateManager.alphaFunc(516, 0.1F);
	    GlStateManager.enableBlend();
	    GlStateManager.blendFuncSeparate(772, 1, 0, 0);

	    /* the glint color of the gui in 1.7 is what is used for the glint color in 1.8 coincidentally */
		/* might as well add this override option anyway */
	    if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldGlintColor.get()) {
		    GlStateManager.color4f(0.5F, 0.25F, 0.8F, 1.0F);
	    } else {
			float a = (float) (color >> 24 & 0xFF) / 255.0f;
			float r = (float) (color >> 16 & 0xFF) / 255.0f;
			float g = (float) (color >> 8 & 0xFF) / 255.0f;
			float b = (float) (color & 0xFF) / 255.0f;
		    GlStateManager.color4f(r, g, b, a);
	    }

	    GlStateManager.pushMatrix();
	}

    public static void renderEnchantmentGlintPost() {
		/* values used to adapt 1.8 sprite rendering to 1.7's position */
		GlStateManager.scalef(0.5F, 0.5F, 0.5F);
		GlStateManager.translatef(0.0F, -0.25F, 0.0F);

		/* because of how models work, using a model to render the 1.7 gui glint will not work. it will be frozen :( */
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder builder = tessellator.getBuilder();
		builder.begin(7, DefaultVertexFormat.POSITION_TEX);
		/* goodbye for loop. hello inlined functions */
		drawGlint(builder, (float) (Minecraft.getTime() % 3000L) / 3000.0F, 4.0F);
		drawGlint(builder, (float) (Minecraft.getTime() % 4873L) / 4873.0F, -1.0F);
		tessellator.end();

      	GlStateManager.popMatrix();
      	GlStateManager.blendFuncSeparate(770, 771, 1, 0);
      	GlStateManager.depthMask(true);
      	GlStateManager.enableLighting();
      	GlStateManager.depthFunc(515);
      	GlStateManager.disableAlphaTest();
      	GlStateManager.disableRescaleNormal();
      	GlStateManager.disableLighting();
		Minecraft.getInstance().getTextureManager().bind(TextureAtlas.BLOCKS_LOCATION);
    }

	private static void drawGlint(BufferBuilder builder, float speed, float skew) {
		/* sprite rendering taken from FireballRenderer#render */
		/* the values are adapted from 1.7. they are simplified heavily. a lot of math refactoring wooo */
		float dimension = 20.0F / 256.0F;
		builder.vertex(-0.5, -0.25, 0.0).texture(speed + dimension * skew, dimension).nextVertex();
		builder.vertex(0.5, -0.25, 0.0).texture(speed + dimension + dimension * skew, dimension).nextVertex();
		builder.vertex(0.5, 0.75, 0.0).texture(speed + dimension, 0.0F).nextVertex();
		builder.vertex(-0.5, 0.75, 0.0).texture(speed, 0.0F).nextVertex();
	}
}
