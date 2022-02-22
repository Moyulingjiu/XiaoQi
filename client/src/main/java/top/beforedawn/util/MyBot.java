package top.beforedawn.util;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import org.jetbrains.annotations.NotNull;
import top.beforedawn.config.BotConfig;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * 获取机器人
 *
 * @author 墨羽翎玖
 */
public class MyBot {
    private static final HashMap<Long, SimpleCombineBot> botPool = new HashMap<>(); // 机器人池

    /**
     * 获取QQ号对应的配置类
     *
     * @param qq qq号
     * @return 配置类 & 机器人 组合对象
     */
    public static SimpleCombineBot getSimpleCombineBot(@NotNull Long qq, SingleEvent singleEvent) {
        SimpleCombineBot simpleCombineBot = botPool.get(qq);
        if (Duration.between(simpleCombineBot.getConfig().getUpdateTime(), LocalDateTime.now()).toMinutes() > BotConfig.EXPIRATION_TIME) {
            simpleCombineBot.getConfig().update(singleEvent);
        }
        if (Duration.between(simpleCombineBot.getConfig().getSaveTime(), LocalDateTime.now()).toMinutes() > BotConfig.EXPIRATION_TIME) {
            simpleCombineBot.getConfig().save();
        }
        return simpleCombineBot;
    }

    /**
     * 获取机器人对象
     * 如果该机器人已经创建过实例那么就不必再创键
     *
     * @param config 机器人配置
     * @return 机器人实例
     */
    public static Bot getBot(BotConfig config) {
        Long qq = config.getQq();
        if (botPool.containsKey(qq)) {
            MyLogger.log(
                    "MyBot",
                    String.format("已经存在[%d]的实例机器人", config.getQq()),
                    config
            );
            return botPool.get(qq).getBot();
        }
        Bot bot = BotFactory.INSTANCE.newBot(config.getQq(), config.getPassword(), new BotConfiguration() {{
            setHeartbeatStrategy(BotConfiguration.HeartbeatStrategy.REGISTER);
            setWorkingDir(new File(config.getWorkdir()));
            setCacheDir(new File(config.getCache()));
        }});
        bot.login();
        botPool.put(qq, new SimpleCombineBot(config, bot));
        return bot;
    }
}
