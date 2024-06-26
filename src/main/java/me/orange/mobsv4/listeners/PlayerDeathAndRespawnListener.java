package me.orange.mobsv4.listeners;

import me.orange.mobsv4.MobManager;
import me.orange.mobsv4.MobsV4;
import me.orange.mobsv4.mobs.BaseMob;
import me.orange.mobsv4.mobs.Cooldowns;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.bukkit.potion.PotionEffect.INFINITE_DURATION;

public class PlayerDeathAndRespawnListener implements Listener {
    private final HashMap<UUID, String> mobTypesToReassign = new HashMap<>();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String currentMobType = getCurrentMobType(player);

        if (currentMobType != null) {
            if (!currentMobType.equals("Dragon")) {
                mobTypesToReassign.put(player.getUniqueId(), currentMobType);
            } else {
                mobTypesToReassign.put(player.getUniqueId(), "Chicken");
            }
        }

        if ((currentMobType != null) && (currentMobType.equals("Dragon"))) {
            event.getDrops().add(new ItemStack(Material.DRAGON_EGG));
        }

        // Prevent the Echo Shard "Token" from dropping
        List<ItemStack> tokens = event.getDrops().stream()
                .filter(item -> item.getType() == Material.ECHO_SHARD && item.hasItemMeta()
                        && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains("Token"))
                .collect(Collectors.toList());

        event.getDrops().removeAll(tokens);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        MobsV4.MOBS.scheduleTaskLater(
                () -> {
                    Player player = event.getPlayer();
                    UUID playerUUID = player.getUniqueId();

                    if (mobTypesToReassign.containsKey(playerUUID)) {
                        String mobType = mobTypesToReassign.get(playerUUID);
                        BaseMob mob = MobManager.findMobByName(mobType);
                        if (mob != null) {
                            MobsV4.setMob(player, mob);
                            for (ArrayList<Object> effect : mob.getEffects()) {
                                player.addPotionEffect(new PotionEffect((PotionEffectType) effect.get(0), INFINITE_DURATION, (Integer) effect.get(1), false, false, true));
                            }
                            Cooldowns.startActionBarUpdateTask(player, mob.getName());
                        }
                        mobTypesToReassign.remove(playerUUID);
                    }
                }, 20L);
    }

    private String getCurrentMobType(Player player) {
        File playerDataFile = new File(MobsV4.getInstance().getDataFolder(), "playerData.yml");
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);

        String mobName = playerData.getString(player.getUniqueId().toString() + ".mob");
        return mobName;
    }
}

