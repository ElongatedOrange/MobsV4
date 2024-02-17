package me.orange.mobsv3.mobs.classes;

import me.orange.mobsv3.MobsV3;
import me.orange.mobsv3.hex.HexUtils;
import me.orange.mobsv3.mobs.BaseMob;
import me.orange.mobsv3.mobs.Cooldowns;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class SkeletonMob extends BaseMob {
    public static String name = "Skeleton";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPrefix() {
        return HexUtils.format("#d3d3d3");
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
        lore.add("  " + getPrefix() + "🏹 Arrow Barrage §8(Right Click)");
        lore.add("  §fShoots §e3 arrows §fin the");
        lore.add("  §fdirection you are facing");
        lore.add("  §7(" + MobsV3.COOLDOWNS.getCooldown(name, token) + "s)");

        return lore;
    }

    @Override
    public ArrayList<String> getSpecial() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("  " + getPrefix() + "🦴 Skeleton's Strength");
        lore.add("  §fBow in off-hand gives §eStrength 2");
        return lore;
    }

    @Override
    public ArrayList<ArrayList<Object>> getEffects() {
        return new ArrayList<>();
    }

    @Override
    public void perform(Player player) {
        if (Cooldowns.handleCooldown(player, name)) return;

        long initialDelay = 15;

        // Shoot 3 arrows in the direction the player is looking
        for (int i = 0; i < 3; i++) {
            long delay = initialDelay * i;
            new BukkitRunnable() {
                @Override
                public void run() {
                    Arrow arrow = player.launchProjectile(Arrow.class);
                    arrow.setVelocity(player.getLocation().getDirection().multiply(5)); // Adjust speed
                    // Tag the arrow with metadata
                    arrow.setDamage(5);
                    // Play shooting sound
                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0F, 1.0F);
                }
            }.runTaskLater(MobsV3.MOBS, delay);
        }
    }

    @Override
    public Boolean performAlt(Player player) {
        return null;
    }
}
