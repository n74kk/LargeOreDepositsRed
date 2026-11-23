package com.megaoredeposits.block;

import com.megaoredeposits.MegaOreDepositsMod;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(MegaOreDepositsMod.MODID);
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(MegaOreDepositsMod.MODID);

    public static final DeferredBlock<SurfaceSampleBlock> COAL_SAMPLE = registerSample("coal_sample");
    public static final DeferredBlock<SurfaceSampleBlock> COPPER_SAMPLE = registerSample("copper_sample");
    public static final DeferredBlock<SurfaceSampleBlock> IRON_SAMPLE = registerSample("iron_sample");
    public static final DeferredBlock<SurfaceSampleBlock> GOLD_SAMPLE = registerSample("gold_sample");
    public static final DeferredBlock<SurfaceSampleBlock> REDSTONE_SAMPLE = registerSample("redstone_sample");
    public static final DeferredBlock<SurfaceSampleBlock> LAPIS_SAMPLE = registerSample("lapis_sample");
    public static final DeferredBlock<SurfaceSampleBlock> DIAMOND_SAMPLE = registerSample("diamond_sample");

    private static DeferredBlock<SurfaceSampleBlock> registerSample(String name) {
        DeferredBlock<SurfaceSampleBlock> block = BLOCKS.register(
                name,
                () -> new SurfaceSampleBlock(BlockBehaviour.Properties.of()
                        .mapColor(MapColor.STONE)
                        .strength(0.35f)
                        .sound(SoundType.STONE)
                        .noOcclusion()
                        .pushReaction(PushReaction.DESTROY))
        );
        ITEMS.registerSimpleBlockItem(name, block);
        return block;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}
