package com.megaoredeposits.client.gui;

import com.megaoredeposits.config.WorldDepositSettings;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.Locale;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;

/**
 * Вкладка "Месторождения" в меню создания нового мира (CreateWorldScreen).
 * Позволяет настраивать размер, плотность, частоту жил, богатое ядро и поверхностные индикаторы.
 */
public class OreDepositsWorldTab extends GridLayoutTab {
    private static final int WIDGET_WIDTH = 175;
    private static final int WIDGET_HEIGHT = 20;

    private final WorldDepositSettings settings;

    private final CycleButton<Boolean> enabledButton;
    private final CycleButton<Boolean> indicatorsButton;
    private final DoubleSlider radiusSlider;
    private final DoubleSlider densitySlider;
    private final DoubleSlider frequencySlider;
    private final DoubleSlider richCoreSlider;
    private final IntSlider maxSamplesSlider;
    private final CycleButton<DepositPreset> presetButton;

    private boolean updatingFromPreset = false;

    public OreDepositsWorldTab(WorldDepositSettings settings) {
        super(Component.translatable("megaoredeposits.tab.title"));
        this.settings = settings;

        GridLayout.RowHelper rowHelper = this.layout.columnSpacing(10).rowSpacing(8).createRowHelper(2);

        // Ряд 1: Главные переключатели
        this.enabledButton = CycleButton.onOffBuilder(settings.isEnabled())
                .withTooltip(val -> Tooltip.create(Component.translatable("megaoredeposits.tab.enabled.tooltip")))
                .create(0, 0, WIDGET_WIDTH, WIDGET_HEIGHT,
                        Component.translatable("megaoredeposits.tab.enabled"),
                        (button, value) -> {
                            settings.setEnabled(value);
                            syncControlStates();
                        });

        this.indicatorsButton = CycleButton.onOffBuilder(settings.isEnableSurfaceIndicators())
                .withTooltip(val -> Tooltip.create(Component.translatable("megaoredeposits.tab.indicators.tooltip")))
                .create(0, 0, WIDGET_WIDTH, WIDGET_HEIGHT,
                        Component.translatable("megaoredeposits.tab.indicators"),
                        (button, value) -> {
                            settings.setEnableSurfaceIndicators(value);
                            markCustomIfUserEdit();
                            syncControlStates();
                        });

        rowHelper.addChild(this.enabledButton);
        rowHelper.addChild(this.indicatorsButton);

        // Ряд 2: Размер жил и Плотность руды
        this.radiusSlider = new DoubleSlider(
                WIDGET_WIDTH, WIDGET_HEIGHT,
                "megaoredeposits.tab.radius",
                0.25, 2.50, 0.05,
                settings.getRadiusMultiplier(),
                val -> {
                    settings.setRadiusMultiplier(val);
                    markCustomIfUserEdit();
                }
        );
        this.radiusSlider.setTooltip(Tooltip.create(Component.translatable("megaoredeposits.tab.radius.tooltip")));

        this.densitySlider = new DoubleSlider(
                WIDGET_WIDTH, WIDGET_HEIGHT,
                "megaoredeposits.tab.density",
                0.20, 2.00, 0.05,
                settings.getDensityMultiplier(),
                val -> {
                    settings.setDensityMultiplier(val);
                    markCustomIfUserEdit();
                }
        );
        this.densitySlider.setTooltip(Tooltip.create(Component.translatable("megaoredeposits.tab.density.tooltip")));

        rowHelper.addChild(this.radiusSlider);
        rowHelper.addChild(this.densitySlider);

        // Ряд 3: Частота спавна и Богатое ядро
        this.frequencySlider = new DoubleSlider(
                WIDGET_WIDTH, WIDGET_HEIGHT,
                "megaoredeposits.tab.frequency",
                0.25, 3.00, 0.05,
                settings.getFrequencyMultiplier(),
                val -> {
                    settings.setFrequencyMultiplier(val);
                    markCustomIfUserEdit();
                }
        );
        this.frequencySlider.setTooltip(Tooltip.create(Component.translatable("megaoredeposits.tab.frequency.tooltip")));

        this.richCoreSlider = new DoubleSlider(
                WIDGET_WIDTH, WIDGET_HEIGHT,
                "megaoredeposits.tab.rich_core",
                0.00, 2.50, 0.05,
                settings.getRichCoreMultiplier(),
                val -> {
                    settings.setRichCoreMultiplier(val);
                    markCustomIfUserEdit();
                }
        );
        this.richCoreSlider.setTooltip(Tooltip.create(Component.translatable("megaoredeposits.tab.rich_core.tooltip")));

        rowHelper.addChild(this.frequencySlider);
        rowHelper.addChild(this.richCoreSlider);

        // Ряд 4: Макс. индикаторов и Выбор пресета
        this.maxSamplesSlider = new IntSlider(
                WIDGET_WIDTH, WIDGET_HEIGHT,
                "megaoredeposits.tab.max_samples",
                0, 20,
                settings.getMaxSurfaceSamples(),
                val -> {
                    settings.setMaxSurfaceSamples(val);
                    markCustomIfUserEdit();
                }
        );
        this.maxSamplesSlider.setTooltip(Tooltip.create(Component.translatable("megaoredeposits.tab.max_samples.tooltip")));

        DepositPreset initialPreset = DepositPreset.detect(settings);
        this.presetButton = CycleButton.<DepositPreset>builder(preset -> Component.translatable(preset.translationKey))
                .withValues(DepositPreset.CYCLE_VALUES)
                .withInitialValue(initialPreset)
                .withTooltip(preset -> Tooltip.create(Component.translatable("megaoredeposits.tab.preset.tooltip")))
                .create(0, 0, WIDGET_WIDTH, WIDGET_HEIGHT,
                        Component.translatable("megaoredeposits.tab.preset"),
                        (button, preset) -> applyPreset(preset));

        rowHelper.addChild(this.maxSamplesSlider);
        rowHelper.addChild(this.presetButton);

        // Ряд 5: Кнопка сброса по центру
        Button resetButton = Button.builder(
                        Component.translatable("megaoredeposits.tab.reset"),
                        btn -> resetToConfigDefaults()
                )
                .width(210)
                .tooltip(Tooltip.create(Component.translatable("megaoredeposits.tab.reset.tooltip")))
                .build();

        rowHelper.addChild(resetButton, 2, rowHelper.newCellSettings().alignHorizontallyCenter().paddingTop(4));

        syncControlStates();
    }

