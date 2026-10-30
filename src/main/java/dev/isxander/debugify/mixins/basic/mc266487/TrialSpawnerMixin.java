/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc266487;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@BugFix(id = "MC-266487", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Gamerule doMobSpawning false disables trial spawners")
@Mixin(TrialSpawner.class)
public class TrialSpawnerMixin {
	@ModifyExpressionValue(
			method = "canSpawnInLevel",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;",
					ordinal = 1
			)
	)
	private Object ignoreDoMobSpawning(Object original) {
		return true;
	}
}
