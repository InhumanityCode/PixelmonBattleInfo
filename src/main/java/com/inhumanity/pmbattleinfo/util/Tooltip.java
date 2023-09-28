package com.inhumanity.pmbattleinfo.util;

import com.inhumanity.pmbattleinfo.config.ClientConfig;

import com.pixelmonmod.pixelmon.api.pokemon.Element;
import com.pixelmonmod.pixelmon.api.pokemon.stats.Moveset;
import com.pixelmonmod.pixelmon.battles.api.rules.clauses.BattleClauseRegistry;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.attacks.Effectiveness;
import com.pixelmonmod.pixelmon.client.ClientProxy;
import com.pixelmonmod.pixelmon.client.gui.battles.PixelmonClientData;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;

public class Tooltip {
    private static final int gradient = Color.BLUE.getRGB();
    private static final int background = Color.BLACK.getRGB();
    private static final int alpha = 225;

    public static int getGradient() { return gradient; }
    public static int getBackground() { return background; }
    public static int getAlpha() { return alpha; }

    public static Collection<ITextComponent> getTooltip(final PixelmonClientData PCD) {
        Collection<ITextComponent> res = new ArrayList<>();
        if (PCD == null) return res;

        boolean isEnemy = Arrays.stream(ClientProxy.battleManager.displayedEnemyPokemon).anyMatch(p -> p.pokemonUUID == PCD.pokemonUUID);
        boolean isOurs = ClientProxy.battleManager.displayedOurPokemon != null && (!isEnemy && Arrays.stream(ClientProxy.battleManager.displayedOurPokemon).anyMatch(p -> p.pokemonUUID == PCD.pokemonUUID));

        boolean hasMultipleOurs = (ClientProxy.battleManager.displayedOurPokemon != null && ClientProxy.battleManager.displayedOurPokemon.length > 1);

        final boolean configKnowMoveset = ClientConfig.battleKnowEnemyMoveset.get();

        HashMap<TooltipValues, ITextComponent> attributes = new HashMap<>();

        // Name Heading
        TextFormatting nameColor = (isOurs ? TextFormatting.AQUA : (isEnemy ? TextFormatting.RED : TextFormatting.GREEN ));
        attributes.put(TooltipValues.Name, newSTC(getNameToUse(PCD)).withStyle(nameColor).withStyle(TextFormatting.BOLD));

        // General Attributes
        attributes.put(TooltipValues.Types, newSTC("Types: " + String.join(", ", PCD.getBaseStats().getTypes().stream().map(Element::getLocalizedName).toArray(String[]::new))));
        attributes.put(TooltipValues.Palette, newSTC("Palette: " + PCD.palette));
        attributes.put(TooltipValues.Weight, newSTC("Weight: " + PCD.getBaseStats().getWeight()));
        attributes.put(TooltipValues.Form, newSTC("Form: " + PCD.form));

        // Add Ownership / Config determined Attributes
        if (isOurs && !hasMultipleOurs)     attributes.putAll(getTooltipOurs(PCD));
        else                               attributes.putAll(getTooltipEnemyAlly(PCD, isEnemy));

        // CREATE TOOLTIP
        res.add(attributes.get(TooltipValues.Name));
        res.add(attributes.get(TooltipValues.HP));
        res.add(attributes.get(TooltipValues.Types));
        res.add(attributes.get(TooltipValues.Ability));
        res.add(attributes.get(TooltipValues.HeldItem));
        res.add(attributes.get(TooltipValues.Form));
        res.add(attributes.get(TooltipValues.Palette));
        res.add(attributes.get(TooltipValues.Weight));

        // Add Moves if Ally or Enemy
        if ((isOurs && hasMultipleOurs) || (!isOurs && configKnowMoveset)) {
            res.add(newSTC(""));

            String title = (isEnemy ? "Moves Effective to You" : "Moves");
            res.add(newSTC(title).withStyle(TextFormatting.GOLD).withStyle(TextFormatting.BOLD));

            // Add moves if they exist
            if (attributes.get(TooltipValues.Move1) != null) {
                res.add(attributes.get(TooltipValues.Move1));
                res.add(attributes.get(TooltipValues.Move1Info));
            }
            if (attributes.get(TooltipValues.Move2) != null) {
                res.add(attributes.get(TooltipValues.Move2));
                res.add(attributes.get(TooltipValues.Move2Info));
            }
            if (attributes.get(TooltipValues.Move3) != null) {
                res.add(attributes.get(TooltipValues.Move3));
                res.add(attributes.get(TooltipValues.Move3Info));
            }
            if (attributes.get(TooltipValues.Move4) != null) {
                res.add(attributes.get(TooltipValues.Move4));
                res.add(attributes.get(TooltipValues.Move4Info));
            }
        }

        return res;
    }

    private static HashMap<TooltipValues, ITextComponent> getTooltipOurs(final PixelmonClientData ours) {
        HashMap<TooltipValues, ITextComponent> res = new HashMap<>();

        res.put(TooltipValues.HP, newSTC(String.format("HP: %d / %d", ours.health.intValue(), ours.maxHealth)));
        res.put(TooltipValues.Ability, newSTC("Ability: " + ours.moveset.getAbility().getLocalizedName()));
        res.put(TooltipValues.HeldItem, newSTC("Held Item: " + ours.heldItem.getLocalizedName()));

        // HP: curr / max
        // Ability
        // Held Item
        return res;
    }

