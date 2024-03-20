package me.orange.mobsv3.listeners;

import me.orange.mobsv3.MobManager;
import me.orange.mobsv3.MobsV3;
import me.orange.mobsv3.mobs.BaseMob;
import me.orange.mobsv3.mobs.Cooldowns;
import me.orange.mobsv3.mobs.classes.DragonMob;
import me.orange.mobsv3.mobs.classes.ResetMob;
import me.orange.mobsv3.ui.MobSelectTitle;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static me.orange.mobsv3.mobs.Cooldowns.cooldownDisplayTasks;
import static me.orange.mobsv3.mobs.Cooldowns.startActionBarUpdateTask;
import static org.bukkit.potion.PotionEffect.INFINITE_DURATION;

public class EventManager implements Listener {

    private final MobsV3 plugin;

    public EventManager(MobsV3 plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) throws ReflectiveOperationException {
        Player p = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) return;

        BaseMob mob = isToken(item);

        // Load player's assigned mob from playerData.yml
        File playerDataFile = new File(plugin.getDataFolder(), "playerData.yml");
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);
        String mobName = playerData.getString(p.getUniqueId().toString() + ".mob");

        if (mob != null && mob.getName().equals(mobName)) {
            if (event.getAction().toString().contains("RIGHT")) {
                if (event.getClickedBlock() != null) event.setCancelled(true);
                mob.perform(p); // Perform the primary action associated with the mob
            } else if (event.getAction().toString().contains("LEFT")) {
                if (mob.getAlt() != null && mob.getAlt().equalsIgnoreCase("click")) {
                    mob.performAlt(p); // Perform the alternate action associated with the mob
                }
            }
            //MobsV3.MOBS.clearActionBarTaskForPlayer(p);
        } else if (item.getType() == Material.DRAGON_EGG && !mobName.equals("Dragon")) {
            if (event.getClickedBlock() == null) {
                for (ItemStack slot : p.getInventory().getContents()) {
                    if (slot == null || slot.getItemMeta() == null) {
                        continue;
                    }

                    ItemMeta meta = slot.getItemMeta();

                    if (isToken(slot) != null) {
                        slot.subtract(64);
                    }

                    if (slot.getType() == Material.DRAGON_EGG) {
                        slot.subtract(64);
                    }
                }

                DragonMob mobSelected = new DragonMob();

                MobsV3.setMob(p, new ResetMob());
                MobsV3.setMob(p, mobSelected);

                p.setPlayerListName(mobSelected.getPrefix() + "[" + mobSelected.getName() + "] " + p.getName());
                p.setDisplayName(mobSelected.getPrefix() + "[" + mobSelected.getName() + "] " + p.getName());

                Bukkit.getServer().getOnlinePlayers().forEach(
                        player -> player.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f)
                );

                MobsV3.MOBS.scheduleTaskLater(
                        () -> {
                            assert mob != null;
                            Cooldowns.startActionBarUpdateTask(p, mob.getName());
                        }, 20L);
            }
        }
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (event.isSneaking()) {
            BaseMob mob = isToken(item);

            if (mob != null) {
                if (mob.getAlt2() != null && mob.getAlt2().equalsIgnoreCase("crouch"))
                    mob.performAlt2(event.getPlayer());
            }
        }
    }

    public BaseMob isToken(ItemStack item) {
        for (BaseMob mob : MobManager.mobs) {
            if (item.getType() == Material.ECHO_SHARD
                    && item.hasItemMeta()
                    && item.getItemMeta().hasLore()
                    && item.getItemMeta().getDisplayName().contains(mob.getName().replace("_", " "))
                    && item.getItemMeta().getDisplayName().contains("Token")) {
                return mob;
            }
        }

        return null;
    }

    @EventHandler
    public void onExplosionDamage(EntityDamageEvent event) {
        // Check if the entity affected by the event is a player
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        // Check if the damage cause is explosion
        if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            // Load the playerData.yml file
            File playerDataFile = new File(MobsV3.getInstance().getDataFolder(), "playerData.yml");
            FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);

            // Check if the player has the mob type "creeper"
            String mobType = playerData.getString(player.getUniqueId().toString() + ".mob");
            if ("creeper".equalsIgnoreCase(mobType)) {
                // Cancel the event to prevent damage
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        File playerDataFile = new File(MobsV3.getInstance().getDataFolder(), "playerData.yml");
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);

        // Check if the player has a mob assigned in the file
        String mobName = playerData.getString(player.getUniqueId().toString() + ".mob");

        if (mobName == null || mobName.isEmpty()) {
            // The player does not have a mob assigned, treat as a new player
            // Schedule to assign a random mob
            MobsV3.MOBS.scheduleTaskLater(() -> {
                MobSelectTitle.pickRandomMob(player);
            }, 100); // 100 ticks delay
        } else {
            BaseMob mob = MobManager.findMobByName(mobName);
            player.setPlayerListName(mob.getPrefix() + "[" + mob.getName() + "] " + player.getName());
            player.setDisplayName(mob.getPrefix() + "[" + mob.getName() + "] " + player.getName());
            Cooldowns.startActionBarUpdateTask(player, mobName);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        clearActionBarTaskForPlayer(player);
    }

    public static void clearActionBarTaskForPlayer(Player player) {
        BukkitTask task = cooldownDisplayTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE || event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return;

        ItemStack item = event.getItemDrop().getItemStack();

        if (item.getItemMeta() != null
                && item.getItemMeta().getDisplayName().contains("Token")
                && item.getItemMeta().hasLore()) {

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null
                || event.getAction().equals(InventoryAction.NOTHING)
                || event.getClickedInventory() == null)
            return;

        Player player = (Player) event.getView().getPlayer();
        ItemStack item = event.getCurrentItem();

        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode().equals(GameMode.CREATIVE)) return;

        if ((event.getClick() == ClickType.NUMBER_KEY || event.getClick() == ClickType.SWAP_OFFHAND) && isToken(item) != null && !(event.getView().getTopInventory().getHolder() instanceof Player)) {
            event.setCancelled(true);
        }

        if (isToken(item) != null) {
            if (!(event.getInventory().getHolder() instanceof Player)) {
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 0.1f);
                event.setCancelled(true);
                player.updateInventory();
            }
        } else if (item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().getDisplayName().contains("Re-roll")) {
            if (event.getInventory().getHolder() instanceof AnvilInventory) {
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 0.1f);
                event.setCancelled(true);
                player.updateInventory();
            }
        } else if (item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().getDisplayName().contains("Essence")) {
            if (!(event.getInventory().getHolder() instanceof Player)) {
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 0.1f);
                event.setCancelled(true);
                player.updateInventory();
            }
        }
    }

    @EventHandler
    public void OnUseTotem(EntityResurrectEvent event) {
        Entity entity = event.getEntity();

        File playerDataFile = new File(MobsV3.getInstance().getDataFolder(), "playerData.yml");
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);
        String mobName = playerData.getString(entity.getUniqueId().toString() + ".mob");
        BaseMob mob = MobManager.findMobByName(mobName);

        Bukkit.getScheduler().runTaskLater(MobsV3.MOBS, () -> {
            if (mob == null) return;
            for (ArrayList<Object> effect : mob.getEffects()) {
                ((LivingEntity) entity).addPotionEffect(new PotionEffect((PotionEffectType) effect.get(0), INFINITE_DURATION, (Integer) effect.get(1), false, false, false));
            }
        }, 1);
    }


}
