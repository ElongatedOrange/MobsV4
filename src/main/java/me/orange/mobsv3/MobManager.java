package me.orange.mobsv3;

import me.orange.mobsv3.mobs.BaseMob;
import me.orange.mobsv3.mobs.classes.*;
import me.orange.mobsv3.ui.MobGuide;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MobManager implements CommandExecutor {
    private static MobsV3 plugin;

    public MobManager(MobsV3 plugin) {
        this.plugin = plugin;
    }
    public static ResetMob none = new ResetMob();
    public static TurtleMob turtle = new TurtleMob();
    public static WardenMob warden = new WardenMob();
    public static CreeperMob creeper = new CreeperMob(plugin);
    public static ChickenMob chicken = new ChickenMob();
    public static BlazeMob blaze = new BlazeMob();
    public static ShulkerMob shulker = new ShulkerMob();
    public static WitchMob witch = new WitchMob();
    public static SkeletonMob skeleton = new SkeletonMob();

    public static ArrayList<BaseMob> mobs = new ArrayList<>() {
        {
            add(none);
            add(chicken);
            add(turtle);
            add(creeper);
            add(witch);
            add(blaze);
            add(shulker);
            add(warden);
            add(skeleton);
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
            return true;
        }

        Player player = (Player) sender;

        // Only operators are allowed to execute the command
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (label.equalsIgnoreCase("mob")) {
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

            MobsV3.MOBS.scheduleTaskLater(() -> MobsV3.setMob(target, mob), 4);
            player.sendMessage("Mob " + mobName + " assigned to " + target.getName() + ".");
        } else if (label.equalsIgnoreCase("guide"))
            player.openInventory(MobGuide.inventory);

        return true;
    }

}
