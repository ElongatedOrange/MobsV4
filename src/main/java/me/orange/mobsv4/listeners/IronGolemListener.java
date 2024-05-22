package me.orange.mobsv4.listeners;

import me.orange.mobsv4.MobsV4;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class IronGolemListener implements Listener {
    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player target = (Player) event.getEntity();

            if (damager.hasMetadata("launchUp")) {
                // Remove the metadata immediately after use
                damager.removeMetadata("launchUp", MobsV4.MOBS);

                // Calculate triple damage
                event.setDamage(event.getDamage() * 2);

                // Schedule the launch to occur slightly later
                Bukkit.getScheduler().runTaskLater(MobsV4.MOBS, () -> {
                    // Launch the target into the air
                    Vector up = target.getVelocity().add(new Vector(0, 1.5, 0)); // Adjust the Y velocity component to launch the player up
                    target.setVelocity(up);

                    // Optionally play a sound or particle effect
                    target.getWorld().playSound(target.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f);
                }, 1L); // Delay by 1 tick
            }
        }
    }


}
