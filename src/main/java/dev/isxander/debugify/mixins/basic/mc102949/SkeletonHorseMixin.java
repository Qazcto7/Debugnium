/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc102949;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.world.entity.animal.equine.SkeletonHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@BugFix(id = "MC-102949", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Can't interact with skeleton horses spawned from spawn eggs or commands")
@Mixin(SkeletonHorse.class)
public class SkeletonHorseMixin {
	@WrapOperation(
			method = "mobInteract",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/animal/equine/SkeletonHorse;isTamed()Z"
			)
	)
	private boolean allowUntamedInteractionIfNotTrap(SkeletonHorse instance, Operation<Boolean> original) {
		return !instance.isTrap() || original.call(instance);
	}
}
