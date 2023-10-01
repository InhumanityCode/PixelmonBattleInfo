package com.inhumanity.pmbattleinfo.mixin.client;

import java.util.*;

import com.inhumanity.pmbattleinfo.util.Tooltip;
import com.inhumanity.pmbattleinfo.config.ClientConfig;
import com.inhumanity.pmbattleinfo.util.RemoveNicknames;

import com.pixelmonmod.pixelmon.client.ClientProxy;
import com.pixelmonmod.pixelmon.client.gui.ScreenHelper;
import com.pixelmonmod.pixelmon.client.gui.battles.PixelmonClientData;
import com.pixelmonmod.pixelmon.client.gui.battles.pokemonOverlays.AllyElement;
import com.pixelmonmod.pixelmon.client.gui.widgets.PixelmonWidget;

import net.minecraft.client.util.InputMappings;
import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;

import static com.inhumanity.pmbattleinfo.init.KeybindsInit.*;

@Mixin(value = AllyElement.class)
public abstract class AllyElementMixin extends PixelmonWidget {
    // Tooltip as a whole
    private final boolean pmbattleinfo$configTooltip = ClientConfig.battlePixelmonTooltip.get();

    @Shadow(remap = false)
    private PixelmonClientData ally;

    private static int xPos = 15;
    private static int yPos = 45;

    @Inject(method = "drawElement(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V", at = @At("TAIL"), remap = false)
    private void drawPixelmonBattleTooltipAllies(MatrixStack matrix, float scale, CallbackInfo ci) {
        RemoveNicknames.removeNickname(ally);

        if (!pmbattleinfo$configTooltip) return;

        // Get list of Displayed Pixelmon
        ArrayList<PixelmonClientData> displayed = new ArrayList<>();
        if (ClientProxy.battleManager.displayedOurPokemon != null) displayed.addAll(Arrays.asList(ClientProxy.battleManager.displayedOurPokemon));
        if (ClientProxy.battleManager.displayedAllyPokemon != null) displayed.addAll(Arrays.asList(ClientProxy.battleManager.displayedAllyPokemon));
        if (displayed.isEmpty()) return;

        // Check alive status: if not null AND has HP, it can have a Tooltip
        boolean isAlive = (ally != null && ally.health.intValue() > 0);
        if (!isAlive) return;

        int idx = 0;
        for (PixelmonClientData pcd : displayed) {
            if (pcd != null && pcd.pokemonUUID == ally.pokemonUUID) {
                break;
            }
            idx++;
        }

        long window = Minecraft.getInstance().getWindow().getWindow();
        int gradient = Tooltip.getGradient();
        int background = Tooltip.getBackground();
        int alpha = Tooltip.getAlpha();

        if (idx == 0 && InputMappings.isKeyDown(window, kbBattleTTAlly_1.getKey().getValue())) {
            ScreenHelper.renderTooltip(matrix, xPos, yPos, Tooltip.getTooltip(ally), gradient, background, alpha, false, false);
        }
        else if (idx == 1 && InputMappings.isKeyDown(window, kbBattleTTAlly_2.getKey().getValue())) {
            ScreenHelper.renderTooltip(matrix, xPos, yPos, Tooltip.getTooltip(ally), gradient, background, alpha, false, false);
        }
        else if (idx == 2 && InputMappings.isKeyDown(window, kbBattleTTAlly_3.getKey().getValue())) {
            ScreenHelper.renderTooltip(matrix, xPos, yPos, Tooltip.getTooltip(ally), gradient, background, alpha, false, false);
        }
        else if (idx == 3 && InputMappings.isKeyDown(window, kbBattleTTAlly_4.getKey().getValue())) {
            ScreenHelper.renderTooltip(matrix, xPos, yPos, Tooltip.getTooltip(ally), gradient, background, alpha, false, false);
        }
        else if (idx == 4 && InputMappings.isKeyDown(window, kbBattleTTAlly_5.getKey().getValue())) {
            ScreenHelper.renderTooltip(matrix, xPos, yPos, Tooltip.getTooltip(ally), gradient, background, alpha, false, false);
        }

//        if (test.isPresent()) {
//            int mouseX = (int) Minecraft.getInstance().mouseHandler.xpos();
//            int mouseY = (int) Minecraft.getInstance().mouseHandler.ypos();
//
//            int topLeftX = test.get()[0];
//            int topLeftY = test.get()[1];
//            int botRightX = test.get()[2];
//            int botRightY = test.get()[3];
//
//            if ((topLeftX < mouseX && mouseX < botRightX) && (topLeftY < mouseY && mouseY < botRightY)) {
//                ModFile.getLogger().info(String.format("(%d, %d)", mouseX, mouseY));
//                if (InputMappings.isKeyDown(window, Inp)) {
//                    ModFile.getLogger().info("Dragging");
//                    xPos = mouseX;
//                    yPos = mouseY;
//                }
//            }
//        }
    }
}
