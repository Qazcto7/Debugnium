/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.test.suites;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.storage.ValueInput;

import java.lang.reflect.Method;
import java.util.Optional;

public class MC17876 {
	@GameTest
	public void testHealthNotClampedOnSaveDataLoad(GameTestHelper ctx) throws Exception {
		Zombie zombie = ctx.spawnWithNoFreeWill(EntityTypes.ZOMBIE, new BlockPos(1, 1, 1));

		ValueInput input = new ValueInput() {
			@Override
			public float getFloatOr(String key, float def) {
				if ("Health".equals(key)) return 40.0f;
				return def;
			}
			@Override public <T> Optional<T> read(String key, Codec<T> codec) { return Optional.empty(); }
			@Override public <T> Optional<T> read(MapCodec<T> codec) { return Optional.empty(); }
			@Override public Optional<ValueInput> child(String key) { return Optional.empty(); }
			@Override public ValueInput childOrEmpty(String key) { return this; }
			@Override public Optional<ValueInputList> childrenList(String key) { return Optional.empty(); }
			@Override public ValueInputList childrenListOrEmpty(String key) { return null; }
			@Override public <T> Optional<TypedInputList<T>> list(String key, Codec<T> codec) { return Optional.empty(); }
			@Override public <T> TypedInputList<T> listOrEmpty(String key, Codec<T> codec) { return null; }
			@Override public boolean getBooleanOr(String key, boolean def) { return def; }
			@Override public byte getByteOr(String key, byte def) { return def; }
			@Override public int getShortOr(String key, short def) { return def; }
			@Override public Optional<Integer> getInt(String key) { return Optional.empty(); }
			@Override public int getIntOr(String key, int def) { return def; }
			@Override public long getLongOr(String key, long def) { return def; }
			@Override public Optional<Long> getLong(String key) { return Optional.empty(); }
			@Override public double getDoubleOr(String key, double def) { return def; }
			@Override public Optional<String> getString(String key) { return Optional.empty(); }
			@Override public String getStringOr(String key, String def) { return def; }
			@Override public Optional<int[]> getIntArray(String key) { return Optional.empty(); }
			@Override public HolderLookup.Provider lookup() { return ctx.getLevel().registryAccess(); }
		};

		Method readMethod = LivingEntity.class.getDeclaredMethod("readAdditionalSaveData", ValueInput.class);
		readMethod.setAccessible(true);
		readMethod.invoke(zombie, input);

		ctx.assertTrue(zombie.getHealth() == 40.0f, "Health loaded from save data should not be clamped to base max health");
		ctx.succeed();
	}
}
