/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.client.mixins.basic.mc12289;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@BugFix(id = "MC-12289", category = FixCategory.BASIC, env = BugFix.Env.CLIENT, description = "Multiplayer server list resets scroll when refreshed")
@Mixin(JoinMultiplayerScreen.class)
public abstract class JoinMultiplayerScreenMixin {
	@Shadow
	protected ServerSelectionList serverSelectionList;

	@WrapOperation(
			method = "refreshServerList",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/Gui;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"
			)
	)
	private void preserveScrollOnRefresh(Gui gui, Screen newScreen, Operation<Void> original) {
		double scrollAmount = this.serverSelectionList != null ? this.serverSelectionList.scrollAmount() : 0.0;
		original.call(gui, newScreen);
		if (newScreen instanceof JoinMultiplayerScreen joinScreen) {
			ServerSelectionList newList = ((JoinMultiplayerScreenAccessor) joinScreen).getServerSelectionList();
			if (newList != null) {
				newList.setScrollAmount(scrollAmount);
			}
		}
	}
}
