package cn.unUnbreakable.block;

import cn.unUnbreakable.Utils.BlockUtils;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public class UnbreakableBlock {

    private final Map<Material, Integer> breakTime;
    private final Map<Material, Degree> drops;

    public UnbreakableBlock(Map<Material, Integer> breakTime, Map<Material, Degree> drops) {
        this.breakTime = breakTime;
        this.drops = drops;
    }

    public static UnbreakableBlock build(List<String> breakTime, List<String> drops) {
        return new UnbreakableBlock(BlockUtils.buildBreakTime(breakTime), BlockUtils.buildDrops(drops));

    }

    public int getBreakTime(Material material) {
        if (breakTime.containsKey(material)) {
            return breakTime.get(material);
        }
        return -1;
    }

    public Map<Material, Degree> getDrops() {
        return drops;
    }
}
