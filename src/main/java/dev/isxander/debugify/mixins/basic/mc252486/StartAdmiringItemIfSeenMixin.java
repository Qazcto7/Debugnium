/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc252486;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.StartAdmiringItemIfSeen;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@BugFix(id = "MC-252486", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Piglin retreat/admiring sound can play excessively")
@Mixin(StartAdmiringItemIfSeen.class)
public class StartAdmiringItemIfSeenMixin {
	@WrapOperation(
			method = "lambda$create$2",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;isLovedItem(Lnet/minecraft/world/item/ItemStack;)Z")
	)
	private static boolean checkLovedAndDistance(ItemStack stack, Operation<Boolean> original, @Local(argsOnly = true) LivingEntity entity, @Local ItemEntity itemEntity) {
		if (!original.call(stack)) {
			return false;
		}
		return itemEntity.closerThan(entity, 9.0);
	}
}
