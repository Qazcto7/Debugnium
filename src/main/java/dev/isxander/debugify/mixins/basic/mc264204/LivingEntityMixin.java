/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc264204;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@BugFix(id = "MC-264204", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Advancement criteria and statistics for deaths caused by goats are not triggered")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@WrapOperation(
			method = "resolveMobResponsibleForDamage",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z",
					ordinal = 0
			)
	)
	private boolean skipNoAngerCheckForPlayer(DamageSource source, TagKey<DamageType> tag, Operation<Boolean> original) {
		if ((Object) this instanceof ServerPlayer) {
			return false;
		}
		return original.call(source, tag);
	}
}
