package top.beforedawn.util;

import org.jetbrains.annotations.NotNull;
import top.beforedawn.config.BotConfig;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志类
 *
 * @author 墨羽翎玖
 */
public class MyLogger {
    private static final SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static void _log(String line, BotConfig config) {
        System.out.print(line);
        if (config == null) {
            // todo: 系统日志
        } else {
            // todo: 机器人日志
        }
    }


    /**
     * 打印日志
     *
     * @param obj 日志产生的类
     * @param contains 日志内容
     */
    public static void log(@NotNull Object obj, @NotNull String contains) {
        String line = String.format("<%s> [%s@%s] system: %s\n",
                matter.format(new Date()),
                obj.getClass().getName(),
                obj.hashCode(),
                contains
        );
        _log(line, null);
    }

    /**
     * 打印日志
     *
     * @param obj 日志产生的类
     * @param contains 日志内容
     * @param config 配置类
     */
    public static void log(@NotNull Object obj, @NotNull String contains, @NotNull BotConfig config) {
        log(obj.getClass().getName(), obj.hashCode(), contains, config);
    }

    /**
     * 打印日志
     *
     * @param className 类名
     * @param hashCode hash码
     * @param contains 内容
     * @param config 配置类
     */
    public static void log(@NotNull String className, int hashCode, @NotNull String contains, @NotNull BotConfig config) {
        String line = String.format("<%s> [%s@%s] %s(%d): %s\n",
                matter.format(new Date()),
                className,
                hashCode,
                config.getName(),
                config.getQq(),
                contains
        );
        _log(line, config);
    }

    /**
     * 打印日志
     *
     * @param className 类名
     * @param contains 内容
     * @param config 配置类
     */
    public static void log(@NotNull String className, @NotNull String contains, @NotNull BotConfig config) {
        String line = String.format("<%s> [%s] %s(%d): %s\n",
                matter.format(new Date()),
                className,
                config.getName(),
                config.getQq(),
                contains
        );
        _log(line, config);
    }
}
