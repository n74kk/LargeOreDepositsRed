package com.megaoredeposits.worldgen;

import com.megaoredeposits.config.WorldDepositSettings;
import com.megaoredeposits.config.WorldDepositSettingsManager;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

/**
 * Генератор крупных органических месторождений руд в форме искажённого эллипсоида
 * с богатым ядром, вмещающей породой и поверхностными камнями-индикаторами.
 */
public class OreDepositFeature extends Feature<OreDepositConfig> {

    public OreDepositFeature(Codec<OreDepositConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<OreDepositConfig> context) {
        WorldDepositSettings settings = WorldDepositSettingsManager.getActiveSettings();
        if (!settings.isEnabled()) {
            return false;
        }

        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        OreDepositConfig config = context.config();

        double radiusMult = settings.getRadiusMultiplier();
        double densityMult = settings.getDensityMultiplier();
        double richCoreMult = settings.getRichCoreMultiplier();

        // Ограничиваем радиус по X/Z до 15 блоков от центра, чтобы не выходить за безопасную зону генерации чанка
        int rx = Mth.clamp((int) Math.round(config.radiusXZ() * radiusMult * (0.85 + random.nextDouble() * 0.30)), 2, 15);
        int rz = Mth.clamp((int) Math.round(config.radiusXZ() * radiusMult * (0.85 + random.nextDouble() * 0.30)), 2, 15);
        int ry = Mth.clamp((int) Math.round(config.radiusY() * radiusMult * (0.85 + random.nextDouble() * 0.30)), 2, 16);

        float baseDensity = Mth.clamp((float) (config.density() * densityMult), 0.05f, 1.0f);
        float effectiveRichCoreChance = Mth.clamp((float) (config.richCoreChance() * richCoreMult), 0.0f, 1.0f);

        // Случайные фазы для органичного искажения краёв жилы
        double phaseX = random.nextDouble() * Math.PI * 2.0;
        double phaseZ = random.nextDouble() * Math.PI * 2.0;
        double tiltX = (random.nextDouble() - 0.5) * 0.35;
        double tiltZ = (random.nextDouble() - 0.5) * 0.35;

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int placedOres = 0;

        for (int dx = -rx; dx <= rx; dx++) {
            for (int dz = -rz; dz <= rz; dz++) {
                double yShift = dx * tiltX + dz * tiltZ;
                for (int dy = -ry; dy <= ry; dy++) {
                    double nx = (double) dx / rx;
                    double ny = (dy - yShift) / ry;
                    double nz = (double) dz / rz;

                    // Плавная органичная деформация границы эллипсоида
                    double wobble = 0.14 * Math.sin(dx * 0.55 + phaseX) * Math.cos(dz * 0.55 + phaseZ);
                    double distSq = nx * nx + ny * ny + nz * nz + wobble;

                    if (distSq > 1.0) {
                        continue;
                    }

                    cursor.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    if (cursor.getY() <= level.getMinBuildHeight() + 1 || cursor.getY() >= level.getMaxBuildHeight() - 1) {
                        continue;
                    }
                    if (!level.ensureCanWrite(cursor)) {
                        continue;
                    }

                    BlockState existing = level.getBlockState(cursor);
                    boolean isStone = existing.is(BlockTags.STONE_ORE_REPLACEABLES);
                    boolean isDeepslate = existing.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

                    if (!isStone && !isDeepslate) {
                        continue;
                    }

                    // Плотность выше в ядре и мягко снижается к краям месторождения
                    double localDensity = baseDensity * (1.15 - 0.55 * Math.max(0.0, distSq));

                    if (random.nextDouble() < localDensity) {
                        BlockState toPlace;
                        if (distSq < 0.25 && random.nextFloat() < effectiveRichCoreChance) {
                            toPlace = config.richOre();
                        } else {
                            toPlace = isDeepslate ? config.deepslateOre() : config.stoneOre();
                        }
                        level.setBlock(cursor, toPlace, Block.UPDATE_CLIENTS);
                        placedOres++;
                    } else if (config.hostRockChance() > 0.0f && random.nextFloat() < config.hostRockChance()) {
                        level.setBlock(cursor, config.hostRock(), Block.UPDATE_CLIENTS);
                    }
                }
            }
        }

        // Генерация поверхностных камней-индикаторов прямо над жилой
        if (placedOres > 0 && settings.isEnableSurfaceIndicators()) {
            int maxAllowed = settings.getMaxSurfaceSamples();
            int sampleCount = Math.min(config.surfaceSampleCount(), maxAllowed);
            // Если игрок увеличил лимит выше базового, масштабируем пропорционально
            if (maxAllowed > 6 && config.surfaceSampleCount() > 0) {
                sampleCount = Mth.clamp((int) Math.round(config.surfaceSampleCount() * (maxAllowed / 6.0)), 1, maxAllowed);
            }
            placeSurfaceIndicators(level, origin, rx, rz, sampleCount, config.surfaceSample(), random);
        }

        return placedOres > 0;
    }

    private void placeSurfaceIndicators(
            WorldGenLevel level,
            BlockPos origin,
            int rx,
            int rz,
            int count,
            BlockState sampleState,
            RandomSource random
    ) {
        if (count <= 0 || sampleState.isAir()) {
            return;
        }

        int spreadX = Math.max(3, rx / 2);
        int spreadZ = Math.max(3, rz / 2);
        int placed = 0;

        for (int attempt = 0; attempt < count * 4 && placed < count; attempt++) {
            int sx = origin.getX() + random.nextInt(spreadX * 2 + 1) - spreadX;
            int sz = origin.getZ() + random.nextInt(spreadZ * 2 + 1) - spreadZ;

            BlockPos checkCol = new BlockPos(sx, origin.getY(), sz);
            if (!level.ensureCanWrite(checkCol)) {
                continue;
            }

            int sy = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, sx, sz);
            BlockPos surfacePos = new BlockPos(sx, sy, sz);
            BlockPos belowPos = surfacePos.below();

            if (!level.ensureCanWrite(surfacePos) || !level.getFluidState(surfacePos).isEmpty()) {
                continue;
            }

            BlockState atSurface = level.getBlockState(surfacePos);
            BlockState below = level.getBlockState(belowPos);

            boolean canReplaceSurface = atSurface.isAir() || (atSurface.canBeReplaced() && level.getFluidState(surfacePos).isEmpty());
            boolean validGround = below.is(BlockTags.DIRT)
                    || below.is(BlockTags.SAND)
                    || below.is(BlockTags.BASE_STONE_OVERWORLD)
                    || below.is(Blocks.GRAVEL)
                    || below.is(Blocks.SNOW_BLOCK);

            if (canReplaceSurface && validGround && sampleState.canSurvive(level, surfacePos)) {
                level.setBlock(surfacePos, sampleState, Block.UPDATE_CLIENTS);
                placed++;
            }
        }
    }
}
