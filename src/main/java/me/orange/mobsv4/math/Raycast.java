package me.orange.mobsv4.math;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Raycast {
    public static ArrayList<LivingEntity> getTargetEntities(Player player)
    {
        ArrayList<LivingEntity> targets = new ArrayList<>();
        Location playerPos = player.getEyeLocation();
        Vector3D playerDir = new Vector3D(playerPos.getDirection());
        Vector3D playerStart = new Vector3D(playerPos);
        Vector3D playerEnd = playerStart.add(playerDir.multiply(50));

        for(LivingEntity entity : player.getWorld().getNearbyLivingEntities(player.getLocation(), 50))
        {
            Vector3D targetPos = new Vector3D(entity.getLocation());
            Vector3D minimum = targetPos.add(-0.5, 0, -0.5);
            Vector3D maximum = targetPos.add(0.5, 1.67, 0.5);

            if(entity != player && hasIntersection(playerStart, playerEnd, minimum, maximum))
            {
                targets.add(entity);
            }
        }

        return targets;
    }

    public static Player getTargetPlayer(Player player)
    {
        Player target = null;
        Location playerPos = player.getEyeLocation();
        Vector3D playerDir = new Vector3D(playerPos.getDirection());
        Vector3D playerStart = new Vector3D(playerPos);
        Vector3D playerEnd = playerStart.add(playerDir.multiply(50));

        for(Player p : player.getWorld().getPlayers())
        {
            Vector3D targetPos = new Vector3D(p.getLocation());
            Vector3D minimum = targetPos.add(-0.5, 0, -0.5);
            Vector3D maximum = targetPos.add(0.5, 1.67, 0.5);

            if(p != player && hasIntersection(playerStart, playerEnd, minimum, maximum))
            {
                if(target == null || target.getLocation().distanceSquared(playerPos) > p.getLocation().distanceSquared(playerPos))
                {
                    target = p;
                }
            }
        }

        return target;
    }

    public static boolean hasIntersection(Vector3D p1, Vector3D p2, Vector3D min, Vector3D max)
    {
        final double epsilon = 0.0001f;
        Vector3D d = p2.subtract(p1).multiply(0.5);
        Vector3D e = max.subtract(min).multiply(0.5);
        Vector3D c = p1.add(d).subtract(min.add(max).multiply(0.5));
        Vector3D ad = d.abs();

        if(Math.abs(c.x) > e.x + ad.x) return false;
        if(Math.abs(c.y) > e.y + ad.y) return false;
        if(Math.abs(c.z) > e.z + ad.z) return false;

        if(Math.abs(d.y * c.z - d.z * c.y) > e.y * ad.z + e.z * ad.y + epsilon) return false;
        if(Math.abs(d.z * c.x - d.x * c.z) > e.z * ad.x + e.x * ad.z + epsilon) return false;
        if(Math.abs(d.x * c.y - d.y * c.x) > e.x * ad.y + e.y * ad.x + epsilon) return false;

        return true;
    }
}