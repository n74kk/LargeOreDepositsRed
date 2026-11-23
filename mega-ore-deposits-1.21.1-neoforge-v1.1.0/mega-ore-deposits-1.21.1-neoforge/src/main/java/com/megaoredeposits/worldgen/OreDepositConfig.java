package com.megaoredeposits.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record OreDepositConfig(
        BlockState stoneOre,
        BlockState deepslateOre,
        BlockState richOre,
        BlockState hostRock,
        BlockState surfaceSample,
        int radiusXZ,
        int radiusY,
        float density,
        float richCoreChance,
        float hostRockChance,
        int surfaceSampleCount
) implements FeatureConfiguration {

    public static final Codec<OreDepositConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.fieldOf("stone_ore").forGetter(OreDepositConfig::stoneOre),
            BlockState.CODEC.fieldOf("deepslate_ore").forGetter(OreDepositConfig::deepslateOre),
            BlockState.CODEC.fieldOf("rich_ore").forGetter(OreDepositConfig::richOre),
            BlockState.CODEC.fieldOf("host_rock").forGetter(OreDepositConfig::hostRock),
            BlockState.CODEC.fieldOf("surface_sample").forGetter(OreDepositConfig::surfaceSample),
            Codec.intRange(2, 24).fieldOf("radius_xz").forGetter(OreDepositConfig::radiusXZ),
            Codec.intRange(2, 18).fieldOf("radius_y").forGetter(OreDepositConfig::radiusY),
            Codec.floatRange(0.05f, 1.0f).fieldOf("density").forGetter(OreDepositConfig::density),
            Codec.floatRange(0.0f, 1.0f).fieldOf("rich_core_chance").forGetter(OreDepositConfig::richCoreChance),
            Codec.floatRange(0.0f, 1.0f).fieldOf("host_rock_chance").forGetter(OreDepositConfig::hostRockChance),
            Codec.intRange(0, 20).fieldOf("surface_sample_count").forGetter(OreDepositConfig::surfaceSampleCount)
    ).apply(instance, OreDepositConfig::new));
}
