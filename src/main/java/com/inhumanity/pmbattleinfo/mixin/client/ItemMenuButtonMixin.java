package com.inhumanity.pmbattleinfo.mixin.client;

import com.inhumanity.pmbattleinfo.config.ClientConfig;
import com.inhumanity.pmbattleinfo.util.PokeBallMath;
import com.inhumanity.pmbattleinfo.util.Tooltip;

import com.mojang.blaze3d.matrix.MatrixStack;

import com.pixelmonmod.api.registry.RegistryValue;
import com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBall;
import com.pixelmonmod.pixelmon.client.ClientProxy;
import com.pixelmonmod.pixelmon.client.gui.ScreenHelper;
import com.pixelmonmod.pixelmon.client.gui.battles.PixelmonClientData;
import com.pixelmonmod.pixelmon.client.gui.battles.battleScreens.BattleMenuElement.MenuListButton;
import com.pixelmonmod.pixelmon.client.gui.battles.battleScreens.screens.bag.ItemMenuButton;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBallRegistry.*;

@Debug(export = true)
@Mixin(value = ItemMenuButton.class)
public abstract class ItemMenuButtonMixin extends MenuListButton {
    @Unique
    private static HashSet<String> pmbattleinfo$balls = new HashSet<>();

    @Final
    @Shadow(remap = false)
    private ItemStack stack;

    @Shadow(remap = false)
    private String title;

    public ItemMenuButtonMixin(int id, Screen parent) {
        super(id, parent);
    }

    @Inject(method = "renderButton", at = @At("HEAD"))
    private void drawPixelmonPokeBallTooltip(MatrixStack matrix, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!ClientConfig.battlePokeBallCatchChance.get()) return;

        final PixelmonClientData[] enemies = ClientProxy.battleManager.displayedEnemyPokemon;
        if (enemies == null) return;

        boolean onlyOneEnemy = (1 == Arrays.stream(enemies).filter(p -> p.health.intValue() > 0).count());
        if (!onlyOneEnemy) return;

        // Return if UI elements do not contain Ball
        String displayName = (this.title == null || this.title.isEmpty() ? this.stack.getHoverName().getString() : this.title);
        if (!displayName.toLowerCase().contains("ball") || displayName.toLowerCase().contains("balls")) {
            pmbattleinfo$balls.clear();
            return;
        }

        // Enemy and Our Pixelmon have to exist if on this screen so ignore errors
        PixelmonClientData enemy = Arrays.stream(enemies).filter(p -> p.health.intValue() > 0).findFirst().get();
        PixelmonClientData ours = Arrays.stream(ClientProxy.battleManager.displayedOurPokemon).filter(p -> p.health.intValue() > 0).findFirst().get();

        // Clear every page length when Set contains more than page length
        if (pmbattleinfo$balls.size() > 6)
            pmbattleinfo$balls.clear();
        pmbattleinfo$balls.add(displayName);

        // Tooltip Info
        int xPos = 10;
        int yPos = 10;
        int gradient = Tooltip.getGradient();
        int background = Tooltip.getBackground();
        int alpha = 255; // Override because this gets displayed overlapped like 6 times - will try to optimize this some other time


        // Tooltip Header
        ArrayList<ITextComponent> tooltip = new ArrayList<>();
        tooltip.add(new StringTextComponent("Catch Rates").withStyle(TextFormatting.BOLD).withStyle(TextFormatting.RED));

        // For each Ball stored
        for (String ballName : pmbattleinfo$balls) {
            // Get the Ball's key name
            RegistryValue<PokeBall> regPokeBall = getPokeBall(ballName.toLowerCase().replace(" ", "_"));

            // Special Key Cases (primarily the accent on E in Poke)
            if (ballName.toLowerCase().contains("christmas pok")) regPokeBall = CHRISTMAS_BALL;
            else if (ballName.toLowerCase().contains("ancient pok")) regPokeBall = ANCIENT_POKE_BALL;
            else if (ballName.toLowerCase().contains("pok")) regPokeBall = POKE_BALL;

            // Just in case Ball not found, skip this Ball
            if (!regPokeBall.getValue().isPresent()) continue;

            double chance = PokeBallMath.getCatchChance(regPokeBall, enemy, ours);

            // Text is base white, gold if >=100%, and gray if not fully supported
            TextFormatting textColor = (100 <= chance ? TextFormatting.GOLD : TextFormatting.WHITE);
            if (regPokeBall.equals(DIVE_BALL) || regPokeBall.equals(DUSK_BALL) || regPokeBall.equals(LURE_BALL) || regPokeBall.equals(MOON_BALL) || regPokeBall.equals(REPEAT_BALL) || regPokeBall.equals(SAFARI_BALL))
                textColor = TextFormatting.GRAY;

            tooltip.add(new StringTextComponent(String.format("%s:  %.1f%%", ballName, chance)).withStyle(textColor));
        }

        // If balls on screen, show Tooltip
        if (!pmbattleinfo$balls.isEmpty()) {
            ScreenHelper.renderTooltip(matrix, xPos, yPos, tooltip, gradient, background, alpha, false, false);
        }
    }
}
