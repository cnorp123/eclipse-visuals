package dev.eclipsevisuals.mixin;

import dev.eclipsevisuals.config.ModConfig;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Implements Full Bright by overriding the lightmap with maximum brightness.
 */
@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {

    @Shadow private NativeImageBackedTexture texture;
    @Shadow private boolean dirty;

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void eclipseFullBright(float delta, CallbackInfo ci) {
        if (!ModConfig.INSTANCE.fullBright) return;

        NativeImage image = this.texture.getImage();
        if (image == null) return;

        // Fill entire lightmap (16x16) with ARGB 0xFFFFFFFF (max brightness)
        for (int sky = 0; sky < 16; sky++) {
            for (int block = 0; block < 16; block++) {
                image.setColorArgb(block, sky, 0xFFFFFFFF);
            }
        }

        this.texture.upload();
        this.dirty = false;
        ci.cancel();
    }
}
