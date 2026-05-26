package dev.eclipsevisuals;

import dev.eclipsevisuals.config.ConfigLoader;
import dev.eclipsevisuals.config.ModConfig;
import dev.eclipsevisuals.gui.EclipseScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EclipseVisualsClient implements ClientModInitializer {

    public static final String MOD_ID = "eclipse-visuals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static KeyBinding OPEN_MENU_KEY;
    public static KeyBinding ZOOM_KEY;

    /** Incremented each tick while right mouse is held (used for zoom smooth lerp). */
    public static boolean zoomActive = false;

    @Override
    public void onInitializeClient() {
        ConfigLoader.load();

        // Register keybindings
        OPEN_MENU_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.eclipse-visuals.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.eclipse-visuals"
        ));

        ZOOM_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.eclipse-visuals.zoom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "category.eclipse-visuals"
        ));

        // Tick handler — open/close screen and handle zoom
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Open/close Eclipse Visuals menu
            while (OPEN_MENU_KEY.wasPressed()) {
                if (client.currentScreen instanceof EclipseScreen) {
                    client.setScreen(null);
                } else if (client.currentScreen == null) {
                    client.setScreen(new EclipseScreen());
                }
            }

            // Zoom key state
            if (ModConfig.INSTANCE.zoom) {
                zoomActive = InputUtil.isKeyPressed(
                        client.getWindow().getHandle(),
                        GLFW.GLFW_KEY_C
                );
            } else {
                zoomActive = false;
            }

            // Auto Sprint
            if (ModConfig.INSTANCE.autoSprint && client.player != null) {
                if (client.player.input.playerInput.forward()) {
                    client.player.setSprinting(true);
                }
            }
        });

        LOGGER.info("Eclipse Visuals client initialized!");
    }
}
