package com.inhumanity.pmbattleinfo.util;

import java.util.Collection;

import com.inhumanity.pmbattleinfo.config.ClientConfig;

import com.pixelmonmod.pixelmon.client.gui.battles.PixelmonClientData;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RemoveNicknames {
    private static final boolean configEnabled = ClientConfig.battleRemoveNickname.get();

    public static boolean getConfig() {
        return configEnabled;
    }

    public static void removeNickname(PixelmonClientData PCD) {
        if (!configEnabled) return;

        if (PCD != null) {
            PCD.nickname = PCD.species.getLocalizedName();
        }
    }

    public static void removeNicknames(PixelmonClientData[] arrPCD) {
        if (!configEnabled) return;

        for (PixelmonClientData PCD : arrPCD) {
            if (PCD != null)
                PCD.nickname = PCD.species.getLocalizedName();
        }
    }

    public static void removeNicknames(Collection<PixelmonClientData> arrPCD) {
        if (!configEnabled) return;

        for (PixelmonClientData PCD : arrPCD) {
            if (PCD != null)
                PCD.nickname = PCD.species.getLocalizedName();
        }
    }
}
