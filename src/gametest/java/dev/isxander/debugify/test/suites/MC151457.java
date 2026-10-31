/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.test.suites;

import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;

public class MC151457 {
	@GameTest
	public void testBucketCraftingRemainders(GameTestHelper helper) {
		ItemStackTemplate powderSnowRemainder = Items.POWDER_SNOW_BUCKET.getCraftingRemainder();
		if (powderSnowRemainder == null || powderSnowRemainder.item().value() != Items.BUCKET) {
			helper.fail("POWDER_SNOW_BUCKET does not have BUCKET crafting remainder");
		}

		ItemStackTemplate codBucketRemainder = Items.COD_BUCKET.getCraftingRemainder();
		if (codBucketRemainder == null || codBucketRemainder.item().value() != Items.BUCKET) {
			helper.fail("COD_BUCKET does not have BUCKET crafting remainder");
		}

		ItemStackTemplate axolotlBucketRemainder = Items.AXOLOTL_BUCKET.getCraftingRemainder();
		if (axolotlBucketRemainder == null || axolotlBucketRemainder.item().value() != Items.BUCKET) {
			helper.fail("AXOLOTL_BUCKET does not have BUCKET crafting remainder");
		}

		ItemStackTemplate pufferfishRemainder = Items.PUFFERFISH_BUCKET.getCraftingRemainder();
		if (pufferfishRemainder == null || pufferfishRemainder.item().value() != Items.BUCKET) {
			helper.fail("PUFFERFISH_BUCKET does not have BUCKET crafting remainder");
		}

		CraftingInput input = CraftingInput.of(2, 1, List.of(
			new ItemStack(Items.POWDER_SNOW_BUCKET),
			new ItemStack(Items.COD_BUCKET)
		));

		NonNullList<ItemStack> remaining = CraftingRecipe.defaultCraftingReminder(input);
		if (remaining.size() != 2 || remaining.get(0).getItem() != Items.BUCKET || remaining.get(1).getItem() != Items.BUCKET) {
			helper.fail("CraftingRecipe did not return buckets as remaining items");
		}

		helper.succeed();
	}
}
