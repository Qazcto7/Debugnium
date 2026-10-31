/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.client.mixins.basic.mc262974;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Hud;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@BugFix(id = "MC-262974", category = FixCategory.BASIC, env = BugFix.Env.CLIENT, description = "Held item tooltips overlap the armor bar when player has the Absorption effect")
@Mixin(Hud.class)
public abstract class HudMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow
	private int displayHealth;

	@Shadow
	protected abstract Player getCameraPlayer();

	@Shadow
	protected abstract LivingEntity getPlayerVehicleWithHealth();

	@Shadow
	protected abstract int getVehicleMaxHearts(LivingEntity vehicle);

	@Shadow
	protected abstract int getVisibleVehicleHeartRows(int vehicleHealth);

	@ModifyExpressionValue(method = "extractSelectedItemName", at = @At(value = "CONSTANT", args = "intValue=59"))
	private int adjustY(int original) {
		if (this.minecraft.gameMode != null && this.minecraft.gameMode.canHurtPlayer()) {
			Player player = this.getCameraPlayer();
			if (player != null) {
				float maxHealth = (float) player.getAttributeValue(Attributes.MAX_HEALTH);
				float displayedHealth = Math.max(Math.max((float) this.displayHealth, (float) Mth.ceil(player.getHealth())), maxHealth);
				int absorption = Mth.ceil(player.getAbsorptionAmount());
				int heartRows = Mth.ceil((displayedHealth + absorption) / 2.0F / 10.0F);
				int rowHeight = Math.max(10 - (heartRows - 2), 3);
				int leftSideOffset = heartRows > 1 ? (heartRows - 1) * rowHeight : 0;

				LivingEntity vehicle = this.getPlayerVehicleWithHealth();
				int vehicleOffset = 0;
				if (vehicle != null) {
					int vehicleMaxHearts = this.getVehicleMaxHearts(vehicle);
					int vehicleRows = this.getVisibleVehicleHeartRows(vehicleMaxHearts);
					if (vehicleRows > 1) {
						vehicleOffset = (vehicleRows - 1) * 10;
					}
				}

				return original + Math.max(leftSideOffset, vehicleOffset);
			}
		}
		return original;
	}
}
