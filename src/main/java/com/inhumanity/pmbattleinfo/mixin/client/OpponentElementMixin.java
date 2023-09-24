package com.inhumanity.pmbattleinfo.mixin.client;

import com.inhumanity.pmbattleinfo.config.ClientConfig;
import com.inhumanity.pmbattleinfo.RemoveNicknames;

import com.mojang.blaze3d.matrix.MatrixStack;

import com.pixelmonmod.pixelmon.api.pokemon.Element;
import com.pixelmonmod.pixelmon.api.pokemon.stats.Moveset;
import com.pixelmonmod.pixelmon.battles.api.rules.clauses.BattleClauseRegistry;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.attacks.Effectiveness;
import com.pixelmonmod.pixelmon.client.ClientProxy;
import com.pixelmonmod.pixelmon.client.gui.ScreenHelper;
import com.pixelmonmod.pixelmon.client.gui.battles.PixelmonClientData;
import com.pixelmonmod.pixelmon.client.gui.battles.pokemonOverlays.OpponentElement;
import com.pixelmonmod.pixelmon.client.gui.widgets.PixelmonWidget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Mixin(value = OpponentElement.class)
public abstract class OpponentElementMixin extends PixelmonWidget {
    // Tooltip as a whole
    private final boolean pmbattleinfo$configTooltip = ClientConfig.battlePixelmonTooltip.get();

    // Tooltip Specific
    private final boolean pmbattleinfo$configHealth = ClientConfig.battleKnowEnemyHP.get();

    private final boolean pmbattleinfo$configAbility = ClientConfig.battleKnowEnemyAbility.get();

    private final boolean pmbattleinfo$configHeldItem = ClientConfig.battleKnowEnemyHeldItem.get();
    private final boolean pmbattleinfo$configKnowMoveset = ClientConfig.battleKnowEnemyMoveset.get();

    @Shadow(remap = false)
    private PixelmonClientData enemy;

    @Inject(method = "drawElement(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V", at = @At("HEAD"), remap = false)
    private void drawPixelmonBattleTooltipEnemy(MatrixStack matrix, float scale, CallbackInfo ci) {
        RemoveNicknames.removeNickname(enemy);
        
        if (!pmbattleinfo$configTooltip) return;

        // Values
        String nameDisplay = enemy.getDisplayName();
        String nameSpecies = enemy.species.getLocalizedName();
        String hpCurr = (pmbattleinfo$configHealth ? String.valueOf(enemy.health.intValue()) : "?");
        String hpMax = (pmbattleinfo$configHealth ? String.valueOf(enemy.maxHealth) : "?");
        String types = String.join(", ", enemy.getBaseStats().getTypes().stream().map(Element::getLocalizedName).toArray(String[]::new));
        Moveset moveset = enemy.moveset;
        String ability = (pmbattleinfo$configAbility ? enemy.moveset.getAbility().getLocalizedName() : "?");
        String heldItemName = (pmbattleinfo$configHeldItem ? enemy.heldItem.getLocalizedName() : "?");

        // Tooltip
        Collection<ITextComponent> arrTooltipText = new ArrayList<>();

        // Name Heading - Default: <nickname> (<species>)
        ITextComponent nameHeading = new StringTextComponent(String.format("%s (%s)", nameDisplay, nameSpecies)).withStyle(TextFormatting.RED).withStyle(TextFormatting.BOLD);
        if (Objects.equals(nameDisplay, nameSpecies))   nameHeading = new StringTextComponent(nameDisplay).withStyle(TextFormatting.RED).withStyle(TextFormatting.BOLD);
        else if (RemoveNicknames.getConfig())           nameHeading = new StringTextComponent(nameSpecies).withStyle(TextFormatting.RED).withStyle(TextFormatting.BOLD);
        arrTooltipText.add(nameHeading);

        // Basic Information
        arrTooltipText.add(new StringTextComponent(String.format("HP: %s / %s", hpCurr, hpMax)));
        arrTooltipText.add(new StringTextComponent(String.format("Types: %s", types)));
        arrTooltipText.add(new StringTextComponent(String.format("Ability: %s", ability)));
        arrTooltipText.add(new StringTextComponent(String.format("Held Item: %s", heldItemName)));

        // Move List
        if (pmbattleinfo$configKnowMoveset) {
            boolean isInverseBattle = ClientProxy.battleManager.rules.hasClause(BattleClauseRegistry.INVERSE_BATTLE);

            arrTooltipText.add(new StringTextComponent(""));
            arrTooltipText.add(new StringTextComponent("Moves Effective to You").withStyle(TextFormatting.YELLOW).withStyle(TextFormatting.BOLD));
            for (Attack atk : moveset.attacks) {
                if (atk != null && atk.getMove() != null) {
                    String atkName = atk.getMove().getAttackName();
                    String atkType = atk.getType().getLocalizedName();
                    String atkCategory = atk.getAttackCategory().name();
                    switch (atkCategory) {
                        case "PHYSICAL":    atkCategory = "phy";    break;
                        case "SPECIAL":     atkCategory = "spe";    break;
                        case "STATUS":      atkCategory = "sta";    break;
                        default:            atkCategory = "-";
                    }

                    int ppCurr = atk.pp;
                    String atkAccuracy = (atk.getMove().getAccuracy() == -1 ? "-" : String.valueOf(atk.getMove().getAccuracy()));
                    String atkPower = String.valueOf(atk.movePower);

                    PixelmonClientData ourPM = ClientProxy.battleManager.displayedOurPokemon[0];

                    TextFormatting format = TextFormatting.DARK_GRAY;
                    double multiplier = Element.getTotalEffectiveness(ourPM.getBaseStats().getTypes(), atk.getType(), isInverseBattle);
                    if (multiplier == Effectiveness.Max.value) format = TextFormatting.DARK_GREEN;
                    else if (multiplier == Effectiveness.Super.value) format = TextFormatting.GREEN;
                    else if (multiplier == Effectiveness.Normal.value) format = TextFormatting.WHITE;
                    else if (multiplier == Effectiveness.Not.value) format = TextFormatting.RED;
                    else if (multiplier == Effectiveness.Barely.value) format = TextFormatting.DARK_RED;

                    arrTooltipText.add(new StringTextComponent(String.format("%2dpp, %s : %s", ppCurr, atkType, atkName)).withStyle(format));
                    arrTooltipText.add(new StringTextComponent(String.format("   CAT: %s, POW: %s, ACC: %s", atkCategory, atkPower, atkAccuracy)).withStyle(format));
                }
            }
        }

        // Hover Detection
        double mouseX = Minecraft.getInstance().mouseHandler.xpos();
        double mouseY = Minecraft.getInstance().mouseHandler.ypos();
        if (this.isMouseOver(mouseX, mouseY) || Screen.hasAltDown()) {
            int gradient = Color.BLUE.getRGB();
            int background = Color.BLACK.getRGB();
            int alpha = 225;
            ScreenHelper.renderTooltip(matrix, 25, 65, arrTooltipText, gradient, background, alpha, false, false);
        }
    }
}