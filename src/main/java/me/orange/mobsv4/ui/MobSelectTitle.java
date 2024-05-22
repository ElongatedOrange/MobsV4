package me.orange.mobsv4.ui;

import me.orange.mobsv4.MobManager;
import me.orange.mobsv4.MobsV4;
import me.orange.mobsv4.mobs.BaseMob;
import me.orange.mobsv4.mobs.Cooldowns;
import me.orange.mobsv4.mobs.classes.DragonMob;
import me.orange.mobsv4.mobs.classes.ResetMob;
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

                    MobsV4.setMob(p, new ResetMob());
                    MobsV4.setMob(p, mobSelected);

                    Cooldowns.startActionBarUpdateTask(p, mobSelected.getName());

                    cancel();
                }
            }
        }.runTaskTimer(MobsV4.MOBS, 0, 3);
    }
}