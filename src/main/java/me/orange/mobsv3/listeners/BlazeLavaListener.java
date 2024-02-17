package me.orange.mobsv3.listeners;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;

public class BlazeLavaListener implements Listener {

    private Plugin plugin;

    public BlazeLavaListener(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Check if the player is in lava
        if (player.getLocation().getBlock().getType() == Material.LAVA) {
            // Load the player's mob type from playerData.yml
            File playerDataFile = new File(plugin.getDataFolder(), "playerData.yml");
            FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);

            String mobType = playerData.getString(player.getUniqueId().toString() + ".mob");
            // Check if the player's assigned mob is "blaze"
            if ("blaze".equalsIgnoreCase(mobType)) {
                // Apply Strength 2 effect for 10 seconds
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 1, false, false, true));
            }
        }
    }
}

