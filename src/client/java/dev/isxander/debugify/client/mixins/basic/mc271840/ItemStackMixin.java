/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.client.mixins.basic.mc271840;

import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@BugFix(id = "MC-271840", category = FixCategory.BASIC, env = BugFix.Env.CLIENT, description = "Weapons enchanted with Sharpness do not visually increase attack damage in tooltip")
@Mixin(ItemStack.class)
public class ItemStackMixin {
	@Unique private static final ThreadLocal<ItemStack> CURRENT_STACK = new ThreadLocal<>();

	@Inject(
			method = "addAttributeTooltips",
			at = @At("HEAD")
	)
	private void captureStack(Consumer<Component> consumer, TooltipDisplay display, Player player, CallbackInfo ci) {
		CURRENT_STACK.set((ItemStack) (Object) this);
	}

	@Inject(
			method = "addAttributeTooltips",
			at = @At("RETURN")
	)
	private void clearStack(Consumer<Component> consumer, TooltipDisplay display, Player player, CallbackInfo ci) {
		CURRENT_STACK.remove();
	}

	@ModifyArg(
			method = "lambda$addAttributeTooltips$0",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/ItemAttributeModifiers$Display;apply(Ljava/util/function/Consumer;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)V"),
			index = 3
	)
	private static AttributeModifier addSharpnessDamage(AttributeModifier modifier) {
		if (modifier.is(Item.BASE_ATTACK_DAMAGE_ID)) {
			ItemStack stack = CURRENT_STACK.get();
			if (stack != null && !stack.isEmpty()) {
				float bonus = getSharpnessBonus(stack);
				if (bonus > 0.0F) {
					return new AttributeModifier(modifier.id(), modifier.amount() + bonus, modifier.operation());
				}
			}
		}
		return modifier;
	}

	@Unique private static float getSharpnessBonus(ItemStack stack) {
		float bonus = 0.0F;
		ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
		for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
			Enchantment enchantment = entry.getKey().value();
			List<ConditionalEffect<EnchantmentValueEffect>> effects = enchantment.getEffects(EnchantmentEffectComponents.DAMAGE);
			for (ConditionalEffect<EnchantmentValueEffect> effect : effects) {
				if (effect.requirements().isEmpty()) {
					bonus = effect.effect().process(entry.getIntValue(), RandomSource.create(), bonus);
				}
			}
		}
		return bonus;
	}
}
