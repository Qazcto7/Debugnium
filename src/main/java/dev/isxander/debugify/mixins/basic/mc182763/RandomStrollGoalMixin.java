/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc182763;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@BugFix(id = "MC-182763", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Fish sometimes stop moving")
@Mixin(RandomStrollGoal.class)
public class RandomStrollGoalMixin {
	@ModifyExpressionValue(
			method = "canUse",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/world/entity/ai/goal/RandomStrollGoal;checkNoActionTime:Z"
			)
	)
	private boolean ignoreNoActionTimeForSwimming(boolean checkNoActionTime) {
		if ((Object) this instanceof RandomSwimmingGoal) {
			return false;
		}
		return checkNoActionTime;
	}
}
