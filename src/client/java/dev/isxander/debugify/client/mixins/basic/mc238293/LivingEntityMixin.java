/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.client.mixins.basic.mc238293;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@BugFix(id = "MC-238293", category = FixCategory.BASIC, env = BugFix.Env.CLIENT, description = "The animation of several actions is sometimes not displayed for other players")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> type, Level level) {
		super(type, level);
	}

	@Shadow
	protected ItemStack useItem;
	@Shadow
	protected int useItemRemaining;
	@Shadow
	public abstract boolean isUsingItem();
	@Shadow
	public abstract InteractionHand getUsedItemHand();
	@Shadow
	public abstract ItemStack getItemInHand(InteractionHand hand);

	@WrapMethod(method = "getUseItem")
	private ItemStack fixEmptyUseItemWhenUsing(Operation<ItemStack> original) {
		ItemStack stack = original.call();
		if (stack.isEmpty() && this.level().isClientSide() && this.isUsingItem()) {
			ItemStack handItem = this.getItemInHand(this.getUsedItemHand());
			if (!handItem.isEmpty()) {
				this.useItem = handItem;
				this.useItemRemaining = handItem.getUseDuration((LivingEntity) (Object) this);
				return handItem;
			}
		}
		return stack;
	}

	@Inject(method = "setItemSlot", at = @At("TAIL"))
	private void onSetItemSlot(EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
		if (this.level().isClientSide() && this.isUsingItem() && this.useItem.isEmpty()) {
			InteractionHand hand = this.getUsedItemHand();
			if ((slot == EquipmentSlot.MAINHAND && hand == InteractionHand.MAIN_HAND) || (slot == EquipmentSlot.OFFHAND && hand == InteractionHand.OFF_HAND)) {
				this.useItem = stack;
				this.useItemRemaining = stack.getUseDuration((LivingEntity) (Object) this);
			}
		}
	}
}
