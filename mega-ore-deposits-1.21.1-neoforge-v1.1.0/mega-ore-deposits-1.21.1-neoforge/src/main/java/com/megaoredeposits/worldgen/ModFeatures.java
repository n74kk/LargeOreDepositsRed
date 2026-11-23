package com.megaoredeposits.worldgen;

import com.megaoredeposits.MegaOreDepositsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, MegaOreDepositsMod.MODID);

    public static final DeferredHolder<Feature<?>, OreDepositFeature> ORE_DEPOSIT =
            FEATURES.register("ore_deposit", () -> new OreDepositFeature(OreDepositConfig.CODEC));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}
