package me.orange.mobsv3.mobs.classes;

import me.orange.mobsv3.MobsV3;
import me.orange.mobsv3.hex.HexUtils;
import me.orange.mobsv3.listeners.DragonFlyListener;
import me.orange.mobsv3.mobs.BaseMob;
import me.orange.mobsv3.mobs.Cooldowns;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DragonMob extends BaseMob {
    private Random random = new Random();

    public static String name = "Dragon";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPrefix() {
        return HexUtils.format("#552cdb");
    }

    @Override
    public String getPrimaryEmoji() {
        return ChatColor.DARK_PURPLE + "🌌";
    }

    @Override
    public boolean hasAltAbility() {
        return true;
    }

    @Override
    public String getAltEmoji() {
        return ChatColor.DARK_PURPLE + "🐲";
    }

    @Override
    public String getAlt() {
        return "Click";
    }

    @Override
    public boolean hasAlt2Ability() {
        return true;
    }

    @Override
    public String getAlt2Emoji() {
        return ChatColor.DARK_PURPLE + "🐉";
    }

    @Override
    public String getAlt2() {
        return "Crouch";
    }

    @Override
    public int getHealth() {
        return 40;
    }

    @Override
    public ArrayList<String> getLore(Player player) {
        // Lore
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§9Token Abilities:");
        lore.add("  " + getPrefix() + "🌌 Vortex §8(Right Click)");
        lore.add("  §fCause nearby blocks and players");
        lore.add("  §fto float up and be thrown away");
        lore.add("  §7(" + MobsV3.COOLDOWNS.getCooldown(name, player) + "s)");
        lore.add("");
        lore.add("  " + getPrefix() + "🐲 Dragon's Wings §8(Left Click)");
        lore.add("  §fGet flung into the air, and begin");
        lore.add("  §eGliding §funtil you hit the ground.");
        lore.add("  §7(" + MobsV3.COOLDOWNS.getCooldown(name + "-Alt", player) + "s)");
        lore.add("");
        lore.add("  " + getPrefix() + "🐉 Dragon's Acid §8(Crouch)");
        lore.add("  §fSpawn Dragon's Acid around you");
        lore.add("  §7(" + MobsV3.COOLDOWNS.getCooldown(name + "-Alt2", player) + "s)");

        return lore;
    }

    @Override
    public ArrayList<String> getSpecial() {
        return null;
    }

    @Override
    public ArrayList<ArrayList<Object>> getEffects() {
        // Potion Effects
        return new ArrayList<ArrayList<Object>>() {{
            add(new ArrayList<Object>() {{
                add(PotionEffectType.INCREASE_DAMAGE);
                add(1);
            }});

            add(new ArrayList<Object>() {{
                add(PotionEffectType.DAMAGE_RESISTANCE);
                add(0);
            }});

            add(new ArrayList<Object>() {{
                add(PotionEffectType.FAST_DIGGING);
                add(0);
            }});
        }};
    }

    @Override
    public void perform(Player p) {
        if (Cooldowns.handleCooldown(p, name)) return;

        final World world = p.getWorld();
        final Location center = p.getLocation();
        final int radius = 7;
        final Random random = new Random();
        final List<Entity> affectedEntities = new ArrayList<>();
        final List<FallingBlock> affectedBlocks = new ArrayList<>();
        // Create the dark purple dust options
        Particle.DustOptions darkPurpleDustOptions = new Particle.DustOptions(Color.fromRGB(68, 0, 68), 1.0f);

        world.playSound(center, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);

        // Make nearby players float
        center.getNearbyEntities(radius, radius, radius).forEach(entity -> {
            if (entity instanceof Player && entity != p) {
                affectedEntities.add(entity);
                ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 1));
            }
        });

        // Convert a selection of surface blocks into floating blocks
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (random.nextInt(100) < 10) { // 10% chance to convert a block
                    Location loc = center.clone().add(x, 0, z);
                    loc.setY(world.getHighestBlockYAt(loc));
                    Block block = world.getBlockAt(loc);
                    if (block.getType() != Material.AIR && block.getType() != Material.BEDROCK) {
                        FallingBlock fallingBlock = world.spawnFallingBlock(block.getLocation().add(0.5, 1, 0.5), block.getBlockData());
                        fallingBlock.setDropItem(false);
                        fallingBlock.setGravity(false); // Prevent the block from falling immediately
                        fallingBlock.setVelocity(new Vector(0, 0.2, 0)); // Make the block float up
                        fallingBlock.setHurtEntities(true);
                        affectedBlocks.add(fallingBlock);
                        block.setType(Material.AIR);
                    }
                }
            }
        }

        // Add a cool spiral particle effect with dark purple redstone particles
        new BukkitRunnable() {
            double angle = 0; // Start angle

            @Override
            public void run() {
                if (angle >= 360 * 5) { // 5 rotations
                    this.cancel(); // Stop the effect after completing the spiral
                    return;
                }

                double radians = Math.toRadians(angle);
                double radius = angle / 360 * 0.5; // Gradually increase the radius for the spiral effect

                // Calculate offset for the spiral
                double offsetX = radius * Math.cos(radians);
                double offsetZ = radius * Math.sin(radians);

                // Spawn dark purple redstone particles in a spiral around the player
                world.spawnParticle(Particle.REDSTONE, center.clone().add(offsetX, 0, offsetZ), 1, 0, 0, 0, 0, darkPurpleDustOptions);

                // Spawn dark purple redstone particles in a spiral around each floating block
                for (FallingBlock fallingBlock : affectedBlocks) {
                    Location blockLoc = fallingBlock.getLocation();
                    world.spawnParticle(Particle.REDSTONE, blockLoc.clone().add(offsetX, 0, offsetZ), 1, 0, 0, 0, 0, darkPurpleDustOptions);
                }

                // Optionally, spawn dark purple redstone particles in a spiral around each affected entity
                for (Entity entity : affectedEntities) {
                    Location entityLoc = entity.getLocation();
                    world.spawnParticle(Particle.REDSTONE, entityLoc.clone().add(offsetX, 0, offsetZ), 1, 0, 0, 0, 0, darkPurpleDustOptions);
                }

                angle += 15; // Increase the angle to move the spiral
            }
        }.runTaskTimer(MobsV3.MOBS, 0L, 1L); // Schedule to run every tick for smooth animation

        // After 10 seconds, "throw" everything away
        new BukkitRunnable() {
            @Override
            public void run() {
                affectedEntities.forEach(entity -> {
                    Vector direction = entity.getLocation().toVector().subtract(center.toVector()).normalize().multiply(5).setY(2);
                    entity.setVelocity(direction);
                });

                affectedBlocks.forEach(fallingBlock -> {
                    Vector direction = fallingBlock.getLocation().toVector().subtract(center.toVector()).normalize().multiply(2).setY(1);
                    fallingBlock.setVelocity(direction);
                    fallingBlock.setDropItem(true); // Allow the block to drop as an item now
                    fallingBlock.setGravity(true); // Let gravity take effect again
                });

                world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
                // Particles for visual effect
                world.spawnParticle(Particle.EXPLOSION_LARGE, center, 20, 0.5, 0.5, 0.5, 0.05);
            }
        }.runTaskLater(MobsV3.MOBS, 20L * 5); // 5 seconds
    }



    @Override
    public Boolean performAlt(Player p) {
        if (Cooldowns.handleCooldown(p, name + "-Alt")) return false;

        Bukkit.getServer().getOnlinePlayers().forEach(player -> player.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.0f));

        p.setVelocity(new Vector(0, 0, 0));
        p.setVelocity(new Vector(0, 4, 0));
        //p.addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(160, 2));

        MobsV3.MOBS.scheduleTaskLater(() -> {
            p.setGliding(true);
            DragonFlyListener.setFlying(p);
        }, 20);

        return true;
    }

    @Override
    public Boolean performAlt2(Player player) {
        if (Cooldowns.handleCooldown(player, name + "-Alt2")) return false;

        final double radius = 4; // Radius around the player
        final int durationInSeconds = 5; // Duration of the dragon acid effect
        final World world = player.getWorld();
        final Location center = player.getLocation();

        // Start repeating task to spawn dragon acid particles and deal damage
        BukkitTask particleTask = new BukkitRunnable() {
            @Override
            public void run() {
                // Spawn particles to simulate dragon acid
                for (int i = 0; i < 360; i += 20) {
                    double angle = Math.toRadians(i);
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);
                    Location particleLocation = center.clone().add(x, 0.25, z);
                    world.spawnParticle(Particle.DRAGON_BREATH, particleLocation, 10, 0.5, 0, 0.5, 0);
                }

                // Check for and damage nearby players except the performer
                world.getNearbyEntities(center, radius, radius, radius).stream()
                        .filter(e -> e instanceof Player && e != player)
                        .map(e -> (Player) e)
                        .forEach(target -> target.damage(6, player)); // Deal damage
            }
        }.runTaskTimer(MobsV3.MOBS, 0L, 20L); // Schedule to run every second

        // Schedule task to stop the particle effect after the duration ends
        new BukkitRunnable() {
            @Override
            public void run() {
                particleTask.cancel(); // Correctly stop the repeating task
            }
        }.runTaskLater(MobsV3.MOBS, 20L * durationInSeconds);

        return true;
    }


}