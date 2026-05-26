package dev.eclipsevisuals.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.eclipsevisuals.EclipseVisualsClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles saving and loading ModConfig as JSON using Gson.
 * Config file: .minecraft/config/eclipse-visuals.json
 */
public class ConfigLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("eclipse-visuals.json");

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            ModConfig.INSTANCE = new ModConfig();
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            ModConfig.INSTANCE = GSON.fromJson(reader, ModConfig.class);
            if (ModConfig.INSTANCE == null) ModConfig.INSTANCE = new ModConfig();
        } catch (IOException e) {
            EclipseVisualsClient.LOGGER.error("Failed to load Eclipse Visuals config", e);
            ModConfig.INSTANCE = new ModConfig();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(ModConfig.INSTANCE, writer);
            }
        } catch (IOException e) {
            EclipseVisualsClient.LOGGER.error("Failed to save Eclipse Visuals config", e);
        }
    }
}
