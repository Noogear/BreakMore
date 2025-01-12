package cn.breakMore.Managers;

import cn.breakMore.Main;
import cn.breakMore.Utils.XLogger;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockManager {
    private final Main plugin;
    private Map<Material, Integer> blocks;

    public BlockManager(Main main) {
        this.plugin = main;
        this.blocks = new HashMap<>();
        load();
    }

    public void load() {
        List<String> blockList = plugin.getConfig().getStringList("block");
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
                blocks.put(Registry.MATERIAL.get(key), time * 20 / 10);
            }
        } catch (Exception e) {
            XLogger.warn(e.getMessage());
        }
    }

    public Integer get(Material material) {
        if (blocks.containsKey(material)) {
            return blocks.get(material);
        }
        return -1;
    }

}
