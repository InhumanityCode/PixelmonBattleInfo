package com.inhumanity.pmbattleinfo.init;

import com.inhumanity.pmbattleinfo.ModFile;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.awt.event.KeyEvent;

@OnlyIn(Dist.CLIENT)
public class KeybindsInit {
    private enum KeybindCategory {
        General("general"),
        Tooltip("tooltip");

        private final String category;

        KeybindCategory(String category) {
            this.category = category;
        }

        public String getCategory() {
            return category;
        }
    }

    // Allies
    public static KeyBinding kbBattleTTAlly_1;
    public static KeyBinding kbBattleTTAlly_2;
    public static KeyBinding kbBattleTTAlly_3;
    public static KeyBinding kbBattleTTAlly_4;
    public static KeyBinding kbBattleTTAlly_5;
    // Enemies
    public static KeyBinding kbBattleTTEnemy_1;
    public static KeyBinding kbBattleTTEnemy_2;
    public static KeyBinding kbBattleTTEnemy_3;
    public static KeyBinding kbBattleTTEnemy_4;
    public static KeyBinding kbBattleTTEnemy_5;

    public static void init() {
        kbBattleTTAlly_1 = register("battle.tt_ally_1", KeyEvent.VK_1, KeybindCategory.Tooltip.getCategory());
        kbBattleTTAlly_2 = register("battle.tt_ally_2", KeyEvent.VK_2, KeybindCategory.Tooltip.getCategory());
        kbBattleTTAlly_3 = register("battle.tt_ally_3", KeyEvent.VK_3, KeybindCategory.Tooltip.getCategory());
        kbBattleTTAlly_4 = register("battle.tt_ally_4", KeyEvent.VK_4, KeybindCategory.Tooltip.getCategory());
        kbBattleTTAlly_5 = register("battle.tt_ally_5", KeyEvent.VK_5, KeybindCategory.Tooltip.getCategory());

        kbBattleTTEnemy_1 = register("battle.tt_enemy_1", KeyEvent.VK_6, KeybindCategory.Tooltip.getCategory());
        kbBattleTTEnemy_2 = register("battle.tt_enemy_2", KeyEvent.VK_7, KeybindCategory.Tooltip.getCategory());
        kbBattleTTEnemy_3 = register("battle.tt_enemy_3", KeyEvent.VK_8, KeybindCategory.Tooltip.getCategory());
        kbBattleTTEnemy_4 = register("battle.tt_enemy_4", KeyEvent.VK_9, KeybindCategory.Tooltip.getCategory());
        kbBattleTTEnemy_5 = register("battle.tt_enemy_5", KeyEvent.VK_0, KeybindCategory.Tooltip.getCategory());

        ModFile.getLogger().info("Finish Keybinds setup");
    }

    private static KeyBinding register(String name, int key, String category) {
        KeyBinding kb = new KeyBinding(
                String.format("key.%s.%s", ModFile.MOD_ID, name),
                KeyConflictContext.GUI,
                InputMappings.Type.KEYSYM,
                key,
                String.format("key.category.%s.%s", ModFile.MOD_ID, category)
        );
        ClientRegistry.registerKeyBinding(kb);
        return kb;
    }
}
