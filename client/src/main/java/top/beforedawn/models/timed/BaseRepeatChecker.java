package top.beforedawn.models.timed;

import java.time.LocalDateTime;

/**
 * 检查是否到达重复周期
 *
 * @author 墨羽翎玖
 */
public abstract class BaseRepeatChecker {
    /**
     * 是否有效
     *
     * @return 是否有效
     */
    public abstract boolean valid();

    /**
     * 获取需要检查的时间
     *
     * @return 时间
     */
    public abstract TaskTime taskTime();

    /**
     * 检查是否到达重复周期
     *
     * @param lastTime 上次执行时间
     * @param now  当前时间
     * @return 是否到达重复周期
     */
    public abstract boolean isRepeat(LocalDateTime lastTime, LocalDateTime now);
}
