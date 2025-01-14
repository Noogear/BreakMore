package cn.unUnbreakable.Utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

public class MaterialUtil {


    public static Material fromSting(String type){
        try{
            NamespacedKey key = NamespacedKey.fromString(type.toLowerCase());
            if(key != null){
                return Registry.MATERIAL.get(key);
            } else {
                return Material.valueOf(type.toUpperCase());
            }
        } catch(Exception e){
            XLogger.err("%s is not a valid material", type);
            return null;
        }

    }
}
