package io.github.axolotlclient.oldanimations.mixin;

import net.minecraft.client.render.entity.model.BiPedModel;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiPedModel.class)
public abstract class BiPedModelMixin {

    @Shadow
	public ModelPart rightArm;

    @Inject(
		method = "setAngles",
		at = @At(
			value = "FIELD",
			opcode = Opcodes.PUTFIELD,
			target = "Lnet/minecraft/client/render/model/ModelPart;posY:F",
			shift = At.Shift.AFTER
		),
		slice = @Slice(
			from = @At(
				value = "FIELD",
				opcode = Opcodes.GETFIELD,
				target = "Lnet/minecraft/client/render/entity/model/BiPedModel;rightArmPose:I",
				ordinal = 0
			),
			to = @At(
				value = "FIELD",
				opcode = Opcodes.GETFIELD,
				target = "Lnet/minecraft/client/render/entity/model/BiPedModel;rightArmPose:I",
				ordinal = 2
			)
		)
    )
    private void axolotlclient$oldArmPosition(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn, CallbackInfo ci) {
		rightArm.posY = 0.0f;
    }
}
