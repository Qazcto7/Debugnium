/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.client.mixins.basic.mc132067;

import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BubbleColumnUpParticle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@BugFix(id = "MC-132067", category = FixCategory.BASIC, env = BugFix.Env.CLIENT, description = "Bubble pop sounds and particles aren't produced at the top of bubble columns")
@Mixin(BubbleColumnUpParticle.class)
public abstract class BubbleColumnUpParticleMixin extends SingleQuadParticle {
	protected BubbleColumnUpParticleMixin(ClientLevel clientLevel, double d, double e, double f, TextureAtlasSprite textureAtlasSprite) {
		super(clientLevel, d, e, f, textureAtlasSprite);
	}

	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/particle/BubbleColumnUpParticle;remove()V"
			)
	)
	private void onBubblePop(CallbackInfo ci) {
		this.level.addParticle(ParticleTypes.BUBBLE_POP, this.x, this.y, this.z, this.xd, this.yd, this.zd);
		if (this.random.nextFloat() < 0.05F) {
			this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.BLOCKS, 0.5F, 1.0F, false);
		}
	}
}
