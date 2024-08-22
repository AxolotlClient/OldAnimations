package io.github.axolotlclient.oldanimations.mixin;

import io.github.axolotlclient.oldanimations.OldAnimations;
import io.github.axolotlclient.oldanimations.ducks.Sneaky;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(FishingBobberEntityRenderer.class)
public class FishingBobberEntityRendererMixin {

	@ModifyArgs(method = "render(Lnet/minecraft/entity/projectile/FishingBobberEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;<init>(DDD)V"))
	private void axolotlclient$modifyLinePosition(Args args) {
		if (isRodEnabled()) {
			/* original values from 1.7 */
			args.set(0, (double) args.get(0) - 0.24D);
			args.set(2, (double) args.get(2) + 0.45D);
		}
	}

	@ModifyConstant(method = "render(Lnet/minecraft/entity/projectile/FishingBobberEntity;DDDFF)V", constant = @Constant(doubleValue = 0.8D))
	public double axolotlclient$moveLinePosition(double constant) {
		/* original value from 1.7 */
		if (isRodEnabled()) constant += 0.05D;
		return constant;
	}

	@Redirect(method = "render(Lnet/minecraft/entity/projectile/FishingBobberEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSneaking()Z"))
	public boolean axolotlclient$removeSneakTranslation(PlayerEntity instance) {
		return !isRodEnabled() && instance.isSneaking();
	}

	@Redirect(method = "render(Lnet/minecraft/entity/projectile/FishingBobberEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getEyeHeight()F"))
	public float axolotlclient$useLerpEyeHeight_Fish(PlayerEntity instance) {
		if (OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().rod.get()) {
			return ((Sneaky) MinecraftClient.getInstance().gameRenderer).axolotlclient$getEyeHeight();
		}
		return instance.getEyeHeight();
	}

	@Unique
	private static boolean isRodEnabled() {
		return OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().rod.get();
	}
}
