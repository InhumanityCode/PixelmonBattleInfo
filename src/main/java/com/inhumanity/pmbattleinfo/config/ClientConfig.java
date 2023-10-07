package com.inhumanity.pmbattleinfo.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ClientConfig {
    public static final Builder BUILDER = new Builder();
    public static final ForgeConfigSpec SPEC;

    // Modules
    public static final ConfigValue<Boolean> battleRemoveNickname;
    public static final ConfigValue<Boolean> battlePixelmonTooltip;
    public static final ConfigValue<Boolean> battleMovesetEffectiveness;
    public static final ConfigValue<Boolean> battlePokeBallCatchChance;

    // Tooltip Specific
    public static final ConfigValue<Boolean> battleKnowEnemyHP;
    public static final ConfigValue<Boolean> battleKnowEnemyAbility;
    public static final ConfigValue<Boolean> battleKnowEnemyHeldItem;
    public static final ConfigValue<Boolean> battleKnowEnemyMoveset;

    static {
        BUILDER.push("Configs for Pixelmon Battle Info");

        // Modules
        battleRemoveNickname = BUILDER
            .comment("Replace Pixelmon Nicknames with Localized Species Name")
            .define("RemoveNicknames", true);

        battlePixelmonTooltip = BUILDER
            .comment("Display Tooltip when Hovering Pixelmon while in Battle (or hold 'alt' for enemy, 'shift' for yours)")
            .define("PixelmonTooltip", true);
        
        battleMovesetEffectiveness = BUILDER
            .comment("Display Your Moveset Effectiveness (1st enemy ONLY)")
            .define("YourMovesetEffectiveness", true);

        battlePokeBallCatchChance = BUILDER
            .comment("Display Poke Ball Catch Chance")
            .define("CatchChanceTooltip", true);


        // Tooltip Specific
        battleKnowEnemyHP = BUILDER
            .comment("In PixelmonTooltip, inherently know enemy exact HP")
            .define("TooltipEnemyHP", true);

        battleKnowEnemyAbility = BUILDER
            .comment("In PixelmonTooltip, inherently know enemy exact Ability")
            .define("TooltipEnemyAbility", true);

        battleKnowEnemyHeldItem = BUILDER
            .comment("In PixelmonTooltip, inherently know enemy exact Held Item")
            .define("TooltipEnemyHeldItem", true);
        
        battleKnowEnemyMoveset = BUILDER
            .comment("In PixelmonTooltip, inherently know enemy exact Moveset")
            .define("TooltipEnemyMoveset", true);
        
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
