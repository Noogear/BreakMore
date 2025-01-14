package cn.BreakableEndFrame.Managers;

import cn.BreakableEndFrame.Main;
import cn.BreakableEndFrame.Utils.XLogger;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private final Main plugin;
    private Map<Material, Integer> tools;

    public ConfigManager(Main main) {
        this.plugin = main;
        this.tools = new HashMap<>();
        load();
    }

    public void load() {
        List<String> blockList = plugin.getConfig().getStringList("break-time");
        for (String entry : blockList) {
            String[] parts = entry.split(":");
            if (parts.length == 2) {
                add(parts[0].trim(), Integer.parseInt(parts[1].trim()));
            }
        }
    }

    public void add(String type, Integer time) {
        try {
            NamespacedKey key = NamespacedKey.fromString(type.toLowerCase());
            if (key != null) {
                tools.put(Registry.MATERIAL.get(key), time * 20 / 10);
            }
        } catch (Exception e) {
            XLogger.warn(e.getMessage());
        }
    }

    public Integer get(Material material) {
        if (tools.containsKey(material)) {
            return tools.get(material);
        }
        return -1;
    }

}
