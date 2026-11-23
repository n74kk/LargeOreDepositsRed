package com.megaoredeposits.client;

import com.megaoredeposits.MegaOreDepositsMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * Клиентская точка входа: подключает встроенный экран конфигурации в меню "Моды -> Настроить".
 */
@Mod(value = MegaOreDepositsMod.MODID, dist = Dist.CLIENT)
public class MegaOreDepositsClient {
    public MegaOreDepositsClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
