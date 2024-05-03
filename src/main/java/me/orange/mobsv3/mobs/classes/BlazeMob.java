package me.orange.mobsv3.mobs.classes;

import me.orange.mobsv3.MobsV3;
import me.orange.mobsv3.hex.HexUtils;
import me.orange.mobsv3.mobs.BaseMob;
import me.orange.mobsv3.mobs.Cooldowns;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
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
    public String getPrimaryEmoji() {
        return "§c🔥";
    }

    @Override
    public boolean hasAltAbility() {
        return true;
    }

    @Override
    public String getAltEmoji() {
        return "§c🎆";
    }

    @Override
    public String getAlt() {
        return "Click";
    }

    @Override
    public boolean hasAlt2Ability() {
        return false;
    }

    @Override
    public String getAlt2Emoji() {
        return null;
    }

    @Override
    public String getAlt2() {
        return null;
    }

    @Override
    public int getHealth() {
        return 22;
    }

    public ArrayList<String> getLore(Player token) {
        // Lore
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§9Token Abilities:");
        lore.add("  " + getPrefix() + "🔥 Flamethrower §8(Right Click)");
        lore.add("  §fShoot a §eFlamethrower §fin the");
        lore.add("  §fdirection you are facing.");
        lore.add("");
        lore.add("  " + getPrefix() + "🎆 Firework §8(Left Click)");
        lore.add("  §fLaunch a §efirework §fin the");
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
        if (Cooldowns.handleCooldown(player, name + "-Alt")) return false;

        final World world = player.getWorld();
        Location startLocation = player.getEyeLocation();
        Vector direction = startLocation.getDirection().normalize(); // Ensure direction is normalized

        new BukkitRunnable() {
            double distance = 0.0; // Track the distance traveled
            final double maxDistance = 30.0; // Maximum distance
            final double step = 2.5; // Distance to move each tick

            @Override
            public void run() {
                distance += step;
                if (distance > maxDistance) {
                    this.cancel(); // Stop if max distance reached
                    return;
                }

                // Calculate new location for this tick
                Location currentLocation = startLocation.clone().add(direction.clone().multiply(distance));
                world.spawnParticle(Particle.FLAME, currentLocation, 1, 0, 0, 0, 0);

                // Check for entities in a small radius around the current location
                for (Entity entity : world.getNearbyEntities(currentLocation, 1.5, 1.5, 1.5)) {
                    if (entity instanceof LivingEntity && !entity.equals(player)) {
                        // Damage the entity
                        ((LivingEntity) entity).damage(30, player);
                        // Create explosion effect
                        createExplosion(currentLocation, world);
                        this.cancel(); // End the beam
                        return;
                    }
                }

                // Check if the beam has hit a block
                if (currentLocation.getBlock().getType() != Material.AIR) {
                    createExplosion(currentLocation, world);
                    this.cancel(); // End the beam
                }
            }
        }.runTaskTimer(MobsV3.MOBS, 0L, 1L); // Schedule to run every tick

        return true;
    }

    @Override
    public Boolean performAlt2(Player player) {
        return false;
    }

    private void createExplosion(Location loc, World world) {
        int numberOfParticles = 150;
        double spread = 1.5;

        for (int i = 0; i < numberOfParticles; i++) {
            double angle = Math.random() * Math.PI * 2;
            double zAngle = Math.random() * Math.PI * 2;
            double x = Math.cos(angle) * Math.sin(zAngle);
            double y = Math.cos(zAngle);
            double z = Math.sin(angle) * Math.sin(zAngle);

            Vector direction = new Vector(x, y, z).normalize().multiply(spread);
            world.spawnParticle(Particle.FLAME, loc, 0, direction.getX(), direction.getY(), direction.getZ(), 0.2);
        }
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1F, 1F); // Explosion sound
    }


}
