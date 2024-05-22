package me.orange.mobsv4.mobs.classes;

import me.orange.mobsv4.MobsV4;
import me.orange.mobsv4.hex.HexUtils;
import me.orange.mobsv4.mobs.BaseMob;
import me.orange.mobsv4.mobs.Cooldowns;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EvokerMob extends BaseMob {
    public static String name = "Evoker";

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
        return getPrefix() + "🔮";
    }

    @Override
    public boolean hasAltAbility() {
        return true;
    }

    @Override
    public String getAltEmoji() {
        return getPrefix() + "🐊";
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
        return 24;
    }

    @Override
    public ArrayList<String> getLore(Player token) {
        // Lore
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§9Token Abilities:");
        lore.add("  " + getPrefix() + "🔮 Vexed §8(Right Click)");
        lore.add("  §fSpawn §c3 §evexes to attack enemies");
        lore.add("");
        lore.add("  " + getPrefix() + "🐊 Chomp! §8(Left Click)");
        lore.add("  §fSummon the §eCrocodile Teeth");
        lore.add("  §7(" + MobsV4.COOLDOWNS.getCooldown(name, token) + "s)");

        return lore;
    }

    @Override
    public ArrayList<String> getSpecial() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("  " + getPrefix() + "👼 Second Chance");
        lore.add("  §f25% chance to pop an extra §etotem");

        return lore;
    }

    @Override
    public ArrayList<ArrayList<Object>> getEffects() {
        // Potion Effects
        return new ArrayList<>() {{
            add(new ArrayList<>() {{
                add(PotionEffectType.SPEED);
                add(0);
            }});
        }};
    }

    @Override
    public void perform(Player player) {
        if (Cooldowns.handleCooldown(player, name)) return;

        World world = player.getWorld();
        Location spawnLocation = player.getLocation();

        // Spawn 3 vexes and tag them
        for (int i = 0; i < 3; i++) {
            Vex vex = (Vex) world.spawnEntity(spawnLocation, EntityType.VEX);
            vex.setMetadata("PlayerOwnedVex", new FixedMetadataValue(MobsV4.MOBS, player.getUniqueId().toString()));
        }
    }

    @Override
    public Boolean performAlt(Player player) {
        if (Cooldowns.handleCooldown(player, name + "-Alt")) return false;

        // Get the player's location
        Location startLocation = player.getLocation();
        // Default to the direction the player is facing
        Vector direction = startLocation.getDirection();

        // Find the nearest player within a certain radius
        Player nearestPlayer = findNearestPlayer(player, 50);
        if (nearestPlayer != null) {
            // Calculate the direction vector towards the nearest player
            direction = nearestPlayer.getLocation().toVector().subtract(startLocation.toVector()).normalize();
        }

        double distance = 0.5; // Distance between fangs
        int count = 20; // Number of times fangs are spawned

        // Loop to create multiple fangs in a line
        for (int i = 1; i <= count; i++) {
            // Calculate the next location in the direction
            Location fangLocation = startLocation.clone().add(direction.multiply(distance * i));
            fangLocation.setY(startLocation.getY()); // Ensure the fangs spawn at the player's feet level

            // Spawn the evoker fangs at the calculated location
            Entity fang = fangLocation.getWorld().spawnEntity(fangLocation, EntityType.EVOKER_FANGS);
            // Set metadata to recognize the fangs are from this player
            fang.setMetadata("owner", new FixedMetadataValue(MobsV4.MOBS, player.getUniqueId().toString()));
        }

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK, 1.0F, 1.0F);

        return true;
    }

    private Player findNearestPlayer(Player player, double radius) {
        double nearestDistanceSquared = Double.MAX_VALUE;
        Player nearestPlayer = null;

        for (Player p : player.getWorld().getPlayers()) {
            if (p.equals(player)) continue; // Skip the player themselves
            double distanceSquared = player.getLocation().distanceSquared(p.getLocation());
            if (distanceSquared < nearestDistanceSquared && distanceSquared <= radius * radius) {
                nearestPlayer = p;
                nearestDistanceSquared = distanceSquared;
            }
        }

        return nearestPlayer;
    }



    @Override
    public Boolean performAlt2(Player player) {
        return false;
    }
}