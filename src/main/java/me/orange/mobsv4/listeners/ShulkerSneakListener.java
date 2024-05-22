package me.orange.mobsv4.listeners;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.scheduler.BukkitTask;

public class ShulkerSneakListener implements Listener {
    private final Plugin plugin;
    private final Map<UUID, BukkitTask> sneakingTasks = new HashMap<>();

    public ShulkerSneakListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (event.isSneaking()) {
            // Start sneaking: Apply Resistance 1 effect and start a repeating task to keep it applied
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> applyResistance(player), 0L, 20L);
            sneakingTasks.put(playerId, task);
        } else {
            // Stopped sneaking: Cancel the task and remove it from the map
            if (sneakingTasks.containsKey(playerId)) {
                sneakingTasks.get(playerId).cancel();
                sneakingTasks.remove(playerId);
            }
        }
    }

    private void applyResistance(Player player) {
        // Load the player's mob type from playerData.yml
        File playerDataFile = new File(plugin.getDataFolder(), "playerData.yml");
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);
        String mobType = playerData.getString(player.getUniqueId().toString() + ".mob");

        if ("shulker".equalsIgnoreCase(mobType) && player.isSneaking()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 1, false, false, true));
        } else {
            // Player is no longer a shulker or not sneaking, cancel task
            UUID playerId = player.getUniqueId();
            if (sneakingTasks.containsKey(playerId)) {
                sneakingTasks.get(playerId).cancel();
                sneakingTasks.remove(playerId);
            }
        }
    }
}

