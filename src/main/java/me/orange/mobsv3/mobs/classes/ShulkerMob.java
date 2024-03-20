package me.orange.mobsv3.mobs.classes;

import me.orange.mobsv3.MobsV3;
import me.orange.mobsv3.hex.HexUtils;
import me.orange.mobsv3.mobs.BaseMob;
import me.orange.mobsv3.mobs.Cooldowns;
import org.apache.commons.lang3.ObjectUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShulkerMob extends BaseMob {
    public static String name = "Shulker";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPrefix() {
        return HexUtils.format("#8e40c9");
    }

    @Override
    public String getPrimaryEmoji() {
        return getPrefix() + "⬆";
    }

    @Override
    public boolean hasAltAbility() {
        return true;
    }

    @Override
    public String getAltEmoji() {
        return getPrefix() + "☁";
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
        return 24;
    }

    @Override
    public ArrayList<String> getLore(Player token) {
        // Lore
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§9Token Abilities:");
        lore.add("  " + getPrefix() + "⬆ Away We Go §8(Right Click)");
        lore.add("  §fGive players in a §e3 §fblock radius");
        lore.add("  §eLevitation, Poison §fand §eSlowness");
        lore.add("  ");
        lore.add("  " + getPrefix() + "☁ Poof! §8(Left Click)");
        lore.add("  §fTeleport randomly away");
        lore.add("  §7(" + MobsV3.COOLDOWNS.getCooldown(name, token) + "s)");

        return lore;
    }

    @Override
    public ArrayList<String> getSpecial() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("  " + getPrefix() + "⛑ Hardened Shell §8(Crouch)");
        lore.add("  §fCrouching gives §eResistance 2");

        return lore;
    }

    @Override
    public ArrayList<ArrayList<Object>> getEffects() {
        // Potion Effects
        return new ArrayList<ArrayList<Object>>() {{
            add(new ArrayList<Object>() {{
                add(PotionEffectType.INCREASE_DAMAGE);
                add(0);
            }});
        }};
    }

    @Override
    public void perform(Player p) {
        if (Cooldowns.handleCooldown(p, name)) return;

        World world = p.getWorld();
        final double maxRadius = 3.0; // Maximum radius
        final Particle.DustOptions purpleDustOptions = new Particle.DustOptions(Color.fromRGB(128, 0, 128), 1);
        final Particle.DustOptions whiteDustOptions = new Particle.DustOptions(Color.fromRGB(255, 255, 255), 1);

        new BukkitRunnable() {
            double radius = 0.2; // Start from a small circle
            boolean isPurple = false; // Start with white color

            @Override
            public void run() {
                if (radius > maxRadius) {
                    this.cancel(); // Stop the loop when the radius exceeds the maximum
                    return;
                }

                // Choose color based on the current state
                Particle.DustOptions dustOptions = isPurple ? purpleDustOptions : whiteDustOptions;

                // Create a circle with the current radius
                for (int i = 0; i < 360; i += 10) {
                    double angle = Math.toRadians(i);
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);
                    world.spawnParticle(Particle.REDSTONE, p.getLocation().add(x, 0, z), 1, 0, 0, 0, 0, dustOptions);
                }

                radius += 0.2; // Increase the radius gradually
                isPurple = !isPurple; // Alternate the color for the next ring
            }
        }.runTaskTimer(MobsV3.MOBS, 0L, 1L); // Schedule to run every tick

        // Apply effects to players within a 3-block radius
        List<Entity> nearbyEntities = p.getNearbyEntities(maxRadius, maxRadius, maxRadius);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player) {
                Player target = (Player) entity;

                // Levitation 5 for 10 seconds
                target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20, 69));

                // Poison 2 for 15 seconds
                target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20, 254));

                // Slowness 1 for 15 seconds
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1));

                // Prevent using Ender Pearls for 10 seconds
                target.setCooldown(Material.ENDER_PEARL, 20 * 10); // 10 seconds cooldown

            }
        }
    }

    @Override
    public Boolean performAlt(Player p) {
        if (Cooldowns.handleCooldown(p, name + "-Alt")) return false;

        // Create smoke bomb effect at the player's current location
        createSmokeBombEffect(p.getLocation());

        // Teleport the player to a random nearby location
        teleportRandomly(p);
        // Play sound at the new location as well to indicate arrival
        p.playSound(p.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1.0f, 1.0f);

        return true;
    }

    @Override
    public Boolean performAlt2(Player player) {
        return false;
    }

    private void createSmokeBombEffect(Location location) {
        // Set the particle color to purple (using RGB values)
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(128, 0, 128), 3.0f); // Increase size to 3.0 for even bigger particles

        // Spawn the purple, larger smoke particles with a higher count and bigger spread
        location.getWorld().spawnParticle(Particle.REDSTONE, location, 200, 3.0, 3.0, 3.0, 0.1, dustOptions);

        // Play the shulker teleport sound with a lower pitch for a deeper sound effect
        location.getWorld().playSound(location, Sound.ENTITY_SHULKER_TELEPORT, 1.0f, 0.8f);
    }

    private void teleportRandomly(Player player) {
        Location originalLocation = player.getLocation();
        World world = originalLocation.getWorld();
        Random random = new Random();
        boolean safeLocationFound = false;

        int attempts = 0;
        int maxAttempts = 150; // Maximum number of attempts to find a safe location

        while (!safeLocationFound && attempts < maxAttempts) {
            // Generate random offsets within 15 to 25 blocks
            int xOffset = 15 + random.nextInt(11) * (random.nextBoolean() ? 1 : -1);
            int zOffset = 15 + random.nextInt(11) * (random.nextBoolean() ? 1 : -1);

            Location targetLocation = originalLocation.clone().add(xOffset, 0, zOffset);
            // Find the highest block at the target location that is not bedrock
            int highestY = world.getHighestBlockYAt(targetLocation);
            Location highestLocation = new Location(world, targetLocation.getX(), highestY, targetLocation.getZ());


            // Ensure the location is not on bedrock. If it is, decrease Y until a non-bedrock block is found.
            while (highestLocation.getBlock().getType() == Material.BEDROCK && highestLocation.getY() > 1) {
                highestLocation.setY(highestLocation.getY() - 1);
            }


            // Check for safe landing spot (solid block with two air blocks above)
            if (highestLocation.getBlock().getType().isSolid() &&
                    highestLocation.clone().add(0, 1, 0).getBlock().getType() == Material.AIR &&
                    highestLocation.clone().add(0, 2, 0).getBlock().getType() == Material.AIR) {
                // Set the teleport location to one block above the highest solid block
                highestLocation.add(0, 1, 0);

                // Teleport the player
                player.teleport(highestLocation);

                safeLocationFound = true;
            } else {
                // Increment the attempt counter
                attempts++;
            }
        }
    }


}