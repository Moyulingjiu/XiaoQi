package top.beforedawn.util;

import org.junit.Test;
import top.beforedawn.config.BotConfig;

public class TestLogger {
    @Test
    public void test1() {
        MyLogger.log(this, "测试日志", new BotConfig("C:/mirai", 1L));
    }
}
