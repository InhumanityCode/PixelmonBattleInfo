package com.inhumanity.pmbattleinfo.mixin.client;

import com.inhumanity.pmbattleinfo.config.ClientConfig;
import com.inhumanity.pmbattleinfo.util.RemoveNicknames;
import com.inhumanity.pmbattleinfo.util.Tooltip;

import com.mojang.blaze3d.matrix.MatrixStack;

import com.pixelmonmod.pixelmon.client.ClientProxy;
import com.pixelmonmod.pixelmon.client.gui.ScreenHelper;
import com.pixelmonmod.pixelmon.client.gui.battles.PixelmonClientData;
import com.pixelmonmod.pixelmon.client.gui.battles.pokemonOverlays.RaidElement;
import com.pixelmonmod.pixelmon.client.gui.widgets.PixelmonWidget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.inhumanity.pmbattleinfo.init.KeybindsInit.*;

@Mixin(value = RaidElement.class)
public abstract class RaidElementMixin extends PixelmonWidget {
    // Tooltip as a whole
    private final boolean pmbattleinfo$configTooltip = ClientConfig.battlePixelmonTooltip.get();

    @Shadow(remap = false)
    private PixelmonClientData enemy;

    @Inject(method = "drawElement(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V", at = @At("TAIL"), remap = false)
    private void drawPixelmonBattleTooltipEnemy(MatrixStack matrix, float scale, CallbackInfo ci) {
        RemoveNicknames.removeNickname(enemy);

        if (!pmbattleinfo$configTooltip) return;

        boolean isAlive = (enemy != null && enemy.health.intValue() > 0);
        if (!isAlive) return;

        int idx = 0;
        for (PixelmonClientData pcd : ClientProxy.battleManager.displayedEnemyPokemon) {
            if (pcd != null && pcd.pokemonUUID == enemy.pokemonUUID) {
                break;
            }
            idx++;
        }

        long window = Minecraft.getInstance().getWindow().getWindow();
        int x = 5;
        int y = 5;
        int gradient = Tooltip.getGradient();
        int background = Tooltip.getBackground();
        int alpha = Tooltip.getAlpha();

        if (idx == 0 && InputMappings.isKeyDown(window, kbBattleTTEnemy_1.getKey().getValue())) {
            ScreenHelper.renderTooltip(matrix, x, y, Tooltip.getTooltip(enemy), gradient, background, alpha, false, false);
        }
        else if (idx == 1 && InputMappings.isKeyDown(window, kbBattleTTEnemy_2.getKey().getValue())) {
            ScreenHelper.renderTooltip(matrix, x, y, Tooltip.getTooltip(enemy), gradient, background, alpha, false, false);
        }
        else if (idx == 2 && InputMappings.isKeyDown(window, kbBattleTTEnemy_3.getKey().getValue())) {
            ScreenHelper.renderTooltip(matrix, x, y, Tooltip.getTooltip(enemy), gradient, background, alpha, false, false);
        }
        else if (idx == 3 && InputMappings.isKeyDown(window, kbBattleTTEnemy_4.getKey().getValue())) {
            ScreenHelper.renderTooltip(matrix, x, y, Tooltip.getTooltip(enemy), gradient, background, alpha, false, false);
        }
        else if (idx == 4 && InputMappings.isKeyDown(window, kbBattleTTEnemy_5.getKey().getValue())) {
            ScreenHelper.renderTooltip(matrix, x, y, Tooltip.getTooltip(enemy), gradient, background, alpha, false, false);
        }
    }
}