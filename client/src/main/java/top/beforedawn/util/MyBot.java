package top.beforedawn.util;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import org.jetbrains.annotations.NotNull;
import top.beforedawn.config.BotConfig;
import top.beforedawn.config.GroupPool;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * <h1>获取机器人</h1>
 * <p><b>该类最初的设计是为了实现同时登陆多个机器人，后来的设计考虑摒弃了这一点，转而改为一个jar包对应一个机器人</b></p>
 *
 * @author 墨羽翎玖
 */
public class MyBot {
    private static final HashMap<Long, SimpleCombineBot> botPool = new HashMap<>(); // 机器人池

    /**
     * 遍历本地文件，注册背景任务
     */
    private static void loadLocalData(SimpleCombineBot bot) {
        SingleEvent singleEvent = new SingleEvent(bot.getConfig().getQq());
        singleEvent.setTitle("config");
        singleEvent.setCombineBot(bot);

        File directory = new File(bot.getConfig().getWorkdir() + "\\group");
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                System.out.println("加载" + file);
                if (file != null && file.isFile() && file.getName().endsWith(".json")) {
                    long groupId = CommonUtil.getLong(file.getName().substring(0, file.getName().length() - 5));
                    if (groupId > 0) {
                        // 加载群的时候会顺便注册任务
                        GroupPool.load(singleEvent, groupId);
                    }
                }
            }

        }
    }

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
        boolean flag = false;
        int times = 0;
        while (!flag) {
            times++;
            try {
                InitFileChecker.checkAndRepair(config.getWorkdir());
                flag = true;
            } catch (IOException e) {
                e.printStackTrace();
                if (times > 3) {
                    break;
                }
            }
        }
        Bot bot = BotFactory.INSTANCE.newBot(qq, config.getPassword(), new BotConfiguration() {{
            // 如果遇到 Bot 闲置一段时间后，发消息返回成功但群内收不到的情况，请切换心跳策略，依次尝试 STAT_HB、REGISTER 和 NONE。
            setHeartbeatStrategy(HeartbeatStrategy.STAT_HB);
            // 登录协议：ANDROID_PHONE，ANDROID_PAD，ANDROID_WATCH
            setProtocol(MiraiProtocol.ANDROID_PHONE);
            setWorkingDir(new File(config.getWorkdir()));
            setCacheDir(new File(config.getCache()));
            fileBasedDeviceInfo("device.json");
        }});
        bot.login();
        config.update(new SingleEvent(config.getQq(), config.getQq()));
        SimpleCombineBot combineBot = new SimpleCombineBot(config, bot);
        botPool.put(qq, combineBot);
        loadLocalData(combineBot);
        return bot;
    }
}
