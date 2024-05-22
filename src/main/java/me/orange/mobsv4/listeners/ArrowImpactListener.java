package me.orange.mobsv4.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class ArrowImpactListener implements Listener {

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow)) return;
        Arrow arrow = (Arrow) event.getEntity();

        if (arrow.hasMetadata("TrueDamageArrow")) {
            Entity hitEntity = event.getHitEntity();
            if (hitEntity instanceof LivingEntity) {
                LivingEntity hit = (LivingEntity) hitEntity;

                // Apply knockback effect
                Vector knock = arrow.getVelocity().normalize().multiply(-1);
                hit.setVelocity(knock.multiply(0.5)); // Adjust knockback strength as needed

                // Apply 2 hearts of true damage
                double newHealth = hit.getHealth() - 3;
                if (newHealth < 0) newHealth = 0;
                hit.setHealth(newHealth);
            }
        }
    }
}
