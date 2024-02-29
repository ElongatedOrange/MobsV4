package me.orange.mobsv3.mobs;

import me.orange.mobsv3.MobManager;
import me.orange.mobsv3.MobsV3;
import me.orange.mobsv3.hex.HexUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class Cooldowns {
    public HashMap<UUID, Integer> SLOWDOWN = new HashMap<>();

    public static HashMap<UUID, Long> TURTLE = new HashMap<>();
    public static HashMap<UUID, Long> WARDEN = new HashMap<>();
    public static HashMap<UUID, Long> WARDEN_ALT = new HashMap<>();
    public static HashMap<UUID, Long> CREEPER = new HashMap<>();
    public static HashMap<UUID, Long> CREEPER_ALT = new HashMap<>();
    public static HashMap<UUID, Long> SKELETON = new HashMap<>();
    public static HashMap<UUID, Long> CHICKEN = new HashMap<>();
    public static HashMap<UUID, Long> CHICKEN_ALT = new HashMap<>();
    public static HashMap<UUID, Long> BLAZE = new HashMap<>();
    public static HashMap<UUID, Long> BLAZE_ALT = new HashMap<>();
    public static HashMap<UUID, Long> SPIDER = new HashMap<>();
    public static HashMap<UUID, Long> ENDERMAN = new HashMap<>();
    public static HashMap<UUID, Long> ZOMBIE = new HashMap<>();
    public static HashMap<UUID, Long> VILLAGER = new HashMap<>();
    public static HashMap<UUID, Long> VILLAGER_ALT = new HashMap<>();
    public static HashMap<UUID, Long> DRAGON = new HashMap<>();
    public static HashMap<UUID, Long> DRAGON_ALT = new HashMap<>();
    public static HashMap<UUID, Long> SHULKER = new HashMap<>();
    public static HashMap<UUID, Long> SHULKER_ALT = new HashMap<>();
    public static HashMap<UUID, Long> PIGLIN = new HashMap<>();
    public static HashMap<UUID, Long> WITHER = new HashMap<>();
    public static HashMap<UUID, Long> WITHER_ALT = new HashMap<>();
    public static HashMap<UUID, Long> WITCH = new HashMap<>();
    public static HashMap<UUID, Long> WITCH_ALT = new HashMap<>();
    public static HashMap<UUID, Long> ELDER_GUARDIAN = new HashMap<>();

    public static final HashMap<UUID, BukkitTask> cooldownDisplayTasks = new HashMap<>();

    public static boolean handleCooldown(Player p, String name) {
        String isAlt = "";
        String displayName = name;

        if (name.contains("-Alt")) {
            isAlt = " alternate";
            displayName = displayName.replace("-Alt", "");
        }

        displayName = displayName.replace("_", " ");

        startActionBarUpdateTask(p, name); // Ensure the action bar update task is running

        long cooldownTime = onCooldown(name, p);
        if (cooldownTime > 0) {
            // Ability is on cooldown, return true to indicate the ability shouldn't be used
            return true;
        } else {
            // Ability is not on cooldown, set the cooldown now
            setCooldown(name, p, System.currentTimeMillis());
            return false; // Ability is ready to be used
        }
    }

    // Utility method to get the cooldown status of an ability for the action bar
    private static String getAbilityStatus(String abilityName, Player player) {
        long cooldownTime = onCooldown(abilityName, player);
        if (cooldownTime > 0) {
            return "§eCooldown: " + cooldownTime + "s";
        } else {
            return "§aReady!";
        }
    }


    public static void startActionBarUpdateTask(Player player, String abilityName) {
        // Early exit if no ability is specified or it's set to "None"
        if (abilityName == null || abilityName.equals("None")) {
            return;
        }

        BukkitTask existingTask = cooldownDisplayTasks.get(player.getUniqueId());
        if (existingTask != null) {
            // A task is already running for this player, no need to start another
            return;
        }

        // Fetch the BaseMob instance; adjust for your implementation
        BaseMob mob = MobManager.findMobByName(abilityName.replace("-Alt", ""));
        if (mob == null) {
            Bukkit.getLogger().warning("Mob not found for abilityName: " + abilityName);
            return;
        }

        // Setup a task to update the action bar
        BukkitTask actionBarTask = new BukkitRunnable() {
            @Override
            public void run() {
                // Fetching the primary and alternate emojis
                String primaryEmoji = mob.getPrimaryEmoji();
                String altEmoji = mob.getAlt() != null && !mob.getAlt().isEmpty() ? mob.getAltEmoji() : "";

                // Adjusting the getAbilityStatus calls to use emojis
                String primaryAbilityStatus = primaryEmoji + " " + getAbilityStatus(mob.getName(), player);
                String altAbilityStatus = "";

                // If there's an alternate ability, fetch its cooldown status using the actual ability name
                if (!altEmoji.isEmpty()) {
                    String actualAltAbilityName = getActualAbilityName(mob.getName(), "Click");
                    altAbilityStatus = altEmoji + " " + getAbilityStatus(actualAltAbilityName, player);
                }

                // Constructing the action bar message
                String actionBarMessage = primaryAbilityStatus;
                if (!altAbilityStatus.isEmpty()) {
                    actionBarMessage += " | " + altAbilityStatus;
                }

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionBarMessage));
            }
        }.runTaskTimer(MobsV3.MOBS, 0L, 20L);

        cooldownDisplayTasks.put(player.getUniqueId(), actionBarTask);
    }

    private static String getActualAbilityName(String mobName, String placeholder) {
        if ("Witch".equals(mobName) && "Click".equals(placeholder)) {
            return "Witch-Alt"; // Replace with the actual ability name
        } else if ("Blaze".equals(mobName) && "Click".equals(placeholder)) {
            return "Blaze-Alt"; // Replace with the actual ability name
        } {

        }
        // Handle other mobs and abilities as needed
        return placeholder; // Fallback to the placeholder if no mapping is found
    }

    public static HashMap<UUID, Long> getType(String type) {
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
        else if (type.equalsIgnoreCase("Blaze-Alt"))
            COOLDOWN = BLAZE_ALT;
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

    public int getCooldown(String type, Player player) {
        return getSeconds(getType(type), player);
    }

    // Method to check if the player has the "Cooldown Counter" item
    private static boolean hasCooldownCounter(Player player) {
        if (player != null) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    if (item.getItemMeta().getDisplayName().equals("§6Cooldown Counter")) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return false;
        }
    }


    // Modified getSeconds method to account for the "Cooldown Counter" item
    public static int getSeconds(HashMap<UUID, Long> COOLDOWN, Player player) {
        int defaultSeconds = 60;
        int cooldownCounterSeconds = 30; // Reduced cooldown duration

        // Check if the player has the "Cooldown Counter" item
        if (hasCooldownCounter(player)) {
            return cooldownCounterSeconds;
        } else {
            return defaultSeconds;
        }
    }

    public static Long onCooldown(String type, Player key) {
        HashMap<UUID, Long> COOLDOWN = getType(type);
        int seconds = getSeconds(COOLDOWN, key);

        if (COOLDOWN.containsKey(key.getUniqueId())) {
            return ((COOLDOWN.get(key.getUniqueId()) / 1000) + seconds) - (System.currentTimeMillis() / 1000);
        }

        return 0L;
    }

    public static void setCooldown(String type, Player key, Long value) {
        HashMap<UUID, Long> COOLDOWN = getType(type);
        if (COOLDOWN.containsKey(key.getUniqueId())) {
            COOLDOWN.replace(key.getUniqueId(), value);
        } else COOLDOWN.put(key.getUniqueId(), value);

    }
}
