package cn.unUnbreakable.block;

import cn.unUnbreakable.Utils.BlockUtils;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public class EndPortalFrame extends UnbreakableBlock {

    private final boolean breakFrame;

    public EndPortalFrame(Map<Material, Integer> breakTime, Map<Material, Degree> drops, boolean breakFrame) {
        super(breakTime,drops);
        this.breakFrame = breakFrame;
    }

    public static EndPortalFrame build(List<String> breakTime, List<String> drops, boolean breakFrame) {
        return new EndPortalFrame(BlockUtils.buildBreakTime(breakTime), BlockUtils.buildDrops(drops), breakFrame);

    }

    public boolean isBreakFrame() {
        return breakFrame;
    }


}
