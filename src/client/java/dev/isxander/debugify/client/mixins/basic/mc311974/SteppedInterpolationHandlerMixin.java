/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.client.mixins.basic.mc311974;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.world.entity.SteppedInterpolationHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@BugFix(id = "MC-311974", category = FixCategory.BASIC, env = BugFix.Env.CLIENT, description = "Entity interpolation causes visual stutter due to missing buffer tick")
@Mixin(SteppedInterpolationHandler.class)
public class SteppedInterpolationHandlerMixin {
	@ModifyExpressionValue(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/EntityType;updateInterval()I"
			)
	)
	private static int increaseInterpolationSteps(int original) {
		return original + 1;
	}
}
