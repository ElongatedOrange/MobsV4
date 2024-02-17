package me.orange.mobsv3.mobs.classes;

import me.orange.mobsv3.MobsV3;
import me.orange.mobsv3.hex.HexUtils;
import me.orange.mobsv3.mobs.BaseMob;
import me.orange.mobsv3.mobs.Cooldowns;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class BlazeMob extends BaseMob {
    public static String name = "Blaze";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPrefix() {
        return HexUtils.format("#fc6203");
    }

    @Override
    public String getAlt() {
        return null;
    }

    @Override
    public int getHealth() {
        return 22;
    }

    public ArrayList<String> getLore(ItemStack token) {
        // Lore
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§9Token Abilities:");
        lore.add("  " + getPrefix() + "🔥 Flamethrower §8(Right Click)");
        lore.add("  §fShoot a §eFlamethrower §fin the");
        lore.add("  §fdirection you are facing.");
        lore.add("  §7(" + MobsV3.COOLDOWNS.getCooldown(name, token) + "s)");

        return lore;
    }

    @Override
    public ArrayList<String> getSpecial() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("  " + getPrefix() + "⚠ Forged in Flames");
        lore.add("  §fIf in §elava §fget §eStrength 2");
        lore.add("  §ffor 10 seconds.");

        return lore;
    }

    @Override
    public ArrayList<ArrayList<Object>> getEffects() {
        // Potion Effects
        return new ArrayList<ArrayList<Object>>() {{
            add(new ArrayList<Object>() {{
                add(PotionEffectType.FIRE_RESISTANCE);
                add(0);
            }});

            add(new ArrayList<Object>() {{
                add(PotionEffectType.SPEED);
                add(0);
            }});

            add(new ArrayList<Object>() {{
                add(PotionEffectType.INCREASE_DAMAGE);
                add(0);
            }});
        }};
    }

    @Override
    public void perform(Player player) {
        if (Cooldowns.handleCooldown(player, name)) return;

        new org.bukkit.scheduler.BukkitRunnable() {
            long ticks = 0;

            @Override
            public void run() {
                if (ticks > 12L) {
                    this.cancel();
                    return;
                }

                // Update the start location and direction with each tick
                Vector direction = player.getLocation().getDirection();
                Location startLocation = player.getLocation().add(0, 1, 0); // Start from player's head height

                for (int i = 0; i < 5; i++) {
                    Location point = startLocation.clone().add(direction.clone().multiply(i));
                    player.getWorld().spawnParticle(Particle.FLAME, point, 5, 0.3, 0.3, 0.3, 0.01);

                    // Apply damage to entities in range
                    for (Entity entity : point.getWorld().getNearbyEntities(point, 0.5, 0.5, 0.5)) {
                        if (entity instanceof LivingEntity && entity != player) {
                            ((LivingEntity) entity).damage(16.0, player);
                            entity.setFireTicks(100);
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(MobsV3.MOBS, 0L, 10L);
    }


    @Override
    public Boolean performAlt(Player player) {
        return null;
    }
}
