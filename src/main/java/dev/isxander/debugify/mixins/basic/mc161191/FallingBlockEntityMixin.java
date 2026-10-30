/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc161191;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@BugFix(id = "MC-161191", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Falling Block entity does not evaluate block loot table when being broken")
@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin {
	@WrapOperation(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/item/FallingBlockEntity;spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"
			)
	)
	private ItemEntity spawnLootTableDrops(FallingBlockEntity instance, ServerLevel level, ItemLike item, Operation<ItemEntity> original) {
		BlockState state = instance.getBlockState();
		BlockEntity blockEntity = null;
		if (instance.blockData != null && state.hasBlockEntity()) {
			try {
				blockEntity = BlockEntity.loadStatic(instance.blockPosition(), state, instance.blockData, level.registryAccess());
			} catch (Exception ignored) {
			}
		}

		List<ItemStack> drops = Block.getDrops(state, level, instance.blockPosition(), blockEntity);
		ItemEntity lastItemEntity = null;
		for (ItemStack drop : drops) {
			lastItemEntity = instance.spawnAtLocation(level, drop);
		}
		return lastItemEntity;
	}
}