    private void applyPreset(DepositPreset preset) {
        if (preset == DepositPreset.CUSTOM) {
            return;
        }
        this.updatingFromPreset = true;
        try {
            settings.setEnabled(true);
            settings.setEnableSurfaceIndicators(preset.indicators);
            settings.setRadiusMultiplier(preset.radius);
            settings.setDensityMultiplier(preset.density);
            settings.setFrequencyMultiplier(preset.frequency);
            settings.setRichCoreMultiplier(preset.richCore);
            settings.setMaxSurfaceSamples(preset.maxSamples);

            this.enabledButton.setValue(true);
            this.indicatorsButton.setValue(preset.indicators);
            this.radiusSlider.setActualValue(preset.radius);
            this.densitySlider.setActualValue(preset.density);
            this.frequencySlider.setActualValue(preset.frequency);
            this.richCoreSlider.setActualValue(preset.richCore);
            this.maxSamplesSlider.setIntValue(preset.maxSamples);
            syncControlStates();
        } finally {
            this.updatingFromPreset = false;
        }
    }

    private void resetToConfigDefaults() {
        WorldDepositSettings defaults = WorldDepositSettings.fromConfig();
        this.updatingFromPreset = true;
        try {
            settings.setEnabled(defaults.isEnabled());
            settings.setEnableSurfaceIndicators(defaults.isEnableSurfaceIndicators());
            settings.setRadiusMultiplier(defaults.getRadiusMultiplier());
            settings.setDensityMultiplier(defaults.getDensityMultiplier());
            settings.setFrequencyMultiplier(defaults.getFrequencyMultiplier());
            settings.setRichCoreMultiplier(defaults.getRichCoreMultiplier());
            settings.setMaxSurfaceSamples(defaults.getMaxSurfaceSamples());

            this.enabledButton.setValue(defaults.isEnabled());
            this.indicatorsButton.setValue(defaults.isEnableSurfaceIndicators());
            this.radiusSlider.setActualValue(defaults.getRadiusMultiplier());
            this.densitySlider.setActualValue(defaults.getDensityMultiplier());
            this.frequencySlider.setActualValue(defaults.getFrequencyMultiplier());
            this.richCoreSlider.setActualValue(defaults.getRichCoreMultiplier());
            this.maxSamplesSlider.setIntValue(defaults.getMaxSurfaceSamples());
            this.presetButton.setValue(DepositPreset.detect(defaults));
            syncControlStates();
        } finally {
            this.updatingFromPreset = false;
        }
    }

    private void markCustomIfUserEdit() {
        if (!this.updatingFromPreset && this.presetButton != null) {
            this.presetButton.setValue(DepositPreset.detect(this.settings));
        }
    }

