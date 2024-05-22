package me.orange.mobsv4.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;

public class MobEffectApplier implements Runnable {

    private Plugin plugin;

    public MobEffectApplier(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            applyStrengthIfSkeletonWithBow(player);
        }
    }

    private void applyStrengthIfSkeletonWithBow(Player player) {
        File playerDataFile = new File(plugin.getDataFolder(), "playerData.yml");
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);

        String mobType = playerData.getString(player.getUniqueId().toString() + ".mob");
        if ("skeleton".equalsIgnoreCase(mobType) && player.getInventory().getItemInOffHand().getType() == Material.BOW) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 1, true, true, true));
        }
    }

}
