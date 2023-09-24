package com.inhumanity.pmbattleinfo;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.inhumanity.pmbattleinfo.config.ClientConfig;

@Mod(ModFile.MOD_ID)
@Mod.EventBusSubscriber(modid = ModFile.MOD_ID)
@OnlyIn(Dist.CLIENT)
public class ModFile {

    public static final String MOD_ID = "pmbattleinfo";

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    private static ModFile instance;

    public ModFile() {
        instance = this;

        ModLoadingContext.get().registerConfig(Type.CLIENT, ClientConfig.SPEC, String.format("%s-client.toml", MOD_ID));

        MinecraftForge.EVENT_BUS.register(this);

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(ModFile::onModLoad);
    }

    public static void onModLoad(FMLCommonSetupEvent event) {
        // Pixelmon.EVENT_BUS.register(new Listener());
        // MinecraftForge.EVENT_BUS.register(new Listener());
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
