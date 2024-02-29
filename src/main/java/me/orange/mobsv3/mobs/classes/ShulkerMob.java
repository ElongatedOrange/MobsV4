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

import java.util.ArrayList;
import java.util.List;

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
    public String getAltEmoji() {
        return null;
    }

    @Override
    public String getAlt() {
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
        final double radius = 3.0; // Radius around the player
        // Define the color and size of the redstone particles
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(128, 0, 128), 1);

        for (int i = 0; i < 360; i += 10) { // Increase the step to decrease density of particles
            double angle = (i * Math.PI / 180);
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            world.spawnParticle(Particle.REDSTONE, p.getLocation().add(x, 1, z), 1, 0, 0, 0, 0, dustOptions);
        }

        // Apply effects to players within a 3-block radius
        List<Entity> nearbyEntities = p.getNearbyEntities(radius, radius, radius);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player) {
                Player target = (Player) entity;

                // Levitation 5 for 10 seconds
                target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 200, 4));

                // Poison 2 for 15 seconds
                target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 254));

                // Slowness 1 for 15 seconds
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 0));
            }
        }
    }

    @Override
    public Boolean performAlt(Player p) {
        return null;
    }
}