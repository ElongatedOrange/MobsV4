package me.orange.mobsv3.mobs;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public abstract class BaseMob {
    public abstract String getName();

    public abstract String getPrefix();

    public abstract String getPrimaryEmoji();

    public abstract String getAltEmoji();

    public abstract String getAlt();

    public abstract int getHealth();

    public abstract ArrayList<String> getLore(Player player);

    public abstract ArrayList<String> getSpecial();

    public abstract ArrayList<ArrayList<Object>> getEffects();

    public abstract void perform(Player player) throws ReflectiveOperationException;

    public abstract Boolean performAlt(Player player);
}