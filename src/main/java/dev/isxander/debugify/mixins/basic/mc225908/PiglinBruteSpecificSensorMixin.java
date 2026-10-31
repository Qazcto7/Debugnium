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
import net.minecraft.world.entity.ai.sensing.PiglinBruteSpecificSensor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@BugFix(id = "MC-225908", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Piglins and piglin brutes still attack wither skeletons and withers on the same team")
@Mixin(PiglinBruteSpecificSensor.class)
public abstract class PiglinBruteSpecificSensorMixin {
	@WrapOperation(
			method = "doTick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/ai/Brain;setMemory(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;Ljava/util/Optional;)V"
			)
	)
	private <U> void ignoreAlliedNemesis(
			Brain<?> brain,
			MemoryModuleType<U> memoryType,
			Optional<U> value,
			Operation<Void> original,
			ServerLevel level,
			LivingEntity entity
	) {
		if (memoryType == MemoryModuleType.NEAREST_VISIBLE_NEMESIS && value.isPresent()) {
			if (value.get() instanceof LivingEntity target && entity.isAlliedTo(target)) {
				value = Optional.empty();
			}
		}
		original.call(brain, memoryType, value);
	}
}
