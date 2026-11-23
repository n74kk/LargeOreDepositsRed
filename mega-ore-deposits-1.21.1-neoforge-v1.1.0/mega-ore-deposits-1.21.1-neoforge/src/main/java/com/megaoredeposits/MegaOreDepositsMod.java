package com.megaoredeposits;

import com.megaoredeposits.block.ModBlocks;
import com.megaoredeposits.config.DepositConfig;
import com.megaoredeposits.config.WorldDepositSettingsManager;
import com.megaoredeposits.worldgen.ModFeatures;
import com.megaoredeposits.worldgen.ModPlacementModifiers;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(MegaOreDepositsMod.MODID)
public class MegaOreDepositsMod {
    public static final String MODID = "megaoredeposits";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MOD_TAB =
            CREATIVE_TABS.register("megaoredeposits_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.megaoredeposits"))
                    .icon(() -> new ItemStack(ModBlocks.IRON_SAMPLE.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModBlocks.COAL_SAMPLE.get());
                        output.accept(ModBlocks.COPPER_SAMPLE.get());
                        output.accept(ModBlocks.IRON_SAMPLE.get());
                        output.accept(ModBlocks.GOLD_SAMPLE.get());
                        output.accept(ModBlocks.REDSTONE_SAMPLE.get());
                        output.accept(ModBlocks.LAPIS_SAMPLE.get());
                        output.accept(ModBlocks.DIAMOND_SAMPLE.get());
                    })
                    .build());

    public MegaOreDepositsMod(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.register(modEventBus);
        ModFeatures.register(modEventBus);
        ModPlacementModifiers.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, DepositConfig.SPEC);

        // Загрузка/сохранение индивидуальных настроек мира до генерации стартовых чанков
        NeoForge.EVENT_BUS.addListener(this::onServerAboutToStart);
        NeoForge.EVENT_BUS.addListener(this::onServerStopped);

        LOGGER.info("Mega Ore Deposits v1.1.0 initialized for Minecraft 1.21.1 NeoForge!");
    }

    private void onServerAboutToStart(ServerAboutToStartEvent event) {
        WorldDepositSettingsManager.onServerAboutToStart(event.getServer());
    }

    private void onServerStopped(ServerStoppedEvent event) {
        WorldDepositSettingsManager.onServerStopped();
    }
}
