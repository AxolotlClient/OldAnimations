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

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.client.render.model.block.entity.HumanoidSkullModel;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HumanoidSkullModel.class)
public class HumanoidSkullModelMixin {
	/* 1.7 skulls don't have the hat layer :P */

	@WrapWithCondition(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelPart;addBox(FFFIIIF)V"))
	private boolean axolotlclient$disableHatLayer(ModelPart instance, float f, float g, float h, int i, int j, int k, float l) {
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.skullBlockRendering.get();
	}

	@WrapWithCondition(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelPart;setPos(FFF)V"))
	private boolean axolotlclient$disableHatLayer2(ModelPart instance, float f, float g, float h) {
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.skullBlockRendering.get();
	}

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelPart;render(F)V"))
	private boolean axolotlclient$disableHatLayer3(ModelPart instance, float f) {
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.skullBlockRendering.get();
	}

	@WrapWithCondition(method = "setupAnimation", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/render/model/ModelPart;rotationY:F"))
	private boolean axolotlclient$disableHatLayer4(ModelPart instance, float value) {
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.skullBlockRendering.get();
	}

	@WrapWithCondition(method = "setupAnimation", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/render/model/ModelPart;rotationX:F"))
	private boolean axolotlclient$disableHatLayer5(ModelPart instance, float value) {
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.skullBlockRendering.get();
	}
}
