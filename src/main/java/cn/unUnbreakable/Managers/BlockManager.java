package cn.unUnbreakable.Managers;

import cn.unUnbreakable.Main;
import cn.unUnbreakable.Utils.MaterialUtil;
import cn.unUnbreakable.Utils.XLogger;
import cn.unUnbreakable.block.EndPortalFrame;
import cn.unUnbreakable.block.UnbreakableBlock;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;

public class BlockManager {
    private final Main plugin;
    private final Set<String> subKeys;
    private Map<Material, UnbreakableBlock> blocks;
    private final FileConfiguration config;

    public BlockManager(Main main) {
        this.plugin = main;
        config = plugin.getConfig();
        this.subKeys = new HashSet<>(Arrays.asList("break-time", "drops", "break-frame"));
        load();
    }

    public void load() {
        blocks = new HashMap<>();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        Set<String> topKeys = config.getKeys(false);
        boolean newConfig = false;

        for (String key : topKeys) {
            XLogger.info(key);
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null) continue;
            List<String> breakTime = section.getStringList("break-time");
            List<String> drops = section.getStringList("drops");
            Material material = MaterialUtil.fromSting(key);
            if (material == null || material.getHardness() >= 0) {
                XLogger.err("Invalid block type: " + material);
                continue;
            }

            boolean endFrame = false;
            if (material == Material.END_PORTAL_FRAME) {
                endFrame = true;
                blocks.put(material, EndPortalFrame.build(breakTime, drops, section.getBoolean("break-frame")));
            } else {
                blocks.put(material, UnbreakableBlock.build(breakTime, drops));
            }
            for (String subKey : section.getKeys(false)) {
                if (!subKeys.contains(subKey.toLowerCase())) {
                    newConfig = true;
                    section.set(subKey, null);
                    continue;
                }
                if ("break-frame".equalsIgnoreCase(subKey) && !endFrame) {
                    newConfig = true;
                    section.set(subKey, null);
                }
            }
        }
        if (newConfig) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timeStamp = sdf.format(new Date());
            File originalFile = new File(plugin.getDataFolder(), "config.yml");
            File backupFile = new File(plugin.getDataFolder(), "config_" + timeStamp + ".yml.bak");
            try {
                Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                XLogger.err("备份配置文件时发生错误: " + e.getMessage());
            }
            plugin.saveConfig();
        }
    }

    public @Nullable UnbreakableBlock getBlock(Material material) {
        return blocks.get(material);
    }

}
