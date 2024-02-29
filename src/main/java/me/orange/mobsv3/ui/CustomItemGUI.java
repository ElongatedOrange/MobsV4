package me.orange.mobsv3.ui;

import me.orange.mobsv3.items.CooldownCounter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class CustomItemGUI implements Listener {
    private static final String GUI_TITLE = "Select Custom Mobs Items";
    private static final CooldownCounter cooldownCounter = new CooldownCounter();

    public static void openSelectionGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, GUI_TITLE);

        // Add custom items to the GUI
        gui.addItem(cooldownCounter.createCounter());

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(GUI_TITLE)) {
            return;
        }

        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        // Add the clicked item to the player's inventory
        Player player = (Player) event.getWhoClicked();
        player.getInventory().addItem(event.getCurrentItem());
    }
}
