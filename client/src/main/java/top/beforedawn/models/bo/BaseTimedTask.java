package top.beforedawn.models.bo;

import net.mamoe.mirai.Bot;
import top.beforedawn.models.timed.TaskTime;

import java.time.LocalDateTime;

/**
 * 背景任务
 * <p>需要重写equals和hashCode方法</p>
 *
 * @author 墨羽翎玖
 */
public interface BaseTimedTask {

    /**
     * 获取时间
     *
     * @return 获取任务执行时间
     */
    TaskTime taskTime();

    /**
     * 执行任务
     * <p>该任务如果出现执行时间过长，请务必开启使用新线程去执行该任务。避免拥塞监控。</p>
     * <p>不过，小柒自带背景任务本身提供了线程池的解决策略，如无必要可以不自行实现</p>
     */
    void execute(Bot bot, LocalDateTime now);
}
