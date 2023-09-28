package com.inhumanity.pmbattleinfo.util;

import com.inhumanity.pmbattleinfo.ModFile;
import com.inhumanity.pmbattleinfo.init.KeybindsInit;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = ModFile.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ModFile.getLogger().info("Start Keybinds setup");
        KeybindsInit.init();
    }
}
