/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc248335;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerConnectionListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@BugFix(id = "MC-248335", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "The handleDisconnection() method is called twice when players are forcibly removed")
@Mixin(ServerConnectionListener.class)
public class ServerConnectionListenerMixin {
	@WrapWithCondition(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/network/Connection;handleDisconnection()V"
			)
	)
	private boolean shouldHandleDisconnection(Connection connection) {
		return !((ConnectionAccessor) connection).debugify$isDisconnectionHandled();
	}
}
