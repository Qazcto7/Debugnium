/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc169113;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@BugFix(id = "MC-169113", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Players on respawn screen do not receive chunk updates")
@Mixin(ChunkMap.class)
public class ChunkMapMixin {
	@WrapWithCondition(
			method = "removeEntity",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ChunkMap;updatePlayerStatus(Lnet/minecraft/server/level/ServerPlayer;Z)V"
			)
	)
	private boolean shouldUpdatePlayerStatus(ChunkMap instance, ServerPlayer player, boolean track) {
		return !player.isDeadOrDying() || player.hasDisconnected();
	}
}
