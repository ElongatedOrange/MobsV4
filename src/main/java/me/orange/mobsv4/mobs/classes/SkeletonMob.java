package me.orange.mobsv4.mobs.classes;

import me.orange.mobsv4.MobsV4;
import me.orange.mobsv4.hex.HexUtils;
import me.orange.mobsv4.mobs.BaseMob;
import me.orange.mobsv4.mobs.Cooldowns;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SkeletonMob extends BaseMob {
    public static String name = "Skeleton";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPrefix() {
        return HexUtils.format("#d3d3d3");
    }

    @Override
    public String getPrimaryEmoji() {
        return ChatColor.WHITE + "🏹";
    }

    @Override
    public boolean hasAltAbility() {
        return true;
    }

    @Override
    public String getAltEmoji() {
        return ChatColor.WHITE + "🦴";
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
        lore.add("  " + getPrefix() + "🏹 Arrow Barrage §8(Right Click)");
        lore.add("  §fShoots §e3 arrows §fin the");
        lore.add("  §fdirection you are facing");
        lore.add("  ");
        lore.add("  " + getPrefix() + "🦴 Bone Edge §8(Left Click)");
        lore.add("  §fSpawns a §ebone spike §from the");
        lore.add("  §fground where your enemies are");
        lore.add("  §7(" + MobsV4.COOLDOWNS.getCooldown(name, token) + "s)");

        return lore;
    }

    @Override
    public ArrayList<String> getSpecial() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("  " + getPrefix() + "🦴 Skeleton's Strength");
        lore.add("  §fBow in off-hand gives §eStrength 2");
        return lore;
    }

    @Override
    public ArrayList<ArrayList<Object>> getEffects() {
        // Potion Effects
        return new ArrayList<>() {{
            add(new ArrayList<Object>() {{
                add(PotionEffectType.SPEED);
                add(0);
            }});
        }};
    }

    @Override
    public void perform(Player player) {
        if (Cooldowns.handleCooldown(player, name)) return;

        long initialDelay = 15;

        // Shoot 3 arrows with increasing damage
        for (int i = 0; i < 3; i++) {
            long delay = initialDelay * i;
            int finalI = i;
            new BukkitRunnable() {
                @Override
                public void run() {
                    Arrow arrow = player.launchProjectile(Arrow.class);
                    arrow.setVelocity(player.getLocation().getDirection().multiply(5)); // Adjust speed
                    // Set damage based on arrow number (i+1) since i starts at 0
                    arrow.setDamage(3 + finalI); // First arrow does 3 damage, second does 4, third does 5
                    // Play shooting sound
                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0F, 1.0F);
                }
            }.runTaskLater(MobsV4.MOBS, delay);
        }
    }


    @Override
    public Boolean performAlt(Player performer) {
        if (Cooldowns.handleCooldown(performer, name + "-Alt")) return false;

        final double radius = 7.0; // Radius around the performer
        List<Player> affectedPlayers = performer.getNearbyEntities(radius, radius, radius).stream()
                .filter(e -> e instanceof Player && e != performer)
                .map(e -> (Player) e)
                .collect(Collectors.toList());

        spawnParticles(performer);
        for (Player target : affectedPlayers) {
            target.setVelocity(new Vector(0, 2, 0)); // Launches the player up
            // Delay spawning the bone structure to ensure players are not stuck inside
            Bukkit.getScheduler().runTaskLater(MobsV4.MOBS, () -> {
                spawnBoneStructure(target.getLocation());
                target.damage(35, performer);
            }, 5L);
        }

        return true;
    }

    @Override
    public Boolean performAlt2(Player player) {
        return false;
    }

    private void spawnBoneStructure(Location location) {
        World world = location.getWorld();
        int groundY = world.getHighestBlockYAt(location) + 1; // Get the highest ground y-coordinate

        // Adjust the y-coordinate so that the structure spawns on the ground
        location.setY(groundY);

        // Offsets for the bone structure
        int[][] offsets = {
                {0, 0, 0}, {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}, // Base layer
                {0, 1, 0}, {0, 2, 0}  // Upper layers
        };

        // Spawn the bone structure
        for (int[] offset : offsets) {
            Location loc = location.clone().add(offset[0], offset[1], offset[2]);
            loc.getBlock().setType(Material.BONE_BLOCK);
        }

        // Logic to remove the bone blocks after some delay
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int[] offset : offsets) {
                    Location loc = location.clone().add(offset[0], offset[1], offset[2]);
                    if (loc.getBlock().getType() == Material.BONE_BLOCK) {
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }.runTaskLater(MobsV4.MOBS, 20L * 5);
    }

    private void spawnParticles(Player player) {
        World world = player.getWorld();
        final double maxRadius = 7.0; // Maximum radius
        final Particle.DustOptions whiteDustOptions = new Particle.DustOptions(Color.fromRGB(255, 255, 255), 1);

        new BukkitRunnable() {
            double radius = 0.5; // Start from a small circle

            @Override
            public void run() {
                if (radius > maxRadius) {
                    this.cancel(); // Stop the loop when the radius exceeds the maximum
                    return;
                }

                // Create a circle with the current radius
                for (int i = 0; i < 360; i += 10) {
                    double angle = Math.toRadians(i);
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);
                    world.spawnParticle(Particle.REDSTONE, player.getLocation().add(x, 0, z), 1, 0, 0, 0, 0, whiteDustOptions);
                }

                radius += 0.5; // Increase the radius more quickly
            }
        }.runTaskTimer(MobsV4.MOBS, 0L, 1L); // Schedule to run every tick
    }

}
