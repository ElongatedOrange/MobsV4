package me.orange.mobsv4;

import me.orange.mobsv4.hex.Metrics;
import me.orange.mobsv4.listeners.*;
import me.orange.mobsv4.mobs.BaseMob;
import me.orange.mobsv4.mobs.Cooldowns;
import me.orange.mobsv4.mobs.ModelData;
import me.orange.mobsv4.mobs.classes.ResetMob;
import me.orange.mobsv4.ui.CustomItemGUI;
import me.orange.mobsv4.ui.MobGuide;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;

import static org.bukkit.potion.PotionEffect.INFINITE_DURATION;

public final class MobsV4 extends JavaPlugin {
    public static MobsV4 MOBS;
    private static MobsV4 instance;
    public static Cooldowns COOLDOWNS;

    public static void setMob(Player player, BaseMob mob) {
        // Remove all current potion effects
        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());

        // Access the plugin's data folder statically
        File playerDataFile = new File(MobsV4.getInstance().getDataFolder(), "playerData.yml");
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Load the configuration from playerData.yml
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);

        String playerUUID = player.getUniqueId().toString();
        String mobPath = playerUUID + ".mob";
        String cooldownPath = playerUUID + ".cooldown";

        if (mob instanceof ResetMob) {
            playerData.set(mobPath, null);
            playerData.set(cooldownPath, 60); // Default cooldown of 60 seconds
        } else {
            playerData.set(mobPath, mob.getName());
            if (!playerData.contains(cooldownPath)) {
                playerData.set(cooldownPath, 60); // Default cooldown of 60 seconds
            }

            ItemStack token = new ItemStack(Material.ECHO_SHARD, 1);
            ItemMeta meta = token.getItemMeta();
            meta.setDisplayName("§f" + mob.getPrefix() + mob.getName().replace("_", " ") + " Token");
            meta.setCustomModelData(ModelData.getModelData(mob.getName()));
            token.setItemMeta(meta);
            token.setLore(mob.getLore(null));
            player.getInventory().addItem(token);

            for (ArrayList<Object> effect : mob.getEffects()) {
                player.addPotionEffect(new PotionEffect((PotionEffectType) effect.get(0), INFINITE_DURATION, (Integer) effect.get(1), false, false, false));
            }
        }

        try {
            playerData.save(playerDataFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(mob.getHealth());
        player.setHealth(mob.getHealth());

        Cooldowns.startActionBarUpdateTask(player, mob.getName());
    }

    public static void addEffects(Player player, BaseMob mob) {
        for (ArrayList<Object> effect : mob.getEffects()) {
            player.addPotionEffect(new PotionEffect((PotionEffectType) effect.get(0), INFINITE_DURATION, (Integer) effect.get(1), false, false, false));
        }
    }

    public void loadAllMobAssignments() {
        File playerDataFile = new File(this.getDataFolder(), "playerData.yml");
        if (!playerDataFile.exists()) {
            return; // File doesn't exist, nothing to load
        }

        FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);
        for (Player player : Bukkit.getOnlinePlayers()) {
            String mobName = playerData.getString(player.getUniqueId().toString() + ".mob");
            if (mobName != null && !mobName.isEmpty()) {
                BaseMob mob = MobManager.findMobByName(mobName);
                if (mob != null) {
                    MobsV4.setMob(player, mob);
                }
            }
        }
    }


    @Override
    public void onEnable() {
        MOBS = this;
        instance = this;
        COOLDOWNS = new Cooldowns();

        this.saveDefaultConfig();

        loadAllMobAssignments();

        getCommand("mob").setExecutor(new MobManager(this));
        getCommand("mob").setTabCompleter(new TabCompletion());
        getCommand("guide").setExecutor(new MobManager(this));

        new BukkitRunnable() {
            @Override
            public void run() {
                new MobEffectApplier(MOBS).run();
            }
        }.runTaskTimer(this, 0L, 20L); // Run the task every second (20 ticks)


        getServer().getPluginManager().registerEvents(new EventManager(this), this);
        getServer().getPluginManager().registerEvents(new ShulkerSneakListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathAndRespawnListener(), this);
        getServer().getPluginManager().registerEvents(new ChickenFallDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new BlazeLavaListener(this), this);
        getServer().getPluginManager().registerEvents(new IronGolemListener(), this);
        getServer().getPluginManager().registerEvents(new EvokerListener(this), this);
        //getServer().getPluginManager().registerEvents(new FireballDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new ArrowImpactListener(), this);
        getServer().getPluginManager().registerEvents(new DragonFlyListener(), this);
        //getServer().getPluginManager().registerEvents(new SpecialEvents(this), this);
        getServer().getPluginManager().registerEvents(new MobGuide(), this);
        //getServer().getPluginManager().registerEvents(new RecipeManager(), this);

        this.getCommand("mobsitems").setExecutor(new CustomItemCommand());
        this.getServer().getPluginManager().registerEvents(new CustomItemGUI(), this);

        //RecipeManager.loadRecipes();

        new Metrics(MOBS, 19514);

    }

    public static MobsV4 getInstance() {
        return instance;
    }


    @Override
    public void onDisable() {

    }

    public void scheduleTaskLater(Runnable task, long delayTicks) {
        getServer().getScheduler().runTaskLater(MOBS, task, delayTicks);
    }


    public static void spawnParticleLine(Location start, Location end, Particle particle, int pointsPerLine, int particleCount, double offsetX, double offsetY, double offsetZ,
                                         double extra, @Nullable Double data, boolean forceDisplay, @Nullable Predicate<Location> operationPerPoint) {
        double d = start.distance(end) / pointsPerLine;

        for (int i = 0; i < pointsPerLine; i++) {
            Location l = start.clone();
            Vector direction = end.toVector().subtract(start.toVector()).normalize();
            Vector v = direction.multiply(i * d);
            l.add(v.getX(), v.getY(), v.getZ());

            if (operationPerPoint == null) {
                start.getWorld().spawnParticle(particle, l, particleCount, offsetX, offsetY, offsetZ, extra, data, forceDisplay);
                continue;
            }

            if (operationPerPoint.test(l)) {
                start.getWorld().spawnParticle(particle, l, particleCount, offsetX, offsetY, offsetZ, extra, data, forceDisplay);
            }
        }
    }
}
