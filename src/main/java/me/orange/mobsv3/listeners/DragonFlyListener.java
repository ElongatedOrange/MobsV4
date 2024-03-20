package me.orange.mobsv3.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

import java.util.HashMap;

public class DragonFlyListener implements Listener {

    public static HashMap<Player, Long> FLYING = new HashMap<>();

    public static void setFlying(Player p) {
        FLYING.put(p, 1L);
    }

    @EventHandler
    public void onEntityToggleGlide(EntityToggleGlideEvent event) {
        if (event.getEntity() instanceof Player p) {

            if (p.getInventory().getChestplate() != null && p.getInventory().getChestplate().getType() == Material.ELYTRA)
                return;

            if (FLYING.containsKey(p)) {
                if (((Entity) p).isOnGround() || p.getLocation().getBlock().getType() == Material.WATER) {
                    FLYING.remove(p);
                    return;
                }

                p.setGliding(true);
                event.setCancelled(true);
            }
        }
    }

}
