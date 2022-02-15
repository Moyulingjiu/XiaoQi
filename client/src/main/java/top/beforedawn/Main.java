package top.beforedawn;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import top.beforedawn.config.BotConfig;
import top.beforedawn.models.bo.BotRemoteInformation;
import top.beforedawn.models.bo.SimpleBlacklist;
import top.beforedawn.plugins.BaseFunction;
import top.beforedawn.plugins.BasePlugin;
import top.beforedawn.util.CommonUtil;
import top.beforedawn.util.FunctionUtil;
import top.beforedawn.util.MyBot;
import top.beforedawn.util.SingleEvent;

import java.util.ArrayList;

public class Main {
    private static final ArrayList<BasePlugin> plugins = new ArrayList<>();

    private static void registerPlugins() {
        plugins.add(new BaseFunction());
    }

    private static boolean inBlacklist(SingleEvent singleEvent) {
        SimpleBlacklist key1 = singleEvent.getConfig().getMemberBlacklist().getByKey(singleEvent.getSenderId());
        SimpleBlacklist key2 = singleEvent.getConfig().getGroupBlacklist().getByKey(singleEvent.getGroupId());
        if (key1 != null) {
            if (CommonUtil.needRemind(key1)) {
                singleEvent.send(
                        String.format("你目前在黑名单中【原因：%s】\n如有疑问前往官方群（%d）询问\n每天仅提醒一次", key1.getComment(), singleEvent.getConfig().getOfficialGroup())
                );
            }
        } else if (key2 != null) {
            if (CommonUtil.needRemind(key2)) {
                singleEvent.send(
                        String.format("本群目前在黑名单中【原因：%s】\n如有疑问前往官方群（%d）询问，或者更换群使用\n每天仅提醒一次", key2.getComment(), singleEvent.getConfig().getOfficialGroup())
                );
            }
        }
        return key1 != null || key2 != null;
    }

    private static boolean awaken(SingleEvent singleEvent) {
        boolean isBlacklist = inBlacklist(singleEvent);
        if (!isBlacklist) {
            singleEvent.send("我在~");
        }
        return isBlacklist;
    }

    private static boolean checkAwaken(SingleEvent singleEvent) {
        boolean isBlacklist = false;
        if (singleEvent.getMessage().getPlain().size() == 0 && singleEvent.getMessage().getAt().size() == 1) {
            for (Long aLong : singleEvent.getMessage().getAt()) {
                if (aLong.equals(singleEvent.getBotId())) {
                    isBlacklist = awaken(singleEvent);
                    break;
                }
            }
        } else if (singleEvent.getMessage().getAt().size() == 0 && singleEvent.getMessage().plainEqual(singleEvent.getBotName())) {
            isBlacklist = awaken(singleEvent);
        }
        return isBlacklist;
    }

    private static void checkRight(SingleEvent singleEvent) {
        if (singleEvent.getMessage().plainEqual("我的权限")) {
            switch (singleEvent.getConfig().checkRight(singleEvent.getSenderId())) {
                case SYSTEM_SUPER_ADMIN:
                    singleEvent.send("你的权限：系统超级管理员");
                    break;
                case SYSTEM_ADMIN:
                    singleEvent.send("你的权限：系统管理员");
                    break;
                case MASTER:
                    singleEvent.send("你的权限：机器人主人");
                    break;
                case ADMIN:
                    singleEvent.send("你的权限：机器人管理员");
                    break;
                case MEMBER:
                    singleEvent.send("你的权限：普通成员");
                    break;
            }
        }
    }

    private static void checkAuthorization(SingleEvent singleEvent) {
        if (singleEvent.getMessage().plainEqual("机器人信息")) {
            BotRemoteInformation bot = FunctionUtil.getBot(singleEvent);
            if (bot != null) {
                StringBuilder builder = new StringBuilder();
                builder.append("机器人id：").append(bot.getQq()).append("\n");
                builder.append("名字：").append(bot.getName()).append("\n");
                builder.append("主人：").append(bot.getMasterQq()).append("\n");
                builder.append("激活码：").append(bot.getKeyValue()).append("\n");
                if (bot.getKeyType().equals("NORMAL")) {
                    builder
                            .append("有效期：")
                            .append(CommonUtil.LocalDateTime2String(bot.getKeyValidBeginDate()))
                            .append("—")
                            .append(CommonUtil.LocalDateTime2String(bot.getKeyValidEndDate()));
                } else {
                    builder.append("激活码有效期：无限期");
                }

                singleEvent.send(builder.toString());
            }
        }
    }

    public static void handle(SingleEvent singleEvent) {
        if (!singleEvent.valid()) {
            return;
        }
        if (checkAwaken(singleEvent)) {
            return;
        }
        checkRight(singleEvent);
        checkAuthorization(singleEvent);

        // 所有插件进行轮询
        for (BasePlugin plugin : plugins) {
            plugin.handle(singleEvent);
        }
    }

    public static void main(String[] args) {
        // 统一配置路径
        String workdir = "C:/mirai";

        Bot bot = MyBot.getBot(new BotConfig(workdir));
        registerPlugins();

        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
            handle(new SingleEvent(event));
            /*
            event.getSubject().sendMessage("收到消息" + chain);
            event.getSubject().sendMessage("来自群：" + event.getSubject().getId() + "\n群名：" + event.getSubject().getName());
            event.getSubject().sendMessage("来自成员：" + event.getSender().getId() + "\n群名片：" + event.getSender().getNameCard());
            event.getSubject().sendMessage("发送时间：" + event.getTime());
            event.getSubject().sendMessage("权限：" + event.getPermission());
            event.getSubject().sendMessage("source：" + event.getSource());
             */

        });

        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, event -> {
            handle(new SingleEvent(event));
        });
    }
}
