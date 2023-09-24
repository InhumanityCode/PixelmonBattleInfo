package com.inhumanity.pmbattleinfo.mixin.client;

import java.awt.Color;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.inhumanity.pmbattleinfo.config.ClientConfig;

import com.pixelmonmod.pixelmon.api.pokemon.Element;
import com.pixelmonmod.pixelmon.battles.api.rules.clauses.BattleClauseRegistry;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.attacks.Effectiveness;
import com.pixelmonmod.pixelmon.client.ClientProxy;
import com.pixelmonmod.pixelmon.client.gui.battles.PixelmonClientData;
import com.pixelmonmod.pixelmon.client.gui.battles.battleScreens.ChooseAttack.MoveButton;

@Mixin(value = MoveButton.class)
public abstract class MoveButtonMixin {
    @Unique
    private final boolean pmbattleinfo$configColoredMoveset = ClientConfig.battleMovesetEffectiveness.get();

    @Shadow(remap = false)
    private Attack attack;

    @ModifyArgs(method = "renderButton", at = @At(value = "INVOKE", target = "Lcom/pixelmonmod/pixelmon/client/gui/ScreenHelper;drawImage(Lnet/minecraft/util/ResourceLocation;Lcom/mojang/blaze3d/matrix/MatrixStack;FFFFFFFFF)V"))
    private void modifyRGB(Args args) {
        if (!pmbattleinfo$configColoredMoveset) return;

        Color color = pmbattleinfo$getEffectivenessColor();

        args.set(6, color.getRed() / (float) 255);          // R
        args.set(7, color.getGreen() / (float) 255);        // G
        args.set(8, color.getBlue() / (float) 255);         // B
    }

    @Unique
    private Color pmbattleinfo$getEffectivenessColor() {
        boolean isInverseBattle = ClientProxy.battleManager.rules.hasClause(BattleClauseRegistry.INVERSE_BATTLE);

        PixelmonClientData enemy = ClientProxy.battleManager.displayedEnemyPokemon[0];

        double multiplier = Element.getTotalEffectiveness(enemy.getBaseStats().getTypes(), attack.getType(), isInverseBattle);

        if (multiplier == Effectiveness.Max.value)          return new Color(0, 170, 0);        // DARK_GREEN
        else if (multiplier == Effectiveness.Super.value)   return new Color(85, 255, 85);      // GREEN
        else if (multiplier == Effectiveness.Normal.value)  return new Color(255, 255, 255);    // WHITE
        else if (multiplier == Effectiveness.Not.value)     return new Color(255, 85, 85);      // RED
        else if (multiplier == Effectiveness.Barely.value)  return new Color(170, 0, 0);        // DARK_RED
        else                                                return new Color(85, 85, 85);    // GRAY
    }
}
