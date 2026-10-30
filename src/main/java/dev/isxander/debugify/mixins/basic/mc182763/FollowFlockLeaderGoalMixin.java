/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc182763;

import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.world.entity.ai.goal.FollowFlockLeaderGoal;
import net.minecraft.world.entity.animal.fish.AbstractSchoolingFish;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@BugFix(id = "MC-182763", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Fish sometimes stop moving")
@Mixin(FollowFlockLeaderGoal.class)
public class FollowFlockLeaderGoalMixin {
	@ModifyArg(
			method = "canUse",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;",
					ordinal = 1
			)
	)
	private Predicate<AbstractSchoolingFish> preventLeaderBecomingFollower(Predicate<AbstractSchoolingFish> predicate) {
		return predicate.and(Predicate.not(AbstractSchoolingFish::hasFollowers));
	}
}
