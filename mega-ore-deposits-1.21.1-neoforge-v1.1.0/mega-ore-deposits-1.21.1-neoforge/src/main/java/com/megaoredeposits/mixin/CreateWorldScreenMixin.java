package com.megaoredeposits.mixin;

import com.megaoredeposits.client.gui.OreDepositsWorldTab;
import com.megaoredeposits.config.WorldDepositSettings;
import com.megaoredeposits.config.WorldDepositSettingsManager;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

/**
 * Добавляет 4-ю вкладку "Месторождения" в стандартное окно создания мира Minecraft 1.21.1
 * и передаёт выбранные параметры в менеджер при нажатии "Создать новый мир".
 */
@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {

    @Unique
    private final WorldDepositSettings megaoredeposits$draftSettings = WorldDepositSettings.fromConfig();

    @ModifyArg(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/tabs/TabNavigationBar$Builder;addTabs([Lnet/minecraft/client/gui/components/tabs/Tab;)Lnet/minecraft/client/gui/components/tabs/TabNavigationBar$Builder;"
            )
    )
    private Tab[] megaoredeposits$addOreDepositsTab(Tab[] originalTabs) {
        Tab[] expanded = Arrays.copyOf(originalTabs, originalTabs.length + 1);
        expanded[originalTabs.length] = new OreDepositsWorldTab(this.megaoredeposits$draftSettings);
        return expanded;
    }

    @Inject(method = "onCreate", at = @At("HEAD"))
    private void megaoredeposits$commitWorldSettings(CallbackInfo ci) {
        WorldDepositSettingsManager.setPendingCreationSettings(this.megaoredeposits$draftSettings.copy());
    }
}
