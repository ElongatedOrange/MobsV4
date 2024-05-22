package me.orange.mobsv4.mobs.classes;

import me.orange.mobsv4.mobs.BaseMob;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ResetMob extends BaseMob {
    @Override
    public String getName() {
        return "None";
    }

    @Override
    public String getPrefix() {
        return "§r";
    }

    @Override
    public String getPrimaryEmoji() {
        return null;
    }

    @Override
    public boolean hasAltAbility() {
        return false;
    }

    @Override
    public String getAltEmoji() {
        return null;
    }

    @Override
    public String getAlt() {
        return null;
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
        return 20;
    }

    @Override
    public ArrayList<String> getLore(Player token) {
        return null;
    }

    @Override
    public ArrayList<String> getSpecial() {
        return null;
    }

    @Override
    public ArrayList<ArrayList<Object>> getEffects() {
        return null;
    }

    @Override
    public void perform(Player player) {
    }

    @Override
    public Boolean performAlt(Player player) {
        return null;
    }

    @Override
    public Boolean performAlt2(Player player) {
        return null;
    }
}
