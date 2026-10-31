/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc225908;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@BugFix(id = "MC-225908", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Piglins and piglin brutes still attack wither skeletons and withers on the same team")
@Mixin(PiglinAi.class)
public abstract class PiglinAiMixin {
	@WrapOperation(
			method = "findNearestValidAttackTarget",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/ai/Brain;getMemory(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;)Ljava/util/Optional;"
			)
	)
	private static <U> Optional<U> filterAlliedNemesis(
			Brain<?> brain,
			MemoryModuleType<U> memoryType,
			Operation<Optional<U>> original,
			ServerLevel level,
			Piglin piglin
	) {
		Optional<U> memory = original.call(brain, memoryType);
		if (memoryType == MemoryModuleType.NEAREST_VISIBLE_NEMESIS && memory.isPresent()) {
			if (memory.get() instanceof LivingEntity living && piglin.isAlliedTo(living)) {
				return Optional.empty();
			}
		}
		return memory;
	}
}
