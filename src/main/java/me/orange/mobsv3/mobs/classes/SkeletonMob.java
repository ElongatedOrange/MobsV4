package me.orange.mobsv3.mobs.classes;

import me.orange.mobsv3.MobsV3;
import me.orange.mobsv3.hex.HexUtils;
import me.orange.mobsv3.mobs.BaseMob;
import me.orange.mobsv3.mobs.Cooldowns;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
    public String getPrimaryEmoji() {
        return getPrefix() + "🏹";
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
        return 20;
    }

    @Override
    public ArrayList<String> getLore(Player token) {
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

        List<ArmorStand> armorStands = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            ArmorStand armorStand = player.getWorld().spawn(player.getLocation(), ArmorStand.class, stand -> {
                stand.setVisible(false);
                stand.setGravity(false);
                stand.setCanPickupItems(false);
                stand.setHelmet(new ItemStack(Material.ARROW));
                stand.setHeadPose(new EulerAngle(Math.toRadians(0), Math.toRadians(90 * finalI), 0));
                stand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
            });
            armorStands.add(armorStand);
        }

        long initialDelay = 15;
        AtomicInteger launchedArrows = new AtomicInteger(0);

        // Shoot 3 arrows in the direction the player is looking
        for (int i = 0; i < 3; i++) {
            long delay = initialDelay * i;
            new BukkitRunnable() {
                @Override
                public void run() {
                    Arrow arrow = player.launchProjectile(Arrow.class);
                    arrow.setVelocity(player.getLocation().getDirection().multiply(5)); // Adjust speed
                    arrow.setDamage(5);
                    // Play shooting sound
                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0F, 1.0F);
                    // Remove armor stands when the last arrow is launched
                    if (launchedArrows.incrementAndGet() == 3) {
                        armorStands.forEach(Entity::remove);
                    }
                }
            }.runTaskLater(MobsV3.MOBS, delay);
        }

        new BukkitRunnable() {
            double t = 0;
            public void run() {
                if (launchedArrows.get() >= 3) {
                    this.cancel();
                    return;
                }

                t += Math.PI / 16;
                double radius = 1.5;
                for (int i = 0; i < armorStands.size(); i++) {
                    ArmorStand stand = armorStands.get(i);
                    double x = radius * Math.cos(t + i * 2 * Math.PI / armorStands.size());
                    double z = radius * Math.sin(t + i * 2 * Math.PI / armorStands.size());
                    stand.teleport(player.getLocation().add(x, 1.5, z));
                }
            }
        }.runTaskTimer(MobsV3.MOBS, 0, 1);
    }


    @Override
    public Boolean performAlt(Player player) {
        return null;
    }
}
