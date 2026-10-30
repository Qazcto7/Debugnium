/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc297196;

import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@BugFix(id = "MC-297196", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Knockback is synchronized to client with inconsistent delay")
@Mixin(ServerLevel.class)
public class ServerLevelMixin {
	@Inject(
			method = "tick",
			at = @At("TAIL")
	)
	private void tickChunkMapAfterEntities(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		ServerLevel level = (ServerLevel) (Object) this;
		((ChunkMapAccessor) level.getChunkSource().chunkMap).debugify$tick();
	}
}
