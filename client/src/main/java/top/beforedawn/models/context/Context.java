package top.beforedawn.models.context;

/**
 * 上下文环境的基类
 *
 * @author 墨羽翎玖
 */
public class Context {
    /**
     * 步骤计数器
     */
    protected int step = 0;
    protected int maxStep = 0;

    public void next() {
        step++;
    }

    public int getStep() {
        return step;
    }

    public boolean isOver() {
        return step >= maxStep;
    }
}
