package cn.breakMore;

import cn.breakMore.Listeners.BlockBreakPacketListener;
import cn.breakMore.Managers.BlockManager;
import cn.breakMore.Managers.PlayerDigging;
import cn.breakMore.Utils.XLogger;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private BlockManager blockManager;

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
        XLogger.info("加载中");
        // Plugin startup logic
        PacketEvents.getAPI().init();
        saveDefaultConfig();
        reloadConfig();
        blockManager = new BlockManager(this);
        new PlayerDigging();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        PacketEvents.getAPI().terminate();
    }


    public void load(){

    }

    public BlockManager getBlockManager(){
        return blockManager;
    }
}
