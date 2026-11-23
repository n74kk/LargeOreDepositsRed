package com.megaoredeposits.config;

import net.minecraft.util.Mth;

/**
 * Настройки генерации месторождений для конкретного мира.
 * Настраиваются во вкладке "Месторождения" при создании мира и сохраняются в папке мира.
 */
public class WorldDepositSettings {
    private boolean enabled;
    private double radiusMultiplier;
    private double densityMultiplier;
    private double frequencyMultiplier;
    private double richCoreMultiplier;
    private boolean enableSurfaceIndicators;
    private int maxSurfaceSamples;

    public WorldDepositSettings() {
        this(true, 1.0, 1.0, 1.0, 1.0, true, 6);
    }

    public WorldDepositSettings(
            boolean enabled,
            double radiusMultiplier,
            double densityMultiplier,
            double frequencyMultiplier,
            double richCoreMultiplier,
            boolean enableSurfaceIndicators,
            int maxSurfaceSamples
    ) {
        this.enabled = enabled;
        this.radiusMultiplier = radiusMultiplier;
        this.densityMultiplier = densityMultiplier;
        this.frequencyMultiplier = frequencyMultiplier;
        this.richCoreMultiplier = richCoreMultiplier;
        this.enableSurfaceIndicators = enableSurfaceIndicators;
        this.maxSurfaceSamples = maxSurfaceSamples;
        clampAll();
    }

    public static WorldDepositSettings fromConfig() {
        try {
            return new WorldDepositSettings(
                    DepositConfig.ENABLED_BY_DEFAULT.get(),
                    DepositConfig.RADIUS_MULTIPLIER.get(),
                    DepositConfig.DENSITY_MULTIPLIER.get(),
                    DepositConfig.FREQUENCY_MULTIPLIER.get(),
                    DepositConfig.RICH_CORE_MULTIPLIER.get(),
                    DepositConfig.ENABLE_SURFACE_INDICATORS.get(),
                    DepositConfig.MAX_SURFACE_SAMPLES.get()
            );
        } catch (IllegalStateException ignored) {
            return new WorldDepositSettings();
        }
    }

    public WorldDepositSettings copy() {
        return new WorldDepositSettings(
                this.enabled,
                this.radiusMultiplier,
                this.densityMultiplier,
                this.frequencyMultiplier,
                this.richCoreMultiplier,
                this.enableSurfaceIndicators,
                this.maxSurfaceSamples
        );
    }

    public void clampAll() {
        this.radiusMultiplier = Mth.clamp(this.radiusMultiplier, 0.25, 2.5);
        this.densityMultiplier = Mth.clamp(this.densityMultiplier, 0.20, 2.0);
        this.frequencyMultiplier = Mth.clamp(this.frequencyMultiplier, 0.25, 3.0);
        this.richCoreMultiplier = Mth.clamp(this.richCoreMultiplier, 0.0, 2.5);
        this.maxSurfaceSamples = Mth.clamp(this.maxSurfaceSamples, 0, 20);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public double getRadiusMultiplier() {
        return radiusMultiplier;
    }

    public void setRadiusMultiplier(double radiusMultiplier) {
        this.radiusMultiplier = Mth.clamp(radiusMultiplier, 0.25, 2.5);
    }

    public double getDensityMultiplier() {
        return densityMultiplier;
    }

    public void setDensityMultiplier(double densityMultiplier) {
        this.densityMultiplier = Mth.clamp(densityMultiplier, 0.20, 2.0);
    }

    public double getFrequencyMultiplier() {
        return frequencyMultiplier;
    }

    public void setFrequencyMultiplier(double frequencyMultiplier) {
        this.frequencyMultiplier = Mth.clamp(frequencyMultiplier, 0.25, 3.0);
    }

    public double getRichCoreMultiplier() {
        return richCoreMultiplier;
    }

    public void setRichCoreMultiplier(double richCoreMultiplier) {
        this.richCoreMultiplier = Mth.clamp(richCoreMultiplier, 0.0, 2.5);
    }

    public boolean isEnableSurfaceIndicators() {
        return enableSurfaceIndicators;
    }

    public void setEnableSurfaceIndicators(boolean enableSurfaceIndicators) {
        this.enableSurfaceIndicators = enableSurfaceIndicators;
    }

    public int getMaxSurfaceSamples() {
        return maxSurfaceSamples;
    }

    public void setMaxSurfaceSamples(int maxSurfaceSamples) {
        this.maxSurfaceSamples = Mth.clamp(maxSurfaceSamples, 0, 20);
    }
}
