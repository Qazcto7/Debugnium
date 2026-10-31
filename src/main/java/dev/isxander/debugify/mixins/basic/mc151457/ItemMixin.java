/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc151457;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.SolidBucketItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {
	@Shadow @Nullable private ItemStackTemplate craftingRemainingItem;

	@Unique private static ItemStackTemplate debugify$bucketRemainder;

	@Inject(method = "getCraftingRemainder", at = @At("HEAD"), cancellable = true)
	private void getBucketCraftingRemainder(CallbackInfoReturnable<ItemStackTemplate> cir) {
		if (this.craftingRemainingItem == null && ((Object) this instanceof MobBucketItem || (Object) this instanceof SolidBucketItem)) {
			if (debugify$bucketRemainder == null) {
				debugify$bucketRemainder = new ItemStackTemplate(Items.BUCKET);
			}
			cir.setReturnValue(debugify$bucketRemainder);
		}
	}
}
