package cn.BreakableEndFrame;

import cn.BreakableEndFrame.Listeners.BlockBreakPacketListener;
import cn.BreakableEndFrame.Managers.ConfigManager;
import cn.BreakableEndFrame.Managers.PlayerDigging;
import cn.BreakableEndFrame.Utils.XLogger;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private ConfigManager configManager;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        //On Bukkit, calling this here is essential, hence the name "load"
        PacketEvents.getAPI().load();
        PacketEvents.getAPI().getEventManager().registerListener(new BlockBreakPacketListener(this));
    }

    @Override
    public void onEnable() {
        new XLogger(this);
        // Plugin startup logic
        PacketEvents.getAPI().init();
        saveDefaultConfig();
        reloadConfig();
        configManager = new ConfigManager(this);
        new PlayerDigging();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        PacketEvents.getAPI().terminate();
    }


    public void load(){

    }

    public ConfigManager getBlockManager(){
        return configManager;
    }
}
