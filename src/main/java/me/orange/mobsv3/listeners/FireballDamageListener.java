package me.orange.mobsv3.listeners;

import me.orange.mobsv3.MobsV3;
import org.bukkit.Effect;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class FireballDamageListener implements Listener {
    private MobsV3 plugin;

    public FireballDamageListener(MobsV3 plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Fireball && event.getEntity() instanceof LivingEntity) {
            Fireball fireball = (Fireball) event.getDamager();
            if (fireball.hasMetadata("TrueDamageFireball")) {
                for (MetadataValue value : fireball.getMetadata("TrueDamageFireball")) {
                    if (value.getOwningPlugin() == plugin && value.asBoolean()) {
                        // Ensure the shooter is a player
                        if (fireball.getShooter() instanceof Player) {
                            Player player = (Player) fireball.getShooter();
                            LivingEntity entity = (LivingEntity) event.getEntity();

                            // Cancel the event to prevent default damage processing
                            event.setCancelled(true);

                            // Directly set the health, considering current health and max health
                            double newHealth = entity.getHealth() - 10; // 5 hearts worth of damage
                            if (newHealth < 0) newHealth = 0; // Ensure not setting health below 0

                            // Apply "true damage" by directly adjusting health
                            entity.setHealth(newHealth);
                        }
                        break;
                    }
                }
            }
        }
    }


}
