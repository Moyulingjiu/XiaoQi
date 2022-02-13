package top.beforedawn;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import top.beforedawn.config.BotConfig;
import top.beforedawn.models.bo.MyMessage;
import top.beforedawn.models.bo.SimpleBlacklist;
import top.beforedawn.plugins.BaseFunction;
import top.beforedawn.plugins.BasePlugin;
import top.beforedawn.util.CommonUtil;
import top.beforedawn.util.MyBot;
import top.beforedawn.util.SimpleCombineBot;

import java.util.ArrayList;

public class Main {
    private static final ArrayList<BasePlugin> plugins = new ArrayList<>();

    private static void registerPlugins() {
        plugins.add(new BaseFunction());
    }

    private static boolean inBlacklist(Long qq, Long groupId, SimpleCombineBot combineBot, GroupMessageEvent event) {
        SimpleBlacklist key1 = combineBot.getConfig().getMemberBlacklist().getByKey(qq);
        SimpleBlacklist key2 = combineBot.getConfig().getGroupBlacklist().getByKey(groupId);
        if (key1 != null) {
            if (CommonUtil.needRemind(key1)) {
                event.getSubject().sendMessage(
                        String.format("你目前在黑名单中【原因：%s】\n如有疑问前往官方群（%d）询问\n每天仅提醒一次", key1.getComment(), combineBot.getConfig().getOfficialGroup())
                );
            }
        } else if (key2 != null) {
            if (CommonUtil.needRemind(key2)) {
                event.getSubject().sendMessage(
                        String.format("本群目前在黑名单中【原因：%s】\n如有疑问前往官方群（%d）询问，或者更换群使用\n每天仅提醒一次", key2.getComment(), combineBot.getConfig().getOfficialGroup())
                );
            }
        }
        return key1 != null || key2 != null;
    }

    private static boolean awaken(Long qq, Long groupId, SimpleCombineBot combineBot, GroupMessageEvent event) {
        boolean isBlacklist = inBlacklist(qq, groupId, combineBot, event);
        if (!isBlacklist) {
            event.getSubject().sendMessage("我在~");
        }
        return isBlacklist;
    }

    private static boolean checkAwaken(MyMessage message, SimpleCombineBot combineBot, GroupMessageEvent event) {
        boolean isBlacklist = false;
        if (message.getPlain().size() == 0 && message.getAt().size() == 1) {
            for (Long aLong : message.getAt()) {
                if (aLong.equals(event.getBot().getId())) {
                    isBlacklist = awaken(event.getSender().getId(), event.getSubject().getId(), MyBot.getSimpleCombineBot(event.getBot().getId()), event);
                    break;
                }
            }
        } else if (message.getAt().size() == 0 && message.plainEqual(combineBot.getConfig().getName())) {
            isBlacklist = awaken(event.getSender().getId(), event.getSubject().getId(), MyBot.getSimpleCombineBot(event.getBot().getId()), event);
        }
        return isBlacklist;
    }

    private static void checkRight(MyMessage message, SimpleCombineBot combineBot, GroupMessageEvent event) {
        if (message.plainEqual("我的权限")) {
            switch (combineBot.getConfig().checkRight(event.getSender().getId())) {
                case SYSTEM_SUPER_ADMIN:
                    event.getSubject().sendMessage("你的权限：系统超级管理员");
                    break;
                case SYSTEM_ADMIN:
                    event.getSubject().sendMessage("你的权限：系统管理员");
                    break;
                case MASTER:
                    event.getSubject().sendMessage("你的权限：机器人主人");
                    break;
                case ADMIN:
                    event.getSubject().sendMessage("你的权限：机器人管理员");
                    break;
                case MEMBER:
                    event.getSubject().sendMessage("你的权限：普通成员");
                    break;
            }
        }
    }

    public static void main(String[] args) {
        // 统一配置路径
        String workdir = "C:/mirai";

        Bot bot = MyBot.getBot(new BotConfig(workdir));
        registerPlugins();

        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
            SimpleCombineBot combineBot = MyBot.getSimpleCombineBot(event.getBot().getId());
            if (combineBot == null) {
                return;
            }
            MyMessage myMessage = CommonUtil.analysisMessage(event.getMessage(), event.getBot().getId());
            if (checkAwaken(myMessage, combineBot, event)) {
                return;
            }
            checkRight(myMessage, combineBot, event);

            // 所有插件进行轮询
            for (BasePlugin plugin : plugins) {
                plugin.handle(myMessage, combineBot, event);
            }

            System.out.println("收到的文本信息：" + myMessage.getPlain());
            System.out.println("收到的at消息：" + myMessage.getAt());

//            event.getSubject().sendMessage("收到消息" + chain);
//            event.getSubject().sendMessage("来自群：" + event.getSubject().getId() + "\n群名：" + event.getSubject().getName());
//            event.getSubject().sendMessage("来自成员：" + event.getSender().getId() + "\n群名片：" + event.getSender().getNameCard());
//            event.getSubject().sendMessage("发送时间：" + event.getTime());
//            event.getSubject().sendMessage("权限：" + event.getPermission());
//            event.getSubject().sendMessage("source：" + event.getSource());
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, event -> {
            SimpleCombineBot combineBot = MyBot.getSimpleCombineBot(event.getBot().getId());
            if (combineBot == null) {
                return;
            }
            MyMessage myMessage = CommonUtil.analysisMessage(event.getMessage(), event.getBot().getId());
            event.getFriend().sendMessage("你好世界！");
        });
    }
}
