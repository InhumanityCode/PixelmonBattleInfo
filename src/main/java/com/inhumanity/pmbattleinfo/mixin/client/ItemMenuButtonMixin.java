package com.inhumanity.pmbattleinfo.mixin.client;

import com.inhumanity.pmbattleinfo.util.Tooltip;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.pixelmonmod.api.registry.RegistryValue;
import com.pixelmonmod.pixelmon.api.pokemon.Element;
import com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBall;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.battles.status.StatusType;
import com.pixelmonmod.pixelmon.client.ClientProxy;
import com.pixelmonmod.pixelmon.client.gui.ScreenHelper;
import com.pixelmonmod.pixelmon.client.gui.battles.PixelmonClientData;
import com.pixelmonmod.pixelmon.client.gui.battles.battleScreens.BattleMenuElement.MenuListButton;
import com.pixelmonmod.pixelmon.client.gui.battles.battleScreens.screens.bag.ItemMenuButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBallRegistry.*;

@Mixin(value = ItemMenuButton.class)
public abstract class ItemMenuButtonMixin extends MenuListButton {
    public ItemMenuButtonMixin(int id, Screen parent, ItemStack stack) {
        super(id, parent);
        this.stack = stack;
    }

    private static HashSet<String> balls = new HashSet<>();

    @Shadow
    private final ItemStack stack;

    @Shadow
    private String title;

