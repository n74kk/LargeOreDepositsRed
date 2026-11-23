package com.megaoredeposits.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.megaoredeposits.MegaOreDepositsMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Управляет жизненным циклом индивидуальных настроек мира:
 * передаёт выбранные игроком параметры из экрана создания мира в поток генерации
 * и сохраняет их в файле <папка_мира>/megaoredeposits-world.json.
 */
public final class WorldDepositSettingsManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String WORLD_FILE_NAME = "megaoredeposits-world.json";

    private static volatile WorldDepositSettings activeSettings = new WorldDepositSettings();
    private static volatile WorldDepositSettings pendingCreationSettings = null;

    private WorldDepositSettingsManager() {
    }

    public static WorldDepositSettings getActiveSettings() {
        return activeSettings;
    }

    /**
     * Вызывается из CreateWorldScreenMixin в момент нажатия кнопки "Создать новый мир".
     */
    public static void setPendingCreationSettings(WorldDepositSettings settings) {
        pendingCreationSettings = settings != null ? settings.copy() : null;
    }

    /**
     * Срабатывает в ServerAboutToStartEvent ДО начала генерации стартовых чанков мира.
     */
    public static void onServerAboutToStart(MinecraftServer server) {
        Path worldFile = server.getWorldPath(LevelResource.ROOT).resolve(WORLD_FILE_NAME);
        WorldDepositSettings pending = pendingCreationSettings;
        pendingCreationSettings = null;

        if (pending != null) {
            pending.clampAll();
            activeSettings = pending;
            saveToFile(worldFile, pending);
            MegaOreDepositsMod.LOGGER.info("Applied custom world creation settings for Mega Ore Deposits and saved to {}", worldFile);
            return;
        }

        if (Files.exists(worldFile)) {
            WorldDepositSettings loaded = loadFromFile(worldFile);
            if (loaded != null) {
                loaded.clampAll();
                activeSettings = loaded;
                MegaOreDepositsMod.LOGGER.info("Loaded Mega Ore Deposits world settings from {}", worldFile);
                return;
            }
        }

        activeSettings = WorldDepositSettings.fromConfig();
    }

    public static void onServerStopped() {
        pendingCreationSettings = null;
        activeSettings = WorldDepositSettings.fromConfig();
    }

    private static void saveToFile(Path path, WorldDepositSettings settings) {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.writeString(path, GSON.toJson(settings), StandardCharsets.UTF_8);
        } catch (IOException e) {
            MegaOreDepositsMod.LOGGER.error("Failed to save Mega Ore Deposits world settings to {}", path, e);
        }
    }

    private static WorldDepositSettings loadFromFile(Path path) {
        try {
            String raw = Files.readString(path, StandardCharsets.UTF_8);
            return GSON.fromJson(raw, WorldDepositSettings.class);
        } catch (Exception e) {
            MegaOreDepositsMod.LOGGER.error("Failed to read Mega Ore Deposits world settings from {}, using defaults", path, e);
            return null;
        }
    }
}
