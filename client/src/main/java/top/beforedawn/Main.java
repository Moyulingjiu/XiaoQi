package top.beforedawn;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import top.beforedawn.config.BotConfig;
import top.beforedawn.models.bo.SimpleBlacklist;
import top.beforedawn.plugins.BaseFunction;
import top.beforedawn.plugins.BasePlugin;
import top.beforedawn.plugins.BotFunction;
import top.beforedawn.util.MyBot;
import top.beforedawn.util.SingleEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Main {
    private static final ArrayList<BasePlugin> plugins = new ArrayList<>();

    private static void registerPlugins() {
        plugins.add(new BotFunction());
        plugins.add(new BaseFunction());
    }

    private static void inBlacklist(SingleEvent singleEvent) {
        SimpleBlacklist temp = singleEvent.getConfig().getBlacklistGlobalUser(singleEvent.getSenderId());
        if (temp != null && temp.needRemind()) {
            singleEvent.send(
                    String.format("你目前在全局黑名单中【原因：%s】禁止使用所有小柒系列机器人\n如有疑问前往官方群（%d）询问\n每天仅提醒一次", temp.getComment(), singleEvent.getConfig().getOfficialGroup())
            );
        }
        temp = singleEvent.getConfig().getBlacklistGlobalGroup(singleEvent.getGroupId());
        if (temp != null && temp.needRemind()) {
            singleEvent.send(
                    String.format("本群目前在全局黑名单中【原因：%s】禁止使用所有小柒系列机器人\n如有疑问前往官方群（%d）询问，或者更换群使用\n每天仅提醒一次", temp.getComment(), singleEvent.getConfig().getOfficialGroup())
            );
        }
        temp = singleEvent.getConfig().getBlacklistUser(singleEvent.getSenderId());
        if (temp != null && temp.needRemind()) {
            singleEvent.send(
                    String.format("你目前在黑名单中【原因：%s】\n如有疑问前往官方群（%d）询问\n每天仅提醒一次", temp.getComment(), singleEvent.getConfig().getOfficialGroup())
            );
        }
        temp = singleEvent.getConfig().getBlacklistGroup(singleEvent.getGroupId());
        if (temp != null && temp.needRemind()) {
            singleEvent.send(
                    String.format("本群目前在黑名单中【原因：%s】\n如有疑问前往官方群（%d）询问，或者更换群使用\n每天仅提醒一次", temp.getComment(), singleEvent.getConfig().getOfficialGroup())
            );
        }
    }

    private static void awaken(SingleEvent singleEvent) {
        // 如果不在黑名单中就可以被唤醒，如果在黑名单中，就执行黑名单提醒
        if (!singleEvent.getConfig().isBlacklist(singleEvent.getSenderId(), singleEvent.getGroupId())) {
            singleEvent.send("我在~");
        } else {
            inBlacklist(singleEvent);
        }
    }

    private static void checkAwaken(SingleEvent singleEvent) {
        if (singleEvent.getMessage().getPlain().size() == 0 && singleEvent.getMessage().getAt().size() == 1) {
            for (Long aLong : singleEvent.getMessage().getAt()) {
                if (aLong.equals(singleEvent.getBotId())) {
                    awaken(singleEvent);
                }
            }
        } else if (singleEvent.getMessage().getAt().size() == 0 && singleEvent.getMessage().plainEqual(singleEvent.getBotName())) {
            awaken(singleEvent);
        }
    }

    private static boolean globalSwitcher(SingleEvent singleEvent) {
        // 全局开关
        if (singleEvent.isGroupMessage() && singleEvent.getMessage().plainBeAtEqual("退群")) {
            if (singleEvent.aboveGroupAdmin()) {
                singleEvent.send("再见啦~" + singleEvent.getBotName() + "会想你们的~");
                singleEvent.quit();
                if (singleEvent.getConfig().getBotSwitcher().isRemindQuit()) {
                    singleEvent.sendMaster(String.format(
                            "[%s]退出群<%s>(%d)",
                            LocalDateTime.now(),
                            singleEvent.getGroupMessageEvent().getGroup().getName(),
                            singleEvent.getGroupId()
                    ));
                }
                return true;
            } else {
                singleEvent.send("你无权执行该操作");
            }
        }
        if (singleEvent.isGroupMessage() && singleEvent.getMessage().plainBeAtEqual("禁言")) {
            if (!singleEvent.getConfig().getBotSwitcher().getMuteGroup().contains(singleEvent.getGroupId())) {
                if (singleEvent.aboveGroupAdmin()) {
                    singleEvent.send("那" + singleEvent.getBotName() + "还是闭嘴吧");
                    singleEvent.getConfig().getBotSwitcher().getMuteGroup().add(singleEvent.getGroupId());
                    if (singleEvent.getConfig().getBotSwitcher().isRemindMute()) {
                        singleEvent.sendMaster(String.format(
                                "[%s]在群<%s>(%d)禁言",
                                LocalDateTime.now(),
                                singleEvent.getGroupMessageEvent().getGroup().getName(),
                                singleEvent.getGroupId()
                        ));
                    }
                    return true;
                } else {
                    singleEvent.send("你无权执行该操作");
                }
            }
        }
        if (singleEvent.isGroupMessage() && singleEvent.getMessage().plainBeAtEqual("解除禁言")) {
            if (singleEvent.aboveGroupAdmin()) {
                if (singleEvent.getConfig().getBotSwitcher().getMuteGroup().remove(singleEvent.getGroupId())) {
                    singleEvent.send("呜呜呜，憋死我了，终于可以说话了");
                    if (singleEvent.getConfig().getBotSwitcher().isRemindMute()) {
                        singleEvent.sendMaster(String.format(
                                "[%s]在群<%s>(%d)解除禁言",
                                LocalDateTime.now(),
                                singleEvent.getGroupMessageEvent().getGroup().getName(),
                                singleEvent.getGroupId()
                        ));
                    }
                    return true;
                } else {
                    singleEvent.send(singleEvent.getBotName() + "好像没有禁言呢");
                }
            } else {
                singleEvent.send("你无权执行该操作");
            }
        }
        return false;
    }

    public static void handle(SingleEvent singleEvent) {
        if (
                !singleEvent.valid() ||
                        globalSwitcher(singleEvent) ||
                        singleEvent.getConfig().isMute(singleEvent.getSenderId(), singleEvent.getGroupId())
        ) {
            return;
        }
        checkAwaken(singleEvent);
        // 如果是黑名单的话那么到此为止了
        if (singleEvent.getConfig().isBlacklist(singleEvent.getSenderId(), singleEvent.getGroupId())) {
            return;
        }
        // 限制模式判断
        if (singleEvent.getConfig().getBotSwitcher().getLimit().contains(singleEvent.getGroupId())) {
            return;
        }

        // 所有插件进行轮询
        for (BasePlugin plugin : plugins) {
            plugin.handle(singleEvent);
        }
    }

    public static void main(String[] args) {
        // 统一配置路径
        String workdir = "C:/mirai";
        Long botId = 1812322920L;

        Bot bot = MyBot.getBot(new BotConfig(workdir));
        registerPlugins();

        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
            handle(new SingleEvent(event));
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, event -> {
            handle(new SingleEvent(event));
        });
    }
}
