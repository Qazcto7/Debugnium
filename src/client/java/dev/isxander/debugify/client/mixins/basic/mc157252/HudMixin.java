/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.client.mixins.basic.mc157252;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@BugFix(id = "MC-157252", category = FixCategory.BASIC, env = BugFix.Env.CLIENT, description = "Crosshair is not centered")
@Mixin(Hud.class)
public class HudMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@WrapOperation(
			method = "extractRenderState",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Hud;extractCrosshair(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V")
	)
	private void centerCrosshair(Hud instance, GuiGraphicsExtractor extractor, DeltaTracker deltaTracker, Operation<Void> original) {
		float guiScale = (float) this.minecraft.getWindow().getGuiScale();
		float exactCenterX = (this.minecraft.getWindow().getWidth() / guiScale) / 2.0F;
		float exactCenterY = (this.minecraft.getWindow().getHeight() / guiScale) / 2.0F;
		float dx = (Math.round((exactCenterX - 7.5F) * guiScale) / guiScale) - (int) ((extractor.guiWidth() - 15) / 2);
		float dy = (Math.round((exactCenterY - 7.5F) * guiScale) / guiScale) - (int) ((extractor.guiHeight() - 15) / 2);

		extractor.pose().pushMatrix();
		extractor.pose().translate(dx, dy);
		original.call(instance, extractor, deltaTracker);
		extractor.pose().popMatrix();
	}
}
