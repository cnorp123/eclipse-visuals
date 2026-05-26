package dev.eclipsevisuals.mixin;

import dev.eclipsevisuals.EclipseVisualsClient;
import dev.eclipsevisuals.config.ModConfig;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyReturnValue;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Handles:
 *   - Zoom (smooth FOV reduction when C key held)
 *   - Motion Blur (post-processing — scaffold only, shader file needed)
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    // Current zoomed FOV (lerped each frame toward target)
    private double eclipseZoomFov = -1.0;

    /**
     * Intercepts getFov() to apply smooth zoom when C key is held.
     * Target FOV comes from ModConfig.zoomFov (default 15 degrees).
     */
    @ModifyReturnValue(method = "getFov", at = @At("RETURN"))
    private double eclipseModifyFov(double original) {
        if (!ModConfig.INSTANCE.zoom || !EclipseVisualsClient.zoomActive) {
            // Smoothly lerp back to original FOV
            if (eclipseZoomFov != -1.0) {
                eclipseZoomFov = lerp(eclipseZoomFov, original, 0.35);
                if (Math.abs(eclipseZoomFov - original) < 0.5) eclipseZoomFov = -1.0;
                return eclipseZoomFov == -1.0 ? original : eclipseZoomFov;
            }
            return original;
        }
        double target = ModConfig.INSTANCE.zoomFov;
        if (eclipseZoomFov == -1.0) eclipseZoomFov = original;
        eclipseZoomFov = lerp(eclipseZoomFov, target, 0.35);
        return eclipseZoomFov;
    }

    private static double lerp(double from, double to, double factor) {
        return from + (to - from) * factor;
    }
}