    private static HashMap<TooltipValues, ITextComponent> getTooltipEnemyAlly(final PixelmonClientData AllyOrEnemy, boolean isEnemy) {
        HashMap<TooltipValues, ITextComponent> res = new HashMap<>();

        final boolean configHealth = ClientConfig.battleKnowEnemyHP.get();
        final boolean configAbility = ClientConfig.battleKnowEnemyAbility.get();
        final boolean configHeldItem = ClientConfig.battleKnowEnemyHeldItem.get();
        final boolean configKnowMoveset = ClientConfig.battleKnowEnemyMoveset.get();

        String hpCurr = (!configHealth ? "?" : String.valueOf(AllyOrEnemy.health.intValue()));
        String hpMax = (!configHealth ? "?" : String.valueOf(AllyOrEnemy.maxHealth));
        String ability = (!configAbility ? "?" : AllyOrEnemy.moveset.getAbility().getLocalizedName());
        String heldItemName = (!configHeldItem ? "?" : AllyOrEnemy.heldItem.getLocalizedName());

        res.put(TooltipValues.HP, newSTC(String.format("HP: %s / %s", hpCurr, hpMax)));
        res.put(TooltipValues.Ability, newSTC("Ability: " + ability));
        res.put(TooltipValues.HeldItem, newSTC("Held Item: " + heldItemName));

        // If moveset config on
        if (configKnowMoveset) {
            Moveset moveset = AllyOrEnemy.moveset;

            int i = 1;
            for (Attack atk : moveset.attacks) {
                if (atk != null && atk.getMove() != null) {
                    int ppCurr = atk.pp;
                    String atkType = atk.getType().getLocalizedName();
                    String atkName = atk.getMove().getAttackName();

                    TooltipValues enumMove = null;
                    TooltipValues enumMoveInfo = null;

                    switch (i) {
                        case 1:
                            enumMove = TooltipValues.Move1;
                            enumMoveInfo = TooltipValues.Move1Info;
                            break;
                        case 2:
                            enumMove = TooltipValues.Move2;
                            enumMoveInfo = TooltipValues.Move2Info;
                            break;
                        case 3:
                            enumMove = TooltipValues.Move3;
                            enumMoveInfo = TooltipValues.Move3Info;
                            break;
                        case 4:
                            enumMove = TooltipValues.Move4;
                            enumMoveInfo = TooltipValues.Move4Info;
                            break;
                    }

                    String atkCategory = atk.getAttackCategory().name();
                    switch (atkCategory) {
                        case "PHYSICAL":    atkCategory = "phy";    break;
                        case "SPECIAL":     atkCategory = "spe";    break;
                        case "STATUS":      atkCategory = "sta";    break;
                        default:            atkCategory = "-";
                    }
                    int atkPower = atk.movePower;
                    String atkAccuracy = (atk.getMove().getAccuracy() == -1 ? "-" : String.valueOf(atk.getMove().getAccuracy()));

                    // If Ally, default font color (WHITE). Else is Enemy, so color determined by Effectiveness to OUR Pixelmon
                    TextFormatting color = (!isEnemy ? TextFormatting.WHITE : getMultiplierColor(Element.getTotalEffectiveness(getOurTypes(), atk.getType(), isInverseBattle())));
                    res.put(enumMove, newSTC(String.format("%2dpp, %s : %s", ppCurr, atkType, atkName)).withStyle(color));
                    res.put(enumMoveInfo, newSTC(String.format("   CAT: %s, POW: %d, ACC: %s", atkCategory, atkPower, atkAccuracy)).withStyle(color));
                }

                i++;
            }
        }

        // HP: curr / max   (CONFIG)
        // Ability          (CONFIG)
        // Held Item        (CONFIG)
        // Move Info P1     (CONFIG)
        // Move Info P2     (^ SAME CONFIG)
        return res;
    }

    private static StringTextComponent newSTC(String str) {
        return new StringTextComponent(str);
    }

    private static String getNameToUse(final PixelmonClientData PCD) {
        String nameDisplay = PCD.getDisplayName();
        String nameSpecies = PCD.species.getLocalizedName();

        String name;
        if (nameDisplay.equals(nameSpecies))    name = nameDisplay;
        else if (RemoveNicknames.getConfig())   name = nameSpecies;
        else                                    name = String.format("%s (%s)", nameDisplay, nameSpecies);

        return name;
    }

    private static TextFormatting getMultiplierColor(double m) {
        if      (m == Effectiveness.Max.value)      return TextFormatting.DARK_GREEN;
        else if (m == Effectiveness.Super.value)    return TextFormatting.GREEN;
        else if (m == Effectiveness.Normal.value)   return TextFormatting.WHITE;
        else if (m == Effectiveness.Not.value)      return TextFormatting.RED;
        else if (m == Effectiveness.Barely.value)   return TextFormatting.DARK_RED;
        else                                        return TextFormatting.DARK_GRAY;
    }

    private static boolean isInverseBattle() {
        return ClientProxy.battleManager.rules.hasClause(BattleClauseRegistry.INVERSE_BATTLE);
    }

    private static List<Element> getOurTypes() {
        return ClientProxy.battleManager.displayedOurPokemon[0].getBaseStats().getTypes();
    }
}
