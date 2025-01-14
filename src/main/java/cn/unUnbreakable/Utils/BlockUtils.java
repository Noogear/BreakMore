package cn.unUnbreakable.Utils;

import cn.unUnbreakable.block.Degree;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockUtils {


    public static Map<Material, Integer> buildBreakTime(List<String> breakTime){
        Map<Material, Integer> bt = new HashMap<>();
        for (String entry : breakTime) {
            String[] parts = entry.split(":");
            if (parts.length == 2) {
                if(MaterialUtil.fromSting(parts[0].trim()) == null){
                    continue;
                }
                bt.put(MaterialUtil.fromSting(parts[0].trim()), Integer.parseInt(parts[1].trim()));
            }
        }
        return bt;
    }

    public static Map<Material, Degree> buildDrops(List<String> drops){
        Map<Material, Degree> ds = new HashMap<>();
        for(String entry : drops) {
            String[] parts = entry.split(":");
            if(parts.length == 2) {
                if(MaterialUtil.fromSting(parts[0].trim()) == null){
                    continue;
                }
                ds.put(MaterialUtil.fromSting(parts[0].trim()), Degree.build(parts[1].trim()));
            }
        }
        return ds;

    }





}
