package me.orange.mobsv4.mobs;

import me.orange.mobsv4.MobManager;
import me.orange.mobsv4.MobsV4;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class Cooldowns {
    public HashMap<UUID, Integer> SLOWDOWN = new HashMap<>();

    public static HashMap<UUID, Long> TURTLE = new HashMap<>();
    public static HashMap<UUID, Long> TURTLE_ALT = new HashMap<>();
    public static HashMap<UUID, Long> WARDEN = new HashMap<>();
    public static HashMap<UUID, Long> WARDEN_ALT = new HashMap<>();
    public static HashMap<UUID, Long> CREEPER = new HashMap<>();
    public static HashMap<UUID, Long> CREEPER_ALT = new HashMap<>();
    public static HashMap<UUID, Long> SKELETON = new HashMap<>();
    public static HashMap<UUID, Long> SKELETON_ALT = new HashMap<>();
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
    public static HashMap<UUID, Long> DRAGON_ALT2 = new HashMap<>();
    public static HashMap<UUID, Long> IRONGOLEM = new HashMap<>();
    public static HashMap<UUID, Long> IRONGOLEM_ALT = new HashMap<>();
    public static HashMap<UUID, Long> PIGLIN = new HashMap<>();
    public static HashMap<UUID, Long> WITHER = new HashMap<>();
    public static HashMap<UUID, Long> WITHER_ALT = new HashMap<>();
    public static HashMap<UUID, Long> EVOKER = new HashMap<>();
    public static HashMap<UUID, Long> EVOKER_ALT = new HashMap<>();
    public static HashMap<UUID, Long> ELDER_GUARDIAN = new HashMap<>();

    public static final HashMap<UUID, BukkitTask> cooldownDisplayTasks = new HashMap<>();

    public static boolean handleCooldown(Player p, String name) {
        String displayName = name.replaceAll("(-Alt|-Alt2)$", "").replace("_", " ");

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

        // Cancel the existing task if there is one
        BukkitTask existingTask = cooldownDisplayTasks.get(player.getUniqueId());
        if (existingTask != null) {
            existingTask.cancel(); // Cancel the existing task
            cooldownDisplayTasks.remove(player.getUniqueId()); // Remove from the map
        }

        BaseMob mob = MobManager.findMobByName(abilityName.replaceAll("(-Alt|-Alt2)$", ""));
        if (mob == null) {
            Bukkit.getLogger().warning("Mob not found for abilityName: " + abilityName);
            return;
        }

        // Schedule a new task to update the action bar
        BukkitTask actionBarTask = new BukkitRunnable() {
            @Override
            public void run() {
                String primaryEmoji = mob.getPrimaryEmoji();
                String altEmoji = mob.hasAltAbility() ? mob.getAltEmoji() : "";
                String alt2Emoji = mob.hasAlt2Ability() ? mob.getAlt2Emoji() : ""; // Adjust for your implementation

                String primaryAbilityStatus = primaryEmoji + " " + getAbilityStatus(mob.getName(), player);
                String altAbilityStatus = !altEmoji.isEmpty() ? altEmoji + " " + getAbilityStatus(mob.getName() + "-Alt", player) : "";
                String alt2AbilityStatus = !alt2Emoji.isEmpty() ? alt2Emoji + " " + getAbilityStatus(mob.getName() + "-Alt2", player) : "";

                String actionBarMessage = primaryAbilityStatus;
                if (!altAbilityStatus.isEmpty()) {
                    actionBarMessage += " | " + altAbilityStatus;
                }
                if (!alt2AbilityStatus.isEmpty()) {
                    actionBarMessage += " | " + alt2AbilityStatus;
                }

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionBarMessage));
            }
        }.runTaskTimer(MobsV4.MOBS, 0L, 20L); // Adjust MobsV3.MOBS to your plugin instance

        cooldownDisplayTasks.put(player.getUniqueId(), actionBarTask);
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
        else if (type.equalsIgnoreCase("IronGolem"))
            COOLDOWN = IRONGOLEM;
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
        else if (type.equalsIgnoreCase("IronGolem-Alt"))
            COOLDOWN = IRONGOLEM_ALT;
        else if (type.equalsIgnoreCase("Wither-Alt"))
            COOLDOWN = WITHER_ALT;
        else if (type.equalsIgnoreCase("Evoker"))
            COOLDOWN = EVOKER;
        else if (type.equalsIgnoreCase("Evoker-Alt"))
            COOLDOWN = EVOKER_ALT;
        else if (type.equalsIgnoreCase("Elder_Guardian"))
            COOLDOWN = ELDER_GUARDIAN;
        else if (type.equalsIgnoreCase("Chicken-Alt"))
            COOLDOWN = CHICKEN_ALT;
        else if (type.equalsIgnoreCase("Villager-Alt")) {
            COOLDOWN = VILLAGER_ALT;
        } else if (type.equalsIgnoreCase("Skeleton-Alt")) {
            COOLDOWN = SKELETON_ALT;
        } else if (type.equalsIgnoreCase("Turtle-Alt")) {
            COOLDOWN = TURTLE_ALT;
        } else if (type.equalsIgnoreCase("Creeper-Alt")) {
            COOLDOWN = CREEPER_ALT;
        } else if (type.equalsIgnoreCase("Dragon-Alt2")) {
            COOLDOWN = DRAGON_ALT2;
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
