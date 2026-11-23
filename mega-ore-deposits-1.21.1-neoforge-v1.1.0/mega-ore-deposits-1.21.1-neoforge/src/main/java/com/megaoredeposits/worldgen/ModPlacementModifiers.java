package com.megaoredeposits.worldgen;

import com.megaoredeposits.MegaOreDepositsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModPlacementModifiers {
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS =
            DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, MegaOreDepositsMod.MODID);

    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<DepositRarityFilter>> DEPOSIT_RARITY =
            PLACEMENT_MODIFIERS.register("deposit_rarity", () -> () -> DepositRarityFilter.CODEC);

    public static void register(IEventBus eventBus) {
        PLACEMENT_MODIFIERS.register(eventBus);
    }
}
