package me.orange.mobsv4.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class ChickenFallDamageListener implements Listener {
    private final Plugin plugin;

    public ChickenFallDamageListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        // Check if the damage is fall damage
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            // Check if the entity is a player
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();

                // Load the player's mob type from playerData.yml
                File playerDataFile = new File(plugin.getDataFolder(), "playerData.yml");
                FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);

                String mobType = playerData.getString(player.getUniqueId().toString() + ".mob");
                // Check if the player's assigned mob is "chicken"
                if ("chicken".equalsIgnoreCase(mobType)) {
                    // Check if the block below is a POINTED_DRIPSTONE
                    if (isLandingOnDripstone(player.getLocation())) {
                        // Cancel the event to negate the damage
                        event.setCancelled(true);
                    }
                    // Cancel the event to prevent fall damage
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean isLandingOnDripstone(Location location) {
        // Check the block directly below the player's location
        Block blockDirectlyBelow = location.getBlock().getRelative(0, -1, 0);
        if (blockDirectlyBelow.getType() == Material.POINTED_DRIPSTONE) {
            return true;
        }

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Block block = location.add(x, -1, z).getBlock();
                if (block.getType() == Material.POINTED_DRIPSTONE) {
                    return true;
                }
            }
        }

        return false;
    }
}
