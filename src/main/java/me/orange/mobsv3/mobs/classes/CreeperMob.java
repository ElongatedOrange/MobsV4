package me.orange.mobsv3.mobs.classes;

import me.orange.mobsv3.MobsV3;
import me.orange.mobsv3.mobs.BaseMob;
import me.orange.mobsv3.mobs.Cooldowns;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CreeperMob extends BaseMob {
    private MobsV3 plugin;
    public static String name = "Creeper";

    public CreeperMob(MobsV3 plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPrefix() {
        return "§a";
    }

    @Override
    public String getPrimaryEmoji() {
        return getPrefix() + "💣";
    }

    @Override
    public boolean hasAltAbility() {
        return true;
    }

    @Override
    public String getAltEmoji() {
        return getPrefix() + "🤯";
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
        return 20;
    }

    @Override
    public ArrayList<String> getLore(Player token) {
        // Lore
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§9Token Abilities:");
        lore.add("  " + getPrefix() + "💣 Boom! §8(Right Click)");
        lore.add("  §fTurn into a Explosive Menace!");
        lore.add("  ");
        lore.add("  " + getPrefix() + "🤯 Shockwave §8(Left Click)");
        lore.add("  §fSpawn a §eShockwave to push away");
        lore.add("  §fenemies");
        lore.add("  §7(" + MobsV3.COOLDOWNS.getCooldown(name, token) + "s)");
        return lore;
    }

    @Override
    public ArrayList<String> getSpecial() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("  " + getPrefix() + "🧨 Blast Resistant");
        lore.add("  §fImmune to §eexplosions");

        return lore;
    }

    @Override
    public ArrayList<ArrayList<Object>> getEffects() {
        // Potion Effects
        return new ArrayList<ArrayList<Object>>() {{
            add(new ArrayList<Object>() {{
                add(PotionEffectType.SPEED);
                add(0);
            }});
        }};
    }

    private final HashMap<UUID, ItemStack[]> inventories = new HashMap<>();
    private final HashMap<UUID, ItemStack[]> armors = new HashMap<>();


    @Override
    public void perform(Player p) {
        if (Cooldowns.handleCooldown(p, name)) return;

        // Play creeper sound effect to all players
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            player.playSound(p.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1.0f, 1.0f);
        });

        // Schedule the custom explosion 2 seconds later
        new BukkitRunnable() {
            @Override
            public void run() {
                // Create an explosion effect without breaking blocks or directly damaging entities
                p.getWorld().createExplosion(p.getLocation(), 2.0F, true, true);

                // Manually deal damage to nearby entities
                for (Entity entity : p.getNearbyEntities(4.0, 4.0, 4.0)) {
                    if (entity instanceof LivingEntity) {
                        LivingEntity nearbyPlayer = (Player) entity;

                        /*
                        // Directly set the health, considering current health and max health
                        double newHealth = nearbyPlayer.getHealth() - 10.0; // 5 hearts worth of damage
                        if (newHealth < 0) newHealth = 0; // Ensure not setting health below 0

                        // Apply "true damage" by directly adjusting health
                        nearbyPlayer.setHealth(newHealth);

                         */
                        nearbyPlayer.damage(55, p);
                    }
                }
            }
        }.runTaskLater(MobsV3.MOBS, 40); // Delay of 2 seconds (40 ticks)
    }

    @Override
    public Boolean performAlt(Player player) {
        if (Cooldowns.handleCooldown(player, name + "-Alt")) return false;

        final int durationTicks = 20; // Duration of the shockwave effect in ticks
        final double maxRadius = 5.0; // Maximum radius of the shockwave
        final World world = player.getWorld();
        final Location center = player.getLocation();

        // Sound effect for the shockwave initiation
        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);

        // Particle effect for the shockwave
        new BukkitRunnable() {
            double radius = 0.0; // Starting radius

            @Override
            public void run() {
                if (radius > maxRadius) {
                    this.cancel(); // Stop the effect after reaching the maximum radius
                    return;
                }

                for (int i = 0; i < 360; i += 10) { // Increment to control the density of the circle
                    double radians = Math.toRadians(i);
                    double x = center.getX() + (radius * Math.cos(radians));
                    double z = center.getZ() + (radius * Math.sin(radians));
                    Location particleLocation = new Location(world, x, center.getY(), z);
                    world.spawnParticle(Particle.SWEEP_ATTACK, particleLocation, 1, 0, 0, 0, 0);
                }

                radius += 0.5; // Increment to control the speed of the shockwave expansion
            }
        }.runTaskTimer(MobsV3.MOBS, 0L, 1L); // Schedule the task to run every game tick

        // Push other players away
        List<Entity> nearbyEntities = player.getNearbyEntities(maxRadius, maxRadius, maxRadius);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player && entity != player) {
                Player otherPlayer = (Player) entity;
                Vector direction = otherPlayer.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                otherPlayer.damage(30, player);
                direction.multiply(2).setY(1.5);
                otherPlayer.setVelocity(direction);
            }
        }

        return true;
    }

    @Override
    public Boolean performAlt2(Player player) {
        return false;
    }

}