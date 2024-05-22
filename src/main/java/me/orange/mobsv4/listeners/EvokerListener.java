package me.orange.mobsv4.listeners;

import me.orange.mobsv4.MobsV4;
import org.bukkit.EntityEffect;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vex;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.Random;
import java.util.UUID;

public class EvokerListener implements Listener {

    private MobsV4 plugin;
    private final Random random = new Random();

    public EvokerListener(MobsV4 plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVexTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof Vex && event.getEntity().hasMetadata("PlayerOwnedVex")) {
            Vex vex = (Vex) event.getEntity();
            String ownerUUID = vex.getMetadata("PlayerOwnedVex").get(0).asString();

            // Check if the target is the owner
            if (event.getTarget() instanceof Player) {
                Player targetPlayer = (Player) event.getTarget();
                if (targetPlayer.getUniqueId().toString().equals(ownerUUID)) {
                    event.setCancelled(true);  // Prevent vex from targeting its owner
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        File playerDataFile = new File(plugin.getDataFolder(), "playerData.yml");
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);

        String mobType = playerData.getString(player.getUniqueId().toString() + ".mob");
        // Check if the player's assigned mob is evoker
        if ("evoker".equalsIgnoreCase(mobType)) {
            double healthAfterDamage = player.getHealth() - event.getFinalDamage();
            if (healthAfterDamage <= 0) {
                // There's a 25% chance to trigger the totem effect
                if (random.nextInt(4) == 0) {
                    event.setCancelled(true); // Cancel the event so the player does not actually die
                    player.setHealth(1.0); // Set their health to 1
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 900, 1)); // Regeneration II for 45 seconds
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 1)); // Absorption I for 5 seconds
                    player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1.0F, 1.0F);
                    player.getWorld().spawnParticle(Particle.TOTEM, player.getLocation(), 30); // Spawn totem particles
                    player.playEffect(EntityEffect.TOTEM_RESURRECT); // Play the totem animation
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() == EntityType.EVOKER_FANGS) {
            Entity damager = event.getDamager();
            if (damager.hasMetadata("owner")) {
                UUID ownerUUID = UUID.fromString(damager.getMetadata("owner").get(0).asString());
                if (event.getEntity() instanceof Player) {
                    Player target = (Player) event.getEntity();
                    if (target.getUniqueId().equals(ownerUUID)) {
                        event.setCancelled(true); // Prevent damage to the owner
                    }
                }
            }
        }
    }

}
