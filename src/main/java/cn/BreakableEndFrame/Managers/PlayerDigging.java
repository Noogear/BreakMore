package cn.BreakableEndFrame.Managers;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockBreakAnimation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class PlayerDigging {

    private static PlayerDigging instance;
    private final Map<Location, BukkitTask> locations;

    public PlayerDigging() {
        instance = this;
        locations = new HashMap<>();
    }

    public static void sendBreakAnimation(Player player, Vector3i block, byte stage) {
        WrapperPlayServerBlockBreakAnimation animation = new WrapperPlayServerBlockBreakAnimation(
                block.hashCode(),
                block,
                stage);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player,animation);
    }

    public static void add(Location location, BukkitTask task) {
        instance.locations.put(location, task);
    }

    public static void cancel(Location location) {
        if(instance.locations.containsKey(location)) {
            instance.locations.get(location).cancel();
        }
    }

    public static void remove(Location location) {
        instance.locations.remove(location);
    }


    public static boolean isBreaking(Location location) {
        return instance.locations.containsKey(location);
    }

}
