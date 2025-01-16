package cn.unUnbreakable.Managers;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockBreakAnimation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class PlayerDigging {

    private static final Map<Location, BukkitTask> locations = new HashMap<>();

    public static void sendBreakAnimation(Player player, int userID, Vector3i block, byte stage) {
        WrapperPlayServerBlockBreakAnimation animation = new WrapperPlayServerBlockBreakAnimation(
                userID,
                block,
                stage);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, animation);
    }

    public static void add(Location location, BukkitTask task) {
        locations.put(location, task);
    }

    public static void cancel(Location location) {
        if (locations.containsKey(location)) {
            locations.get(location).cancel();
        }
    }

    public static void remove(Location location) {
        locations.remove(location);
    }

    public static boolean isBreaking(Location location) {
        return locations.containsKey(location);
    }

}
