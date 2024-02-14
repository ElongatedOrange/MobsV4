package me.orange.mobsv3.mobs.classes;

import me.orange.mobsv3.MobsV3;
import me.orange.mobsv3.mobs.BaseMob;
import me.orange.mobsv3.mobs.Cooldowns;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
    public String getAlt() {
        return null;
    }

    @Override
    public int getHealth() {
        return 20;
    }

    @Override
    public ArrayList<String> getLore(ItemStack token) {
        // Lore
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§9Token Abilities:");
        lore.add("  " + getPrefix() + "🪨 Harden §8(Right Click)");
        lore.add("  §fGain §eResistance 3§f and §eStrength 3");
        lore.add("  §ffor §c10 §fseconds.");
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
        return null;
    }
}
