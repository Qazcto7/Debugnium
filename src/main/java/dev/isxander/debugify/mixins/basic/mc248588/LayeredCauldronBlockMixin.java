/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc248588;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@BugFix(id = "MC-248588", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "The \"mobGriefing\" gamerule doesn't prevent burning entities from being able to decrease the levels of water or powder snow cauldrons")
@Mixin(LayeredCauldronBlock.class)
public class LayeredCauldronBlockMixin {
	@WrapOperation(
			method = "lambda$entityInside$0",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;mayInteract(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)Z")
	)
	private boolean checkMobGriefing(Entity instance, ServerLevel level, BlockPos pos, Operation<Boolean> original) {
		return (instance instanceof Player || level.getGameRules().get(GameRules.MOB_GRIEFING)) && original.call(instance, level, pos);
	}
}
