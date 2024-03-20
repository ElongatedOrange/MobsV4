package me.orange.mobsv3.mobs.classes;

import me.orange.mobsv3.MobsV3;
import me.orange.mobsv3.hex.HexUtils;
import me.orange.mobsv3.mobs.BaseMob;
import me.orange.mobsv3.mobs.Cooldowns;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WitchMob extends BaseMob {
    public static String name = "Witch";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPrefix() {
        return HexUtils.format("#800080");
    }

    @Override
    public String getPrimaryEmoji() {
        return getPrefix() + "⚡";
    }

    @Override
    public boolean hasAltAbility() {
        return true;
    }

    @Override
    public String getAltEmoji() {
        return getPrefix() + "🍷";
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
        return 22;
    }

    @Override
    public ArrayList<String> getLore(Player token) {
        // Lore
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§9Token Abilities:");
        lore.add("  " + getPrefix() + "⚡ Lightning §8(Right Click)");
        lore.add("  §fStrike players in a §c5 §eblock radius §faround");
        lore.add("  §fyou with §elighning §fand receive §eStrength §fand");
        lore.add("  §eSpeed.");
        lore.add("");
        lore.add("  " + getPrefix() + "🍷 Potion Throw §8(Left Click)");
        lore.add("  §fThrow an §einsta damage potion §finfront of you.");
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
            add(new ArrayList<>() {{
                add(PotionEffectType.SPEED);
                add(0);
            }});

            add(new ArrayList<Object>() {{
                add(PotionEffectType.REGENERATION);
                add(0);
            }});
        }};
    }

    @Override
    public void perform(Player p) {
        if (Cooldowns.handleCooldown(p, name)) return;

        // Get player's location
        Location playerLocation = p.getLocation();

        // Get entities within a 5 block radius of the player
        List<Player> nearbyEntities = (List<Player>) playerLocation.getWorld().getNearbyPlayers(playerLocation, 5.0, 5.0, 5.0);
        nearbyEntities.remove(p);

        // Strike each entity with lightning
        for (Entity entity : nearbyEntities) {
            Location entityLocation = entity.getLocation();
            entityLocation.getWorld().strikeLightning(entityLocation);
        }

        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 300, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 300, 1));
    }

    @Override
    public Boolean performAlt(Player p) {
        if (Cooldowns.handleCooldown(p, name + "-Alt")) return false;

        List<PotionEffect> effects = Arrays.asList(
                new PotionEffect(PotionEffectType.HARM, 600, 2)
        );

        Random random = new Random();
        PotionEffect chosenEffect = effects.get(random.nextInt(effects.size()));

        ItemStack item = new ItemStack(Material.SPLASH_POTION, 1);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.addCustomEffect(chosenEffect, true);
        meta.setColor(chosenEffect.getColor());
        item.setItemMeta(meta);

        ThrownPotion thrownPotion = p.launchProjectile(ThrownPotion.class);
        thrownPotion.setItem(item);
        thrownPotion.setPotionMeta(meta);

        return true;
    }

    @Override
    public Boolean performAlt2(Player player) {
        return false;
    }
}