package me.orange.mobsv4.mobs;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public abstract class BaseMob {
    public abstract String getName();

    public abstract String getPrefix();

    public abstract String getPrimaryEmoji();

    public abstract boolean hasAltAbility();

    public abstract String getAltEmoji();

    public abstract String getAlt();

    public abstract boolean hasAlt2Ability();

    public abstract String getAlt2Emoji();

    public abstract String getAlt2();

    public abstract int getHealth();

    public abstract ArrayList<String> getLore(Player player);

    public abstract ArrayList<String> getSpecial();

    public abstract ArrayList<ArrayList<Object>> getEffects();

    public abstract void perform(Player player) throws ReflectiveOperationException;

    public abstract Boolean performAlt(Player player);

    public abstract Boolean performAlt2(Player player);

}