    @Inject(method="renderButton(Lcom/mojang/blaze3d/matrix/MatrixStack;IIF)V", at = @At("TAIL"), remap = false)
    private void drawPixelmonPokeBallTooltip(MatrixStack matrix, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        final PixelmonClientData[] enemies = ClientProxy.battleManager.displayedEnemyPokemon;
        if (enemies == null) return;

        boolean onlyOneEnemy = (1 == Arrays.stream(enemies).filter(p -> p.health.intValue() > 0).count());
        if (!onlyOneEnemy) return;

        // Return if UI elements do not contain Ball
        String displayName = (this.title == null || this.title.isEmpty() ? this.stack.getHoverName().getString() : this.title);
        if (!displayName.toLowerCase().contains("ball") || displayName.toLowerCase().contains("balls")) {
            balls.clear();
            return;
        }

        // Enemy and Our Pixelmon have to exist if on this screen so ignore errors
        PixelmonClientData enemy = Arrays.stream(enemies).filter(p -> p.health.intValue() > 0).findFirst().get();
        PixelmonClientData ours = Arrays.stream(ClientProxy.battleManager.displayedOurPokemon).filter(p -> p.health.intValue() > 0).findFirst().get();

        // Clear every page length when Set contains more than page length
        if (balls.size() > 6)
            balls.clear();
        balls.add(displayName);

        // Tooltip Info
        int xPos = 10;
        int yPos = 10;
        int gradient = Tooltip.getGradient();
        int background = Tooltip.getBackground();
        int alpha = 255; // Override because this gets displayed overlapped like 6 times - will try to optimize this some other time

        int catchRate = enemy.getBaseStats().getCatchRate(); // Base enemy catch multiplier
        double statusRate = 1; // Multiplier based on certain Status of enemy
        if (StatusType.getEffect(enemy.status) != null) {
            switch (StatusType.getEffect(enemy.status)) {
                case Poison: case PoisonBadly:
                case Burn:
                case Paralysis:
                    statusRate = 1.5; break;
                case Sleep:
                case Freeze:
                    statusRate = 2.5; break;
            }
        }
        double percentHP = enemy.health.get() / enemy.maxHealth; // percent HP impacts catch chance

        // Tooltip Header
        ArrayList<ITextComponent> tooltip = new ArrayList<>();
        tooltip.add(new StringTextComponent("Catch Rates").withStyle(TextFormatting.BOLD).withStyle(TextFormatting.RED));

        // External Values that affect catch chance (ball multiplier)
        final int turnsPassed = ClientProxy.battleManager.battleTurn; // Turn 1 = 0 turns passed
//        final long currTime = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getGameTime() : 0; // For 1 condition of Dusk Ball

        // For each Ball stored
        for (String ballName : balls) {
            // Get the Ball's key name
            RegistryValue<PokeBall> regPokeBall = getPokeBall(ballName.toLowerCase().replace(" ", "_"));

            // Special Key Cases (primarily the accent on E in Poke)
            if (ballName.toLowerCase().contains("christmas pok")) regPokeBall = CHRISTMAS_BALL;
            else if (ballName.toLowerCase().contains("ancient pok")) regPokeBall = ANCIENT_POKE_BALL;
            else if (ballName.toLowerCase().contains("pok")) regPokeBall = POKE_BALL;

            // Just in case Ball not found, skip this Ball
            if (!regPokeBall.getValue().isPresent()) continue;

            PokeBall pokeBall = regPokeBall.getValue().get();

            //// Balls with Impossible Conditions
            // Dive Ball - don't know if Enemy in Water
            // Dusk Ball - don't know if Enemy in Darkness
            // Lure Ball - don't know how Enemy was found (IF Fishing)
            // Moon Ball - don't know if Enemy Evolutionary Tree uses Moon Stone
            // Repeat Ball - don't know if Enemy was has been Caught Before
            // Safari Ball - don't yet know if Player in Plains or Savanna type Biomes

            // Get Ball multiplier
            double ballRate = pokeBall.getCatchBonus();
            if (regPokeBall.equals(BEAST_BALL)) ballRate = (enemy.species.isUltraBeast() ? 5 : 0.1);
            else if (regPokeBall.equals(DREAM_BALL)) ballRate = (StatusType.getEffect(enemy.status) == StatusType.Sleep ? 4 : ballRate);
//            else if (regPokeBall.equals(DUSK_BALL)) ballRate = (13000 <= currTime && currTime <= 23000 ? 3 : ballRate);
            else if (regPokeBall.equals(FAST_BALL)) ballRate = (enemy.getBaseStats().getBattleStats().getStat(BattleStatsType.SPEED) >= 100 ? 4 : ballRate);
            else if (regPokeBall.equals(HEAVY_BALL)) {
                // Adds flat value to Enemy catch multiplier depending on weight - does not account for weight reducing moves
                ballRate = 1;
                double weight = enemy.getBaseStats().getWeight();

                if (weight < 199.9) catchRate -= 20;
                else if (199.9 <= weight && weight < 299.9) catchRate += 20;
                else if (299.9 <= weight && weight < 409.5) catchRate += 30;
                else if (409.5 <= weight) catchRate += 40;
                // If catch rat
                catchRate = Math.min(255, catchRate);
            }
            else if (regPokeBall.equals(LEVEL_BALL)) {
                int enemyLevel = enemy.level;
                int ourLevel = ours.level;
                double levelMulti = (double) ourLevel / enemyLevel;

                // Lever range multiplier rates
                if (enemyLevel < ourLevel && levelMulti < 2) ballRate = 2;
                else if (2 <= levelMulti && levelMulti < 4) ballRate = 4;
                else if (4 <= levelMulti) ballRate = 8;
            }
            else if (regPokeBall.equals(LOVE_BALL)) ballRate = (enemy.species == ours.species && enemy.getGender() != ours.getGender() ? 8 : ballRate);
            else if (regPokeBall.equals(MASTER_BALL)) ballRate = 255;
            else if (regPokeBall.equals(NEST_BALL)) ballRate = Math.max((41 - enemy.level) / 5, 1);
            else if (regPokeBall.equals(NET_BALL)) ballRate = (enemy.getBaseStats().getTypes().stream().anyMatch(type -> type == Element.BUG || type == Element.WATER) ? 3.5 : ballRate);
            else if (regPokeBall.equals(ORIGIN_BALL)) ballRate = 255;
            else if (regPokeBall.equals(PARK_BALL)) ballRate = 255;
            else if (regPokeBall.equals(QUICK_BALL)) ballRate = (turnsPassed == 0 ? 5 : ballRate);
            else if (regPokeBall.equals(TIMER_BALL)) ballRate = Math.min(1 + 0.3 * (turnsPassed), 4);

            // If enemy is Ultra Beast, and Ball is not Ultra Beast, ball multiplier is 0.1
            ballRate = enemy.species.isUltraBeast() && !regPokeBall.equals(BEAST_BALL) ? 0.1 : ballRate;

            // Cap chance at 100%
            double chance = Math.min(100, ballRate == 255 ? 100 : (catchRate * ballRate * statusRate * (1 - ((double) 2/3) * percentHP)) / 255 * 100);

            // Text is base white, gold if >=100%, and gray if not fully supported
            TextFormatting textColor = (100 <= chance ? TextFormatting.GOLD : TextFormatting.WHITE);
            if (regPokeBall.equals(DIVE_BALL) || regPokeBall.equals(DUSK_BALL) || regPokeBall.equals(LURE_BALL) || regPokeBall.equals(MOON_BALL) || regPokeBall.equals(REPEAT_BALL) || regPokeBall.equals(SAFARI_BALL))
                textColor = TextFormatting.GRAY;

            tooltip.add(new StringTextComponent(String.format("%s:  %.1f%%", ballName, chance)).withStyle(textColor));
        }

        // If balls on screen, show Tooltip
        if (!balls.isEmpty()) {
            ScreenHelper.renderTooltip(matrix, xPos, yPos, tooltip, gradient, background, alpha, false, false);
        }
    }
}
