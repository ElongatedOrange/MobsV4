package me.orange.mobsv4.mobs.classes;

import me.orange.mobsv4.MobsV4;
import me.orange.mobsv4.mobs.BaseMob;
import me.orange.mobsv4.mobs.Cooldowns;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class ChickenMob extends BaseMob {
    public static String name = "Chicken";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPrefix() {
        return "§7";
    }

    @Override
    public String getPrimaryEmoji() {
        return getPrefix() + "🪶";
    }

    @Override
    public boolean hasAltAbility() {
        return true;
    }

    @Override
    public String getAltEmoji() {
        return getPrefix() + "🐓";
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
    public ArrayList<String> getLore(Player player) {
        // Lore
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§9Token Abilities:");
        lore.add("  " + getPrefix() + "🪶 Fast AF §8(Right Click)");
        lore.add("  §fGain §eHaste §ffor §c15 §fseconds.");
        lore.add("  ");
        lore.add("  " + getPrefix() + "🐓 Chicken Out §8(Left Click)");
        lore.add("  §eDash §faway from your enemies.");
        lore.add("  §7(" + MobsV4.COOLDOWNS.getCooldown(name, player) + "s)");

        return lore;
    }

    @Override
    public ArrayList<String> getSpecial() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("  " + getPrefix() + "⚠ Feather Falling");
        lore.add("  §fDoesn't take fall damage!");

        return lore;
    }

    @Override
    public ArrayList<ArrayList<Object>> getEffects() {
        // Potion Effects
        return new ArrayList<ArrayList<Object>>() {{
            add(new ArrayList<Object>() {{
                add(PotionEffectType.SPEED);
                add(1);
            }});
        }};
    }

    @Override
    public void perform(Player p) {
        if (Cooldowns.handleCooldown(p, name)) return;

        Bukkit.getServer().getOnlinePlayers().forEach(player -> player.playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1.0f, 1.0f));

        p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 300, 9, false, false, false));
    }

    @Override
    public Boolean performAlt(Player player) {
        if (Cooldowns.handleCooldown(player, name + "-Alt")) return false;

        // Calculate the launch direction based on where the player is looking
        Vector direction = player.getLocation().getDirection();

        // Apply a velocity to the player to launch them into the sky at an angle
        Vector launchVelocity = direction.multiply(3).setY(1);
        player.setVelocity(launchVelocity);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_2, 1.0F, 1.0F);

        // Schedule a repeating task to create particles following the player
        new BukkitRunnable() {
            int count = 0; // Counter to limit the duration of the particle effect

            @Override
            public void run() {
                if (count > 20) { // Stop after 1 second (20 ticks)
                    this.cancel();
                    return;
                }

                // Spawn white particles at the player's location
                player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation(), 10, 0.3, 0.3, 0.3, 0, new Particle.DustOptions(Color.WHITE, 1));

                count++;
            }
        }.runTaskTimer(MobsV4.MOBS, 0L, 1L);

        return true;
    }

    @Override
    public Boolean performAlt2(Player player) {
        return false;
    }

}