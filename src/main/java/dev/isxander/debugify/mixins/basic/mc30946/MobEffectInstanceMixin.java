/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc30946;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@BugFix(id = "MC-30946", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Beacon regeneration speed is reduced compared to normal speed")
@Mixin(MobEffectInstance.class)
public class MobEffectInstanceMixin {
	@Shadow
	private int duration;

	@Shadow
	private int amplifier;

	@Shadow
	@Final
	private Holder<MobEffect> effect;

	@Inject(method = "update", at = @At("HEAD"))
	private void captureOldDuration(MobEffectInstance other, CallbackInfoReturnable<Boolean> cir, @Share("oldDuration") LocalIntRef oldDuration) {
		oldDuration.set(this.duration);
	}

	@Inject(method = "update", at = @At("RETURN"))
	private void preservePeriodicPhase(MobEffectInstance other, CallbackInfoReturnable<Boolean> cir, @Share("oldDuration") LocalIntRef oldDuration) {
		if (oldDuration.get() > 0 && other.getAmplifier() == this.amplifier) {
			int interval = 0;
			if (this.effect.is(MobEffects.REGENERATION)) {
				interval = 50 >> this.amplifier;
			} else if (this.effect.is(MobEffects.POISON)) {
				interval = 25 >> this.amplifier;
			} else if (this.effect.is(MobEffects.WITHER)) {
				interval = 40 >> this.amplifier;
			}

			if (interval > 0) {
				int currentPhase = oldDuration.get() % interval;
				int targetPhase = this.duration % interval;
				int diff = (currentPhase - targetPhase + interval) % interval;
				this.duration += diff;
			}
		}
	}
}
