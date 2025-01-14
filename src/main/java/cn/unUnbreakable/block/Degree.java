package cn.unUnbreakable.block;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Degree {

    private final int start;
    private final int end;
    private final int[] fixed;

    public Degree(int start, int end, int[] fixed) {
        this.start = start;
        this.end = end;
        this.fixed = fixed;
    }

    public static Degree build(String value) {
        int start = 0;
        int end = 0;
        int[] fixed = new int[0];
        if (value.contains("-")) {
            String[] range = value.replace(" ", "").split("-");
            if (range.length >= 2) {
                start = Integer.parseInt(range[0]);
                end = Integer.parseInt(range[range.length - 1]);
            }
        } else {
            fixed = Arrays.stream(value.replace(" ", "").split(",")).mapToInt(Integer::parseInt).toArray();
        }
        return new Degree(start, end, fixed);
    }

    public int getRandom() {
        if (fixed.length == 0) {
            return ThreadLocalRandom.current().nextInt(start, end);
        }
        return fixed[ThreadLocalRandom.current().nextInt(fixed.length)];
    }
}
