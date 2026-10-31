/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.client.mixins.basic.mc207091;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.NativeImage;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import java.io.IOException;
import java.nio.file.Path;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@BugFix(id = "MC-207091", category = FixCategory.BASIC, env = BugFix.Env.CLIENT, description = "Screenshots use alpha channel despite not encoding any transparent pixels")
@Mixin(NativeImage.class)
public class NativeImageMixin {
	@WrapOperation(
			method = "writeToFile(Ljava/io/File;)V",
			at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/NativeImage;writeToFile(Ljava/nio/file/Path;)V")
	)
	private void stripAlphaOnScreenshot(NativeImage image, Path path, Operation<Void> original) throws IOException {
		if (image.format() == NativeImage.Format.RGBA && path.getParent() != null && path.getParent().endsWith("screenshots")) {
			try (NativeImage rgbImage = new NativeImage(NativeImage.Format.RGB, image.getWidth(), image.getHeight(), false)) {
				long srcPtr = image.getPointer();
				long dstPtr = rgbImage.getPointer();
				long numPixels = (long) image.getWidth() * image.getHeight();
				for (long i = 0; i < numPixels; i++) {
					long srcOffset = i * 4;
					long dstOffset = i * 3;
					MemoryUtil.memPutByte(dstPtr + dstOffset, MemoryUtil.memGetByte(srcPtr + srcOffset));
					MemoryUtil.memPutByte(dstPtr + dstOffset + 1, MemoryUtil.memGetByte(srcPtr + srcOffset + 1));
					MemoryUtil.memPutByte(dstPtr + dstOffset + 2, MemoryUtil.memGetByte(srcPtr + srcOffset + 2));
				}
				rgbImage.writeToFile(path);
				return;
			}
		}
		original.call(image, path);
	}
}
