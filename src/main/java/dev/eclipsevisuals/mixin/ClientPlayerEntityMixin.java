package dev.eclipsevisuals.mixin;

import dev.eclipsevisuals.config.ModConfig;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Implements Auto Sprint by forcing sprinting when moving forward.
 */
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void eclipseAutoSprint(CallbackInfo ci) {
        if (!ModConfig.INSTANCE.autoSprint) return;
        ClientPlayerEntity self = (ClientPlayerEntity) (Object) this;
        // Sprint if holding forward and not in liquid, sneaking, or exhausted
        if (self.input != null && self.input.playerInput.forward()
                && !self.isSneaking()
                && !self.isUsingItem()
                && self.getHungerManager().getFoodLevel() > 6) {
            self.setSprinting(true);
        }
    }
}
