/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.test.suites;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class MC279288 {
	@GameTest
	public void testShieldBlocksCloseProjectile(GameTestHelper ctx) {
		ServerPlayer player = ctx.makeMockServerPlayerInLevel();
		player.setPos(ctx.absoluteVec(Vec3.atBottomCenterOf(new BlockPos(1, 1, 1))));
		player.setYHeadRot(0.0F); // Facing south (+Z)

		player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.SHIELD));
		player.startUsingItem(InteractionHand.OFF_HAND);
		try {
			java.lang.reflect.Field field = net.minecraft.world.entity.LivingEntity.class.getDeclaredField("useItemRemaining");
			field.setAccessible(true);
			field.setInt(player, player.getUseItem().getUseDuration(player) - 10);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		Arrow arrow = new Arrow(EntityTypes.ARROW, ctx.getLevel());
		// Arrow is flying into player along -Z (from south towards north), penetrated slightly past player center
		arrow.setPos(player.position().add(0, 0, -0.05));
		arrow.setDeltaMovement(new Vec3(0, 0, -1));

		DamageSource damageSource = ctx.getLevel().damageSources().arrow(arrow, null);
		float blocked = player.applyItemBlocking(ctx.getLevel(), damageSource, 10.0F);

		ctx.assertTrue(blocked > 0.0F, "Shield should successfully block projectile flying into it");
		ctx.succeed();
	}
}
