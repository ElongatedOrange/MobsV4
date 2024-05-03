package me.orange.mobsv3.ui;

import me.orange.mobsv3.MobManager;
import me.orange.mobsv3.MobsV3;
import me.orange.mobsv3.mobs.BaseMob;
import me.orange.mobsv3.mobs.Cooldowns;
import me.orange.mobsv3.mobs.classes.DragonMob;
import me.orange.mobsv3.mobs.classes.ResetMob;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class MobSelectTitle {
    public static void pickRandomMob(Player p) {
        BaseMob alrMob = new ResetMob();

        for (BaseMob mob : MobManager.mobs) {
            if (p.getScoreboardTags().contains(mob)) {
                alrMob = mob;
            }
        }

        BaseMob finalAlrMob = alrMob;

        new BukkitRunnable() {
            int loops = 0;
            BaseMob lastMob = null;
            final Object curMob = finalAlrMob;

            @Override
            public void run() {
                BaseMob mobSelected = MobManager.mobs.get(new Random().nextInt(MobManager.mobs.size() - 1) + 1);

                while (mobSelected == lastMob
                        || mobSelected instanceof DragonMob
                        || mobSelected instanceof ResetMob
                        || mobSelected == curMob)
                    mobSelected = MobManager.mobs.get(new Random().nextInt(MobManager.mobs.size() - 1) + 1);

                lastMob = mobSelected;

                if (loops <= 19) {
                    p.sendTitle(ChatColor.YELLOW + mobSelected.getName(), "", 0, 4, 0);
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    loops++;
                } else {
                    p.sendTitle(ChatColor.GREEN + mobSelected.getName(), "", 0, 40, 20);
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                    p.playSound(p, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);

                    MobsV3.setMob(p, new ResetMob());
                    MobsV3.setMob(p, mobSelected);

                    p.setPlayerListName(mobSelected.getPrefix() + "[" + mobSelected.getName() + "] " + p.getName());
                    p.setDisplayName(mobSelected.getPrefix() + "[" + mobSelected.getName() + "] " + p.getName());
                    Cooldowns.startActionBarUpdateTask(p, mobSelected.getName());

                    cancel();
                }
            }
        }.runTaskTimer(MobsV3.MOBS, 0, 3);
    }
}