package com.inhumanity.pmbattleinfo.mixin.client;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.inhumanity.pmbattleinfo.config.ClientConfig;
import com.inhumanity.pmbattleinfo.RemoveNicknames;

import com.mojang.blaze3d.matrix.MatrixStack;

import com.pixelmonmod.pixelmon.api.pokemon.Element;
import com.pixelmonmod.pixelmon.client.gui.ScreenHelper;
import com.pixelmonmod.pixelmon.client.gui.battles.PixelmonClientData;
import com.pixelmonmod.pixelmon.client.gui.battles.pokemonOverlays.AllyElement;
import com.pixelmonmod.pixelmon.client.gui.widgets.PixelmonWidget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

@Mixin(value = AllyElement.class)
public abstract class AllyElementMixin extends PixelmonWidget {
    // Tooltip as a whole
    private final boolean pmbattleinfo$configTooltip = ClientConfig.battlePixelmonTooltip.get();

    @Shadow(remap = false)
    private PixelmonClientData ally;

    @Inject(method = "drawElement(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V", at = @At("HEAD"), remap = false)
    private void drawPixelmonBattleTooltipOurs(MatrixStack matrix, float scale, CallbackInfo ci) {
        RemoveNicknames.removeNickname(ally);

        if (!pmbattleinfo$configTooltip) return;

        // Values
        String nameDisplay = ally.getDisplayName();
        String nameSpecies = ally.species.getLocalizedName();
        String hpCurr = String.valueOf(ally.health.intValue());
        String hpMax = String.valueOf(ally.maxHealth);
        String types = String.join(", ", ally.getBaseStats().getTypes().stream().map(Element::getLocalizedName).toArray(String[]::new));
        String ability = ally.moveset.getAbility().getLocalizedName();
        String heldItemName = ally.heldItem.getLocalizedName();

        // Tooltip
        Collection<ITextComponent> arrTooltipText = new ArrayList<>();

        // Name Heading - Default: <nickname> (<species>)
        ITextComponent nameHeading = new StringTextComponent(String.format("%s (%s)", nameDisplay, nameSpecies)).withStyle(TextFormatting.GREEN).withStyle(TextFormatting.BOLD);
        if (Objects.equals(nameDisplay, nameSpecies))   nameHeading = new StringTextComponent(nameDisplay).withStyle(TextFormatting.GREEN).withStyle(TextFormatting.BOLD);
        else if (RemoveNicknames.getConfig())           nameHeading = new StringTextComponent(nameSpecies).withStyle(TextFormatting.GREEN).withStyle(TextFormatting.BOLD);
        arrTooltipText.add(nameHeading);

        // Basic Information
        arrTooltipText.add(new StringTextComponent(String.format("HP: %s / %s", hpCurr, hpMax)));
        arrTooltipText.add(new StringTextComponent(String.format("Types: %s", types)));
        arrTooltipText.add(new StringTextComponent(String.format("Ability: %s", ability)));
        arrTooltipText.add(new StringTextComponent(String.format("Held Item: %s", heldItemName)));

        // Hover Detection
        double mouseX = Minecraft.getInstance().mouseHandler.xpos();
        double mouseY = Minecraft.getInstance().mouseHandler.ypos();
        if (this.isMouseOver(mouseX, mouseY) || Screen.hasShiftDown()) {
            int gradient = Color.BLUE.getRGB();
            int background = Color.BLACK.getRGB();
            int alpha = 225;
            ScreenHelper.renderTooltip(matrix, 25, 65, arrTooltipText, gradient, background, alpha, false, false);
        }
    }
}
