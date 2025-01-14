package cn.BreakableEndFrame.Utils;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class EventUtils {

    public static boolean callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
        if (event instanceof Cancellable cancellable) {
            return !cancellable.isCancelled();
        } else {
            return true;
        }
    }
}
