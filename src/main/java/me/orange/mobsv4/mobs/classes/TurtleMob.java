package me.orange.mobsv4.mobs.classes;

import me.orange.mobsv4.MobsV4;
import me.orange.mobsv4.mobs.BaseMob;
import me.orange.mobsv4.mobs.Cooldowns;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class TurtleMob extends BaseMob {
    public static String name = "Turtle";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPrefix() {
        return "§2";
    }

    @Override
    public String getPrimaryEmoji() {
        return getPrefix() + "🪨";
    }

    @Override
    public boolean hasAltAbility() {
        return true;
    }

    @Override
    public String getAltEmoji() {
        return getPrefix() + "💣";
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
        lore.add("  " + getPrefix() + "🪨 Harden §8(Right Click)");
        lore.add("  §fGain §eResistance 3 §fand §eStrength 3");
        lore.add("  §ffor §c10 §fseconds.");
        lore.add("  ");
        lore.add("  " + getPrefix() + "💣 Explosive Baby Turtles! §8(Left Click)");
        lore.add("  §fShoots a §eTurtle Egg §fthat hatches");
        lore.add("  §c4 §eexplosive baby turtles");
        lore.add("  §7(" + MobsV4.COOLDOWNS.getCooldown(name, token) + "s)");

        return lore;
    }

    @Override
    public ArrayList<String> getSpecial() {
        return null;
    }

    @Override
    public ArrayList<ArrayList<Object>> getEffects() {
        // Potion Effects
        return new ArrayList<>() {{
            add(new ArrayList<Object>() {{
                add(PotionEffectType.WATER_BREATHING);
                add(0);
            }});
        }};
    }

    @Override
    public void perform(Player p) {
        if (Cooldowns.handleCooldown(p, name)) return;

        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 2, false, false, true));
        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 2, false, false, true));

        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            player.playSound(p.getLocation(), Sound.BLOCK_SNIFFER_EGG_HATCH, 1.0f, 1.0f);
        });
    }

    @Override
    public Boolean performAlt(Player player) {
        if (Cooldowns.handleCooldown(player, name + "-Alt")) return false;

        World world = player.getWorld();
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection().multiply(1.5); // Speed of the egg

        // Spawn the turtle egg entity
        FallingBlock turtleEgg = world.spawnFallingBlock(eyeLocation, Material.TURTLE_EGG.createBlockData());
        turtleEgg.setDropItem(false);
        turtleEgg.setVelocity(direction);

        // Create a task that will track the turtle egg
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!turtleEgg.isValid() || turtleEgg.isOnGround()) {
                    // When the egg lands, spawn baby turtles
                    for (int i = 0; i < 4; i++) {
                        spawnExplodingTurtle(world, turtleEgg.getLocation().add(0, 1, 0), player);
                        removeTurtleEggsAround(turtleEgg.getLocation());
                    }
                    // Remove the egg and cancel the task
                    turtleEgg.remove();
                    this.cancel();
                }
                // Spawn particles along the way
                world.spawnParticle(Particle.EXPLOSION_NORMAL, turtleEgg.getLocation(), 10, 0.3, 0.3, 0.3, 0.05);
            }
        }.runTaskTimer(MobsV4.MOBS, 0L, 1L);

        return true;
    }

    @Override
    public Boolean performAlt2(Player player) {
        return false;
    }

    private void spawnExplodingTurtle(World world, Location location, Player player) {
        Turtle turtle = (Turtle) world.spawnEntity(location, EntityType.TURTLE);
        turtle.setBaby();

        // Increase the spread and randomness of the bounce effect
        double spread = 0.5; // Increase this value for a wider spread
        Vector bounce = new Vector((Math.random() - 0.5) * spread, 0.5, (Math.random() - 0.5) * spread);
        turtle.setVelocity(bounce);

        // Create a task to make the turtle explode after some time
        new BukkitRunnable() {
            @Override
            public void run() {
                // Damage nearby entities excluding the player who performed the action
                turtle.getNearbyEntities(3, 3, 3).forEach(entity -> {
                    if (entity instanceof Player && entity != player) {
                        ((Player) entity).damage(30, player); // Damage value
                    }
                });

                // Create explosion effect
                world.createExplosion(turtle.getLocation(), 2F, false, false); // Explosion power, no fire, no block damage

                // Remove the turtle
                turtle.remove();
            }
        }.runTaskLater(MobsV4.MOBS, 20L);
    }

    private void removeTurtleEggsAround(Location location) {
        int radius = 3; // Radius around the location to check for turtle eggs
        World world = location.getWorld();

        // Calculate the bounds for the area to check
        int startX = location.getBlockX() - radius;
        int startY = location.getBlockY() - radius;
        int startZ = location.getBlockZ() - radius;
        int endX = location.getBlockX() + radius;
        int endY = location.getBlockY() + radius;
        int endZ = location.getBlockZ() + radius;

        // Iterate through each block in the area
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    // Get the block at the current location
                    Block block = world.getBlockAt(x, y, z);

                    // Check if the block is a turtle egg
                    if (block.getType() == Material.TURTLE_EGG) {
                        // Set the block to air, effectively removing the turtle egg
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }



}
