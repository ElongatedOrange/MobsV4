package me.orange.mobsv4.mobs.classes;

import me.orange.mobsv4.MobsV4;
import me.orange.mobsv4.hex.HexUtils;
import me.orange.mobsv4.mobs.BaseMob;
import me.orange.mobsv4.mobs.Cooldowns;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IronGolemMob extends BaseMob {
    public static String name = "IronGolem";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPrefix() {
        return "§f";
    }

    @Override
    public String getPrimaryEmoji() {
        return getPrefix() + "👊";
    }

    @Override
    public boolean hasAltAbility() {
        return true;
    }

    @Override
    public String getAltEmoji() {
        return getPrefix() + "🌹";
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
        return 26;
    }

    @Override
    public ArrayList<String> getLore(Player token) {
        // Lore
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§9Token Abilities:");
        lore.add("  " + getPrefix() + "👊 Iron Fist §8(Right Click)");
        lore.add("  §fWhen player hit §elaunches §fplayer up in");
        lore.add("  §ethe air while dealing §edouble §fdamage");
        lore.add("  ");
        lore.add("  " + getPrefix() + "🌹 Golem's Love! §8(Left Click)");
        lore.add("  §fGives §eRegeneration 2 §ffor 5 seconds to");
        lore.add("  §feveryone in a 3 block radius");
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
        return new ArrayList<ArrayList<Object>>() {{
            add(new ArrayList<Object>() {{
                add(PotionEffectType.DAMAGE_RESISTANCE);
                add(1);
            }});

            add(new ArrayList<Object>() {{
                add(PotionEffectType.SLOW);
                add(1);
            }});
        }};
    }

    @Override
    public void perform(Player player) {
        if (Cooldowns.handleCooldown(player, name)) return;

        // Set a metadata flag on the player to indicate the next hit should launch the target
        player.setMetadata("launchUp", new FixedMetadataValue(MobsV4.MOBS, true));
        player.sendMessage("§aNext hit will launch the target and deal double damage!");

        // Particle effect options for white color
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 255, 255), 1.0f);

        // Create a circle of particles around the player
        for (int i = 0; i < 360; i += 10) {
            double angle = Math.toRadians(i);
            double x = Math.cos(angle) * 1.5; // Radius of 1.5 blocks around the player
            double z = Math.sin(angle) * 1.5;
            player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().add(x, 1.0, z), 1, dustOptions);
        }

        // Optionally play a sound to indicate the ability activation
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }



    @Override
    public Boolean performAlt(Player player) {
        if (Cooldowns.handleCooldown(player, name + "-Alt")) return false;

        // Define the potion effect and its duration
        PotionEffect regenEffect = new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 1); // Duration in ticks (20 ticks = 1 second)

        // Apply the effect to the player
        player.addPotionEffect(regenEffect);

        // Get nearby players and apply the same effect
        List<Entity> nearbyEntities = player.getNearbyEntities(3.0, 3.0, 3.0);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player) {
                Player nearbyPlayer = (Player) entity;
                nearbyPlayer.addPotionEffect(regenEffect);
            }
        }

        // Particle effect for the red sphere
        spawnRedSphere(player.getLocation(), 3);

        return true;
    }

    private void spawnRedSphere(Location center, double radius) {
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.0f);
        int particles = 100; // Adjust the number for more or less density

        for (int i = 0; i < particles; i++) {
            double angle = 2 * Math.PI * i / particles;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            for (double y = center.getY() - radius; y < center.getY() + radius; y += 0.5) {
                Location particleLoc = new Location(center.getWorld(), x, y, z);
                if (particleLoc.distance(center) <= radius) { // Ensure it's within a sphere
                    center.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 1, dustOptions);
                }
            }
        }
    }


    @Override
    public Boolean performAlt2(Player player) {
        return false;
    }

}