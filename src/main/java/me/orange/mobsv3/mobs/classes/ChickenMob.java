package me.orange.mobsv3.mobs.classes;

import me.orange.mobsv3.MobsV3;
import me.orange.mobsv3.hex.HexUtils;
import me.orange.mobsv3.mobs.BaseMob;
import me.orange.mobsv3.mobs.Cooldowns;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
        lore.add("  " + getPrefix() + "🪶 Fast AF §8(Right Click)");
        lore.add("  §fGain §eHaste §ffor §c10 §fseconds.");
        lore.add("  §7(" + MobsV3.COOLDOWNS.getCooldown(name, token) + "s)");

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

        p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 200, 9, false, false, false));
    }

    public void Egg(Player p) {
        Bukkit.getServer().getOnlinePlayers().forEach(player -> player.playSound(p.getLocation(), Sound.ENTITY_EGG_THROW, 1.0f, 1.0f));

        Egg egg = p.launchProjectile(Egg.class);
    }

    @Override
    public Boolean performAlt(Player p) {
        return null;
    }
}