package me.orange.mobsv4;

import me.orange.mobsv4.mobs.BaseMob;
import me.orange.mobsv4.mobs.classes.*;
import me.orange.mobsv4.ui.MobGuide;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

import static me.orange.mobsv4.mobs.Cooldowns.cooldownDisplayTasks;

public class MobManager implements CommandExecutor {
    private static MobsV4 plugin;

    public MobManager(MobsV4 plugin) {
        this.plugin = plugin;
    }
    public static ResetMob none = new ResetMob();
    public static TurtleMob turtle = new TurtleMob();
    public static WardenMob warden = new WardenMob();
    public static CreeperMob creeper = new CreeperMob(plugin);
    public static ChickenMob chicken = new ChickenMob();
    public static BlazeMob blaze = new BlazeMob();
    public static IronGolemMob ironGolem = new IronGolemMob();
    public static EvokerMob evoker = new EvokerMob();
    public static SkeletonMob skeleton = new SkeletonMob();
    public static DragonMob dragon = new DragonMob();

    public static ArrayList<BaseMob> mobs = new ArrayList<>() {
        {
            add(none);
            add(chicken);
            add(turtle);
            add(creeper);
            add(evoker);
            add(blaze);
            add(ironGolem);
            add(warden);
            add(skeleton);
            add(dragon);
        }
    };

    public static BaseMob findMobByName(String name) {
        return mobs.stream()
                .filter(mob -> mob.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        Player player = (Player) sender;

        // Handle the /mob command
        if (label.equalsIgnoreCase("mob")) {
            // Check if the player is an operator
            if (!player.isOp()) {
                player.sendMessage("You do not have permission to use this command.");
                return true;
            }

            if (args.length < 1) {
                player.sendMessage("Usage: /mob <mobName> [playerName]");
                return true;
            }

            String mobName = args[0];
            Player target; // Default to the command sender

            if (args.length == 2) {
                // Attempt to get the player specified in the arguments
                Player specifiedPlayer = Bukkit.getServer().getPlayer(args[1]);
                if (specifiedPlayer == null) {
                    player.sendMessage("Player " + args[1] + " not found.");
                    return true;
                }
                target = specifiedPlayer;
            } else {
                target = player;
            }

            // Find the mob by name
            BaseMob mob = MobManager.mobs.stream()
                    .filter(m -> m.getName().equalsIgnoreCase(mobName))
                    .findFirst()
                    .orElse(null);

            if (mob == null) {
                player.sendMessage("Mob " + mobName + " not found.");
                return true;
            }

            // Schedule the mob assignment
            MobsV4.MOBS.scheduleTaskLater(() -> MobsV4.setMob(target, mob), 4);
            clearActionBarTaskForPlayer(player);
            player.sendMessage("Mob " + mobName + " assigned to " + target.getName() + ".");
        }

        // Handle the /guide command
        else if (label.equalsIgnoreCase("guide")) {
            // This command is available to all players
            player.openInventory(MobGuide.inventory);
        }

        return true;
    }

    public static void clearActionBarTaskForPlayer(Player player) {
        BukkitTask task = cooldownDisplayTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

}

