package cn.unUnbreakable.Managers;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class PlayerDigging {

    private static final Map<Location, BukkitTask> locations = new HashMap<>();

    public static void add(Location location, BukkitTask task) {
        locations.put(location, task);
    }

    public static void cancel(Location location) {
            locations.get(location).cancel();
    }

    public static void remove(Location location) {
        locations.remove(location);
    }

    public static boolean isBreaking(Location location) {
        return locations.containsKey(location);
    }

}
