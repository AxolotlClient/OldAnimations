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

package io.github.axolotlclient.oldanimations.mixin;

import io.github.axolotlclient.oldanimations.OldAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

	@Shadow
	private Minecraft minecraft;

	@Unique
	private float eyeHeightSubtractor;
	@Unique
	private long lastEyeHeightUpdate;

	@ModifyVariable(method = "transformCamera", at = @At(value = "STORE"), ordinal = 1)
	private float oldanimations$modifyEyeHeight(float eyeHeight) {

		Entity entity = this.minecraft.getCamera();

		if (OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().sneaking.get()) {
			float height = eyeHeight;
			if (entity.isSneaking()) {
				height += 0.08F;
			}
			float actualEyeHeightSubtractor = entity.isSneaking() ? 0.08F : 0;
			long sinceLastUpdate = System.currentTimeMillis() - lastEyeHeightUpdate;
			lastEyeHeightUpdate = System.currentTimeMillis();
			if (actualEyeHeightSubtractor > eyeHeightSubtractor) {
				eyeHeightSubtractor += sinceLastUpdate / 500f;
				if (actualEyeHeightSubtractor < eyeHeightSubtractor) {
					eyeHeightSubtractor = actualEyeHeightSubtractor;
				}
			} else if (actualEyeHeightSubtractor < eyeHeightSubtractor) {
				eyeHeightSubtractor -= sinceLastUpdate / 500f;
				if (actualEyeHeightSubtractor > eyeHeightSubtractor) {
					eyeHeightSubtractor = actualEyeHeightSubtractor;
				}
			}
			return height - eyeHeightSubtractor;
		}
		return entity.getEyeHeight();
	}
}
