package me.orange.mobsv3.mobs.classes;

import me.orange.mobsv3.MobsV3;
import me.orange.mobsv3.hex.HexUtils;
import me.orange.mobsv3.math.Raycast;
import me.orange.mobsv3.mobs.BaseMob;
import me.orange.mobsv3.mobs.Cooldowns;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class WardenMob extends BaseMob {
    public static String name = "Warden";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPrefix() {
        return "§3";
    }

    @Override
    public String getPrimaryEmoji() {
        return getPrefix() + "💥";
    }

    @Override
    public boolean hasAltAbility() {
        return false;
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
        lore.add("  " + getPrefix() + "💥 Sonic Boom §8(Right Click)");
        lore.add("  §fShoot the Warden's §eSonic Boom");
        lore.add("  §fin the direction you are facing");
        lore.add("  §fand get §eStrength 2 §ffor 30 seconds");
        lore.add("  §7(" + MobsV3.COOLDOWNS.getCooldown(name, token) + "s)");
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
                add(0);
            }});

            add(new ArrayList<Object>() {{
                add(PotionEffectType.SLOW);
                add(0);
            }});
        }};
    }

    @Override
    public void perform(Player p) {
        if (Cooldowns.handleCooldown(p, name)) return;

        Location start = this.getRightSide(p.getEyeLocation(), 0.5);
        Location end = p.getEyeLocation().clone().add(p.getEyeLocation().getDirection().normalize().multiply(15));
        Particle particle = Particle.SONIC_BOOM;

        MobsV3.spawnParticleLine(start, end, particle, 50, 1,
                0.1D, 0.1D, 0.1D, 0D, null, false, l -> l.getBlock().isPassable());

        Bukkit.getServer().getOnlinePlayers().forEach(player -> player.playSound(p.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1.0f, 1.0f));

        ArrayList<LivingEntity> hits = Raycast.getTargetEntities(p);

        if (hits.size() > 0 && hits.get(0) != null) {
            hits.forEach((hit) -> {
                Vector knock = p.getLocation().getDirection().multiply(-1);
                hit.knockback(1.5, knock.getX(), knock.getZ());
                hit.setHealth(hit.getHealth() - 8);
                hit.damage(2.0, p);
            });
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 600, 1, false, false, true));
    }

    @Override
    public Boolean performAlt(Player p) {
        return false;
    }

    @Override
    public Boolean performAlt2(Player player) {
        return false;
    }

    public Location getRightSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().subtract(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance)).subtract(0, 0.4, 0);
    }
}