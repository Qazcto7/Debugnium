/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc123848;

import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@BugFix(id = "MC-123848", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Item frames (and items within) when removed from a ceiling, drop atop, not under, the block")
@Mixin(ItemFrame.class)
public abstract class ItemFrameMixin extends HangingEntity {
	protected ItemFrameMixin(EntityType<? extends HangingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public ItemEntity spawnAtLocation(ServerLevel level, ItemStack stack) {
		return this.spawnAtLocation(level, stack, this.getDirection() == Direction.DOWN ? -0.6F : 0.0F);
	}
}
