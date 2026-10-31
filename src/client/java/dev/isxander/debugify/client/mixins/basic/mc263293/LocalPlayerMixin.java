/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.client.mixins.basic.mc263293;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@BugFix(id = "MC-263293", category = FixCategory.BASIC, env = BugFix.Env.CLIENT, description = "Dying causes toggle sprint and toggle sneak to untoggle")
@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
	@WrapOperation(
			method = "respawn",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/KeyMapping;resetToggleKeys()V"
			)
	)
	private void preventResetToggleKeysOnRespawn(Operation<Void> original) {
		// Preserve sprint and sneak toggle state across respawn
	}
}
