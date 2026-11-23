package com.megaoredeposits.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class DepositConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue ENABLED_BY_DEFAULT;
    public static final ModConfigSpec.DoubleValue RADIUS_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue DENSITY_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue FREQUENCY_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue RICH_CORE_MULTIPLIER;
    public static final ModConfigSpec.BooleanValue ENABLE_SURFACE_INDICATORS;
    public static final ModConfigSpec.IntValue MAX_SURFACE_SAMPLES;

    static {
        BUILDER.comment("Mega Ore Deposits - Базовые настройки генерации крупных месторождений руд (используются по умолчанию при создании мира)")
                .push("deposits");

        ENABLED_BY_DEFAULT = BUILDER
                .comment("Включить генерацию крупных месторождений по умолчанию")
                .define("enabledByDefault", true);

        RADIUS_MULTIPLIER = BUILDER
                .comment("Глобальный множитель радиуса всех месторождений (1.0 = стандарт, 1.5 = +50% к размеру)")
                .defineInRange("radiusMultiplier", 1.0, 0.25, 2.5);

        DENSITY_MULTIPLIER = BUILDER
                .comment("Глобальный множитель плотности руды внутри жилы (1.0 = стандарт)")
                .defineInRange("densityMultiplier", 1.0, 0.2, 2.0);

        FREQUENCY_MULTIPLIER = BUILDER
                .comment("Глобальный множитель частоты спавна месторождений (1.0 = стандарт, 2.0 = в 2 раза чаще)")
                .defineInRange("frequencyMultiplier", 1.0, 0.25, 3.0);

        RICH_CORE_MULTIPLIER = BUILDER
                .comment("Множитель шанса появления блоков сырой руды в центре жилы (1.0 = стандарт)")
                .defineInRange("richCoreMultiplier", 1.0, 0.0, 2.5);

        ENABLE_SURFACE_INDICATORS = BUILDER
                .comment("Генерировать ли поверхностные камни-индикаторы над месторождениями")
                .define("enableSurfaceIndicators", true);

        MAX_SURFACE_SAMPLES = BUILDER
                .comment("Максимальное количество индикаторов на поверхности над одним месторождением")
                .defineInRange("maxSurfaceSamples", 6, 0, 20);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