    private void syncControlStates() {
        boolean on = settings.isEnabled();
        this.indicatorsButton.active = on;
        this.radiusSlider.active = on;
        this.densitySlider.active = on;
        this.frequencySlider.active = on;
        this.richCoreSlider.active = on;
        this.maxSamplesSlider.active = on && settings.isEnableSurfaceIndicators();
        this.presetButton.active = on;
    }

    public enum DepositPreset {
        DEFAULT("megaoredeposits.preset.default", 1.0, 1.0, 1.0, 1.0, true, 6),
        ABUNDANT("megaoredeposits.preset.abundant", 1.4, 1.3, 1.8, 1.5, true, 8),
        RARE_GIANTS("megaoredeposits.preset.rare_giants", 2.0, 1.25, 0.45, 1.8, true, 10),
        HARDCORE("megaoredeposits.preset.hardcore", 0.75, 0.70, 0.55, 0.5, false, 0),
        CUSTOM("megaoredeposits.preset.custom", 1.0, 1.0, 1.0, 1.0, true, 6);

        public static final DepositPreset[] CYCLE_VALUES = {DEFAULT, ABUNDANT, RARE_GIANTS, HARDCORE};

        public final String translationKey;
        public final double radius;
        public final double density;
        public final double frequency;
        public final double richCore;
        public final boolean indicators;
        public final int maxSamples;

        DepositPreset(String translationKey, double radius, double density, double frequency, double richCore, boolean indicators, int maxSamples) {
            this.translationKey = translationKey;
            this.radius = radius;
            this.density = density;
            this.frequency = frequency;
            this.richCore = richCore;
            this.indicators = indicators;
            this.maxSamples = maxSamples;
        }

        public static DepositPreset detect(WorldDepositSettings s) {
            for (DepositPreset p : CYCLE_VALUES) {
                if (Math.abs(s.getRadiusMultiplier() - p.radius) < 0.01
                        && Math.abs(s.getDensityMultiplier() - p.density) < 0.01
                        && Math.abs(s.getFrequencyMultiplier() - p.frequency) < 0.01
                        && Math.abs(s.getRichCoreMultiplier() - p.richCore) < 0.01
                        && s.isEnableSurfaceIndicators() == p.indicators
                        && s.getMaxSurfaceSamples() == p.maxSamples) {
                    return p;
                }
            }
            return CUSTOM;
        }
    }

    private static class DoubleSlider extends AbstractSliderButton {
        private final String translationKey;
        private final double min;
        private final double max;
        private final double step;
        private final DoubleConsumer onChange;

        public DoubleSlider(int width, int height, String translationKey, double min, double max, double step, double initial, DoubleConsumer onChange) {
            super(0, 0, width, height, Component.empty(), normalize(initial, min, max));
            this.translationKey = translationKey;
            this.min = min;
            this.max = max;
            this.step = step;
            this.onChange = onChange;
            updateMessage();
        }

        private static double normalize(double val, double min, double max) {
            return Mth.clamp((val - min) / (max - min), 0.0, 1.0);
        }

        private double getSnappedValue() {
            double raw = min + this.value * (max - min);
            double snapped = Math.round(raw / step) * step;
            return Mth.clamp(snapped, min, max);
        }

        public void setActualValue(double actual) {
            this.value = normalize(actual, min, max);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            String formatted = String.format(Locale.ROOT, "%.2fx", getSnappedValue());
            setMessage(Component.translatable(translationKey, formatted));
        }

        @Override
        protected void applyValue() {
            onChange.accept(getSnappedValue());
        }
    }

    private static class IntSlider extends AbstractSliderButton {
        private final String translationKey;
        private final int min;
        private final int max;
        private final IntConsumer onChange;

        public IntSlider(int width, int height, String translationKey, int min, int max, int initial, IntConsumer onChange) {
            super(0, 0, width, height, Component.empty(), normalize(initial, min, max));
            this.translationKey = translationKey;
            this.min = min;
            this.max = max;
            this.onChange = onChange;
            updateMessage();
        }

        private static double normalize(int val, int min, int max) {
            return Mth.clamp((double) (val - min) / (max - min), 0.0, 1.0);
        }

        private int getIntValue() {
            return Mth.clamp((int) Math.round(min + this.value * (max - min)), min, max);
        }

        public void setIntValue(int actual) {
            this.value = normalize(actual, min, max);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.translatable(translationKey, getIntValue()));
        }

        @Override
        protected void applyValue() {
            onChange.accept(getIntValue());
        }
    }
}
