/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc297196;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@BugFix(id = "MC-297196", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Knockback is synchronized to client with inconsistent delay")
@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin {
	@WrapOperation(
			method = "tick(Ljava/util/function/BooleanSupplier;Z)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ChunkMap;tick()V"
			)
	)
	private void cancelPreEntityChunkMapTick(ChunkMap instance, Operation<Void> original) {
		// Suppressed so ChunkMap.tick runs after entities tick in ServerLevel.tick
	}
}
