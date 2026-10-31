/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc248335;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.network.Connection;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@BugFix(id = "MC-248335", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "The handleDisconnection() method is called twice when players are forcibly removed")
@Mixin(Connection.class)
public class ConnectionMixin {
	@WrapOperation(
			method = "handleDisconnection",
			at = @At(
					value = "INVOKE",
					target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;)V"
			)
	)
	private void suppressDuplicateWarning(Logger instance, String message, Operation<Void> original) {
		// Suppress warning if called twice
	}
}
