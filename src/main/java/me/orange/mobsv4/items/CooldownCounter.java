package me.orange.mobsv4.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class CooldownCounter {
    public ItemStack createCounter() {
        ItemStack counter = new ItemStack(Material.CLOCK);
        ItemMeta meta = counter.getItemMeta();

        meta.setDisplayName("§6Cooldown Counter");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Halves your Cooldown time"));
        meta.setCustomModelData(10);

        counter.setItemMeta(meta);
        return counter;
    }
}
