package com.inhumanity.pmbattleinfo.util;

import com.pixelmonmod.api.registry.RegistryValue;

import com.pixelmonmod.pixelmon.api.pokemon.Element;
import com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBall;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.battles.status.StatusType;
import com.pixelmonmod.pixelmon.client.ClientProxy;
import com.pixelmonmod.pixelmon.client.gui.battles.PixelmonClientData;

import static com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBallRegistry.*;

public class PokeBallMath {
    public static double getCatchChance(final RegistryValue<PokeBall> regPokeBall, final PixelmonClientData enemy, final PixelmonClientData ours) {
        int catchRate = enemy.getBaseStats().getCatchRate();
        if (regPokeBall.equals(HEAVY_BALL))
            catchRate = modifyCatchRateHeavyBall(catchRate, enemy.getBaseStats().getWeight());

        double statusRate = getStatusRate(enemy);
        double ballRate = getBallRate(regPokeBall, enemy, ours);
        double percentHP = enemy.health.get() / enemy.maxHealth;

        double chance = (catchRate * ballRate * statusRate * (1 - ((double) 2/3) * percentHP)) / 255 * 100;
        if (ballRate == 255) chance = 100;

        return Math.min(100, chance);
    }

    private static double getBallRate(final RegistryValue<PokeBall> regPokeBall, final PixelmonClientData enemy, final PixelmonClientData ours) {
        double ballRate = regPokeBall.getValue().get().getCatchBonus();

        final int turnsPassed = ClientProxy.battleManager.battleTurn;

        //// Balls with Impossible Conditions
        // Dive Ball - don't know if Enemy in Water
        // Dusk Ball - don't know if Enemy in Darkness
        // Lure Ball - don't know how Enemy was found (IF Fishing)
        // Moon Ball - don't know if Enemy Evolutionary Tree uses Moon Stone
        // Repeat Ball - don't know if Enemy was has been Caught Before
        // Safari Ball - don't yet know if Player in Plains or Savanna type Biomes
        if (regPokeBall.equals(BEAST_BALL)) {
            ballRate = (enemy.species.isUltraBeast() ? 5 : 0.1);
        }
        else if (regPokeBall.equals(DREAM_BALL)) {
            if (StatusType.getEffect(enemy.status) == StatusType.Sleep)
                ballRate = 4;
        }
        else if (regPokeBall.equals(FAST_BALL)) {
            if (enemy.getBaseStats().getBattleStats().getStat(BattleStatsType.SPEED) >= 100)
                ballRate = 4;
        }
        else if (regPokeBall.equals(HEAVY_BALL)) {
            // Adds flat value to Enemy catch multiplier depending on weight - does not account for weight reducing moves
            ballRate = 1;
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
        else if (regPokeBall.equals(LOVE_BALL)) {
            if (enemy.species == ours.species && enemy.getGender() != ours.getGender())
                ballRate = 8;
        }
        else if (regPokeBall.equals(MASTER_BALL)) {
            ballRate = 255;
        }
        else if (regPokeBall.equals(NEST_BALL)) {
            ballRate = Math.max((41 - enemy.level) / 5, 1);
        }
        else if (regPokeBall.equals(NET_BALL)) {
            if (enemy.getBaseStats().getTypes().stream().anyMatch(type -> type == Element.BUG || type == Element.WATER))
                ballRate = 3.5;
        }
        else if (regPokeBall.equals(ORIGIN_BALL)) {
            ballRate = 255;
        }
        else if (regPokeBall.equals(PARK_BALL)) {
            ballRate = 255;
        }
        else if (regPokeBall.equals(QUICK_BALL)) {
            ballRate = (turnsPassed == 0 ? 5 : ballRate);
        }
        else if (regPokeBall.equals(TIMER_BALL)) {
            ballRate = Math.min(1 + 0.3 * (turnsPassed), 4);
        }

        // Ultra Beast Clause
        if (ballRate < 255 && enemy.species.isUltraBeast() && !regPokeBall.equals(BEAST_BALL))
            ballRate = 0.1;

        return ballRate;
    }

    private static int modifyCatchRateHeavyBall(int catchRate, double weight) {
        if (weight < 199.9)                             catchRate -= 20;
        else if (199.9 <= weight && weight < 299.9)     catchRate += 20;
        else if (299.9 <= weight && weight < 409.5)     catchRate += 30;
        else if (409.5 <= weight)                       catchRate += 40;

        return Math.min(255, catchRate);
    }

    private static double getStatusRate(final PixelmonClientData PCD) {
        double statusRate = 1;

        if (StatusType.getEffect(PCD.status) != null) {
            switch (StatusType.getEffect(PCD.status)) {
                case Poison: case PoisonBadly:
                case Burn:
                case Paralysis:
                    statusRate = 1.5; break;
                case Sleep:
                case Freeze:
                    statusRate = 2.5; break;
            }
        }

        return statusRate;
    }
}
