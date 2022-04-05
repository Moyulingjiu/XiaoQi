package top.beforedawn.util;

import org.junit.Test;
import top.beforedawn.config.BackgroundTask;
import top.beforedawn.models.timed.DailyChecker;
import top.beforedawn.models.timed.GroupTimedMessage;

import java.time.LocalDateTime;

public class BackgroundTest {
    /**
     * 该测试可以证明a和b等价，尽管他们的时间并不相同
     */
    @Test
    public void test1() {
        GroupTimedMessage a = new GroupTimedMessage();
        a.setGroupId(1L);
        a.setName("测试");
        a.setChecker(new DailyChecker(12, 0));
        a.setLastTime(LocalDateTime.now());
        GroupTimedMessage b = new GroupTimedMessage();
        b.setGroupId(1L);
        b.setName("测试");
        b.setChecker(new DailyChecker(12, 0));
        GroupTimedMessage c = new GroupTimedMessage();
        c.setGroupId(2L);
        c.setName("测试");
        c.setChecker(new DailyChecker(12, 0));
        BackgroundTask.getInstance().put(a);
        System.out.println(BackgroundTask.getInstance().size());
        BackgroundTask.getInstance().put(b);
        System.out.println(BackgroundTask.getInstance().size());
        BackgroundTask.getInstance().put(c);
        System.out.println(BackgroundTask.getInstance().size());
        System.out.println(a.hashCode());
        System.out.println(b.hashCode());
        System.out.println(c.hashCode());
        System.out.println(a.equals(b));
        System.out.println(a.equals(c));
    }
}
