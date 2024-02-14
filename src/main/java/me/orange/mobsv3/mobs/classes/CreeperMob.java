package me.orange.mobsv3.mobs.classes;

import me.orange.mobsv3.MobsV3;
import me.orange.mobsv3.mobs.BaseMob;
import me.orange.mobsv3.mobs.Cooldowns;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CreeperMob extends BaseMob {
    private MobsV3 plugin;
    public static String name = "Creeper";

    public CreeperMob(MobsV3 plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPrefix() {
        return "§a";
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
        lore.add("  " + getPrefix() + "💣 Boom! §8(Right Click)");
        lore.add("  §fTurn into a Explosive Menace!");
        lore.add("  §7(" + MobsV3.COOLDOWNS.getCooldown(name, token) + "s)");
        return lore;
    }

    @Override
    public ArrayList<String> getSpecial() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("  " + getPrefix() + "🧨 Blast Resistant");
        lore.add("  §fImmune to §eexplosions");

        return lore;
    }

    @Override
    public ArrayList<ArrayList<Object>> getEffects() {
        // Potion Effects
        return new ArrayList<ArrayList<Object>>() {{
            add(new ArrayList<Object>() {{
                add(PotionEffectType.SPEED);
                add(0);
            }});
        }};
    }

    private final HashMap<UUID, ItemStack[]> inventories = new HashMap<>();
    private final HashMap<UUID, ItemStack[]> armors = new HashMap<>();


    @Override
    public void perform(Player p) {
        if (Cooldowns.handleCooldown(p, name)) return;

        // Play creeper sound effect to all players
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            player.playSound(p.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1.0f, 1.0f);
        });

        // Schedule the custom explosion 2 seconds later
        new BukkitRunnable() {
            @Override
            public void run() {
                // Create an explosion effect without breaking blocks or directly damaging entities
                p.getWorld().createExplosion(p.getLocation(), 2.0F, false, true);

                // Manually deal damage to nearby entities
                for (Entity entity : p.getNearbyEntities(4.0, 4.0, 4.0)) {
                    if (entity instanceof LivingEntity) {
                        LivingEntity nearbyPlayer = (Player) entity;

                        // Directly set the health, considering current health and max health
                        double newHealth = nearbyPlayer.getHealth() - 6.0; // 5 hearts worth of damage
                        if (newHealth < 0) newHealth = 0; // Ensure not setting health below 0

                        // Apply "true damage" by directly adjusting health
                        nearbyPlayer.setHealth(newHealth);

                        nearbyPlayer.damage(1, p);
                    }
                }
            }
        }.runTaskLater(MobsV3.MOBS, 40); // Delay of 2 seconds (40 ticks)
    }

    @Override
    public Boolean performAlt(Player p) {
        return null;
    }
}