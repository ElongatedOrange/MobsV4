package me.orange.mobsv3.mobs;

import me.orange.mobsv3.MobsV3;
import me.orange.mobsv3.hex.HexUtils;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class Cooldowns {
    public HashMap<UUID, Integer> SLOWDOWN = new HashMap<>();

    public HashMap<UUID, Long> TURTLE = new HashMap<>();
    public HashMap<UUID, Long> WARDEN = new HashMap<>();
    public HashMap<UUID, Long> WARDEN_ALT = new HashMap<>();
    public HashMap<UUID, Long> CREEPER = new HashMap<>();
    public HashMap<UUID, Long> CREEPER_ALT = new HashMap<>();
    public HashMap<UUID, Long> SKELETON = new HashMap<>();
    public HashMap<UUID, Long> CHICKEN = new HashMap<>();
    public HashMap<UUID, Long> CHICKEN_ALT = new HashMap<>();
    public HashMap<UUID, Long> BLAZE = new HashMap<>();
    public HashMap<UUID, Long> SPIDER = new HashMap<>();
    public HashMap<UUID, Long> ENDERMAN = new HashMap<>();
    public HashMap<UUID, Long> ZOMBIE = new HashMap<>();
    public HashMap<UUID, Long> VILLAGER = new HashMap<>();
    public HashMap<UUID, Long> VILLAGER_ALT = new HashMap<>();
    public HashMap<UUID, Long> DRAGON = new HashMap<>();
    public HashMap<UUID, Long> DRAGON_ALT = new HashMap<>();
    public HashMap<UUID, Long> SHULKER = new HashMap<>();
    public HashMap<UUID, Long> SHULKER_ALT = new HashMap<>();
    public HashMap<UUID, Long> PIGLIN = new HashMap<>();
    public HashMap<UUID, Long> WITHER = new HashMap<>();
    public HashMap<UUID, Long> WITHER_ALT = new HashMap<>();
    public HashMap<UUID, Long> WITCH = new HashMap<>();
    public HashMap<UUID, Long> WITCH_ALT = new HashMap<>();
    public HashMap<UUID, Long> ELDER_GUARDIAN = new HashMap<>();

    public static Boolean handleCooldown(Player p, String name) {
        String isAlt = "";
        String displayName = name;

        if (name.contains("-Alt")) {
            isAlt = " alternate";
            displayName = displayName.replace("-Alt", "");
        }

        displayName = displayName.replace("_", " ");


        if (MobsV3.COOLDOWNS.onCooldown(name, p, p.getInventory().getItemInMainHand()) > 0) {
            if (!MobsV3.COOLDOWNS.SLOWDOWN.containsKey(p.getUniqueId()))
                MobsV3.COOLDOWNS.SLOWDOWN.put(p.getUniqueId(), 0);

            if (MobsV3.COOLDOWNS.SLOWDOWN.get(p.getUniqueId()) >= 4) {
                // lol dont run the other nerd stuff
            } else if (MobsV3.COOLDOWNS.SLOWDOWN.get(p.getUniqueId()) == 3) {
                slowdownTick(p);

                p.sendMessage(HexUtils.format("#A5133B----------------------------------------------\n") +
                        "§cSlow Down!\n" +
                        HexUtils.format("#A5133B----------------------------------------------"));
            } else {
                slowdownTick(p);

                p.sendMessage("§9----------------------------------------------\n" +
                        "§cThe " + displayName + "'s" + isAlt + " ability is currently on cooldown! " +
                        "§e(" + MobsV3.COOLDOWNS.onCooldown(name, p, p.getInventory().getItemInMainHand()) +
                        "/" + MobsV3.COOLDOWNS.getCooldown(name, p.getInventory().getItemInMainHand()) + ")\n" +
                        "§9----------------------------------------------");
            }

            return true;
        } else {
            MobsV3.COOLDOWNS.setCooldown(name, p, System.currentTimeMillis());

            final String finName = displayName;
            final String finAlt = isAlt;

            MobsV3.MOBS.scheduleTaskLater(() -> {
                p.sendMessage("§6----------------------------------------------\n" +
                        "§aThe " + finName + "'s" + finAlt + " ability is ready!\n" +
                        "§6----------------------------------------------");

                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
            }, MobsV3.COOLDOWNS.onCooldown(name, p, p.getInventory().getItemInMainHand()) * 20);

            return false;
        }
    }

    public static void slowdownTick(Player p) {
        final int old;

        MobsV3.COOLDOWNS.SLOWDOWN.replace(p.getUniqueId(), MobsV3.COOLDOWNS.SLOWDOWN.get(p.getUniqueId()) + 1);
        old = MobsV3.COOLDOWNS.SLOWDOWN.get(p.getUniqueId()) + 1;

        MobsV3.MOBS.scheduleTaskLater(() -> {
            if (old == MobsV3.COOLDOWNS.SLOWDOWN.get(p.getUniqueId()))
                MobsV3.COOLDOWNS.SLOWDOWN.replace(p.getUniqueId(), 0);
        }, 60);
    }

    public HashMap<UUID, Long> getType(String type) {
        HashMap<UUID, Long> COOLDOWN = new HashMap<>();

        if (type.equalsIgnoreCase("Turtle"))
            COOLDOWN = TURTLE;
        else if (type.equalsIgnoreCase("Warden"))
            COOLDOWN = WARDEN;
        else if (type.equalsIgnoreCase("Creeper"))
            COOLDOWN = CREEPER;
        else if (type.equalsIgnoreCase("Skeleton"))
            COOLDOWN = SKELETON;
        else if (type.equalsIgnoreCase("Chicken"))
            COOLDOWN = CHICKEN;
        else if (type.equalsIgnoreCase("Blaze"))
            COOLDOWN = BLAZE;
        else if (type.equalsIgnoreCase("Spider"))
            COOLDOWN = SPIDER;
        else if (type.equalsIgnoreCase("Enderman"))
            COOLDOWN = ENDERMAN;
        else if (type.equalsIgnoreCase("Zombie"))
            COOLDOWN = ZOMBIE;
        else if (type.equalsIgnoreCase("Villager"))
            COOLDOWN = VILLAGER;
        else if (type.equalsIgnoreCase("Dragon"))
            COOLDOWN = DRAGON;
        else if (type.equalsIgnoreCase("Shulker"))
            COOLDOWN = SHULKER;
        else if (type.equalsIgnoreCase("Piglin"))
            COOLDOWN = PIGLIN;
        else if (type.equalsIgnoreCase("Wither"))
            COOLDOWN = WITHER;
        else if (type.equalsIgnoreCase("Warden-Alt"))
            COOLDOWN = WARDEN_ALT;
        else if (type.equalsIgnoreCase("Creeper-Alt"))
            COOLDOWN = CREEPER_ALT;
        else if (type.equalsIgnoreCase("Dragon-Alt"))
            COOLDOWN = DRAGON_ALT;
        else if (type.equalsIgnoreCase("Shulker-Alt"))
            COOLDOWN = SHULKER_ALT;
        else if (type.equalsIgnoreCase("Wither-Alt"))
            COOLDOWN = WITHER_ALT;
        else if (type.equalsIgnoreCase("Witch"))
            COOLDOWN = WITCH;
        else if (type.equalsIgnoreCase("Witch-Alt"))
            COOLDOWN = WITCH_ALT;
        else if (type.equalsIgnoreCase("Elder_Guardian"))
            COOLDOWN = ELDER_GUARDIAN;
        else if (type.equalsIgnoreCase("Chicken-Alt"))
            COOLDOWN = CHICKEN_ALT;
        else if (type.equalsIgnoreCase("Villager-Alt")) {
            COOLDOWN = VILLAGER_ALT;
        }

        return COOLDOWN;
    }

    public int getCooldown(String type, ItemStack item) {
        return getSeconds(getType(type));
    }

    public int getSeconds(HashMap<UUID, Long> COOLDOWN) {
        int seconds = 60;
        int levelBonus = 0;

        return seconds;
    }

    public Long onCooldown(String type, Player key, ItemStack item) {
        HashMap<UUID, Long> COOLDOWN = getType(type);
        int seconds = getSeconds(COOLDOWN);

        if (COOLDOWN.containsKey(key.getUniqueId())) {
            return ((COOLDOWN.get(key.getUniqueId()) / 1000) + seconds) - (System.currentTimeMillis() / 1000);
        }

        return 0L;
    }

    public void setCooldown(String type, Player key, Long value) {
        HashMap<UUID, Long> COOLDOWN = getType(type);
        if (COOLDOWN.containsKey(key.getUniqueId())) {
            COOLDOWN.replace(key.getUniqueId(), value);
        } else COOLDOWN.put(key.getUniqueId(), value);

    }
}
