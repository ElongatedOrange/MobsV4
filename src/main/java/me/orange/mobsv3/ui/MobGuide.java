package me.orange.mobsv3.ui;

import me.orange.mobsv3.MobManager;
import me.orange.mobsv3.hex.HexUtils;
import me.orange.mobsv3.mobs.BaseMob;
import me.orange.mobsv3.mobs.classes.ResetMob;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;

public class MobGuide implements Listener {
    public static Inventory inventory;

    public MobGuide() {
        inventory = Bukkit.createInventory(null, 36, "Mob Guide");
        initializeItems();
    }

    public static void initializeItems() {
        int slotIndex = 10;

        for (BaseMob mob : MobManager.mobs) {
            if (mob instanceof ResetMob) continue;

            Material mat = switch (mob.getName()) {
                default -> Material.BARRIER;
                case "Blaze" -> Material.BLAZE_POWDER;
                case "Chicken" -> Material.EGG;
                case "Creeper" -> Material.GUNPOWDER;
                case "Shulker" -> Material.SHULKER_SHELL;
                case "Skeleton" -> Material.BONE;
                case "Turtle" -> Material.SCUTE;
                case "Warden" -> Material.ECHO_SHARD;
                case "Witch" -> Material.POTION;
            };

            inventory.setItem(slotIndex, createGuiItem(mat, "§6The " + mob.getName(),
                    new ArrayList<String>() {{
                        add(HexUtils.format("#FF0056" + "❤ " + mob.getHealth() / 2));
                        add("");
                        add("§9Unique Effects");

                        StringBuilder effectString = new StringBuilder();

                        for (int i = 0; i <= mob.getEffects().size() - 1; i++) {
                            PotionEffectType type = (PotionEffectType) mob.getEffects().get(i).get(0);
                            Integer amplifier = (Integer) mob.getEffects().get(i).get(1);

                            String realEffect = StringUtils.capitaliseAllWords(type.getName().replace("_", " ").toLowerCase());

                            if (type == PotionEffectType.INCREASE_DAMAGE || type == PotionEffectType.DAMAGE_RESISTANCE || type == PotionEffectType.SLOW
                                    || type == PotionEffectType.JUMP || type == PotionEffectType.FAST_DIGGING || type == PotionEffectType.SLOW_DIGGING) {
                                if (type == PotionEffectType.INCREASE_DAMAGE)
                                    realEffect = "Strength";
                                else if (type == PotionEffectType.DAMAGE_RESISTANCE)
                                    realEffect = "Resistance";
                                else if (type == PotionEffectType.SLOW)
                                    realEffect = "Slowness";
                                else if (type == PotionEffectType.JUMP)
                                    realEffect = "Jump Boost";
                                else if (type == PotionEffectType.FAST_DIGGING)
                                    realEffect = "Haste";
                                else
                                    realEffect = "Mining Fatigue";
                            }

                            if (amplifier > 0) realEffect = realEffect + " " + (amplifier + 1);

                            if (i == mob.getEffects().size() - 1) {
                                effectString.append(realEffect);
                            } else
                                effectString.append(realEffect).append(", ");
                        } add("   §r§e" + effectString);

                        if (mob.getSpecial() != null) {
                            add("");
                            add("§9Unique Abilities:");
                            addAll(mob.getSpecial());
                        }

                        ArrayList<String> mobLore = mob.getLore(null);
                        mobLore.remove(0);
                        addAll(mobLore);
                    }}));

            if (slotIndex == 16 || slotIndex == 25) {
                slotIndex += 2;
            }

            slotIndex++;
        }

        /*inventory.setItem(35, createGuiItem(Material.BOOK, "§c💗 How Do Lives Work 💗", new ArrayList<>() {{
            add(HexUtils.format("#A5133B----------------------------------------------"));
            add("§fEvery Player has §e3 lives §fwhen they spawn into the world.");
            add("§fIf you §elose all of your lives§f, you will get §cbanned §efor §c1 §fday.");
            add("");
            add("§fYou only §elose a life §fif a §ePlayer §fkills you.");
            add("§fDying to something like a §eZombie §fwill not make you lose a life.");
            add("");
            add("§fYou do not §egain lives §fby killing other §ePlayers§f, so try");
            add("§fnot to risk any of your own trying to get more.");
            add("");
            add("§fYou can check how many lives you have with §e/lives§f.");
            add(HexUtils.format("#A5133B----------------------------------------------"));
        }}));*/

        int index = 0;

        for (ItemStack slot : inventory.getContents()) {
            if (slot == null || slot.getType() == Material.AIR) {
                if (slot == null) inventory.setItem(index, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
                else slot.setType(Material.GRAY_STAINED_GLASS_PANE);
            }

            index += 1;
        }
    }

    protected static ItemStack createGuiItem(final Material material, final String name, ArrayList<String> lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(lore);

        if (name.contains("Level")) {
            meta.setCustomModelData(15);
        } else if (name.contains("Lives")) {
            meta.setCustomModelData(16);
        }

        item.setItemMeta(meta);

        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getInventory().equals(inventory)) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return;
        final Player p = (Player) e.getWhoClicked();
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent e) {
        if (e.getInventory().equals(inventory)) {
            e.setCancelled(true);
        }
    }
}
