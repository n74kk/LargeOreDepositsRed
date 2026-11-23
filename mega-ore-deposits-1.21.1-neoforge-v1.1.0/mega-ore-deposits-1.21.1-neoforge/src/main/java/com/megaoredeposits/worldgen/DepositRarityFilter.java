package com.megaoredeposits.worldgen;

import com.megaoredeposits.config.WorldDepositSettings;
import com.megaoredeposits.config.WorldDepositSettingsManager;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

/**
 * Модификатор редкости, учитывающий индивидуальный множитель частоты мира
 * (frequencyMultiplier) и флаг включения генерации из вкладки создания мира.
 */
public class DepositRarityFilter extends PlacementFilter {
    public static final MapCodec<DepositRarityFilter> CODEC = ExtraCodecs.POSITIVE_INT
            .fieldOf("chance")
            .xmap(DepositRarityFilter::new, filter -> filter.chance);

    private final int chance;

    public DepositRarityFilter(int chance) {
        this.chance = chance;
    }

    @Override
    protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
        WorldDepositSettings settings = WorldDepositSettingsManager.getActiveSettings();
        if (!settings.isEnabled()) {
            return false;
        }
        double freq = settings.getFrequencyMultiplier();
        if (freq <= 0.0) {
            return false;
        }
        double effectiveChance = Math.max(1.0, this.chance / freq);
        return random.nextDouble() < (1.0 / effectiveChance);
    }

    @Override
    public PlacementModifierType<?> type() {
        return ModPlacementModifiers.DEPOSIT_RARITY.get();
    }
}
