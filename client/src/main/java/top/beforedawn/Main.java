package top.beforedawn;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.AnonymousMember;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.data.*;
import top.beforedawn.config.*;
import top.beforedawn.models.bo.MyGroup;
import top.beforedawn.models.bo.MyMessage;
import top.beforedawn.models.bo.SimpleBlacklist;
import top.beforedawn.plugins.*;
import top.beforedawn.util.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Main {
    private static final ArrayList<BasePlugin> plugins = new ArrayList<>();

    /**
     * 注册插件
     */
    private static void registerPlugins() {
        plugins.add(new BotFunction());
        plugins.add(new BaseFunction());

        plugins.add(new CocFunction());

        plugins.add(new RepeatFunction());
        plugins.add(new AutoReplyFunction());
        plugins.add(new NudgeFunction());
        plugins.add(new UnlockFlashFunction());
        plugins.add(new DriftingBottleFunction());
        plugins.add(new RpgFunction());
        plugins.add(new TalkFunction());
        plugins.add(new SelfReplyFunction());
    }

    /**
     * 成员监控的事件注册
     */
    private static void memberWatcher() {
        // 踢出群成员
        GlobalEventChannel.INSTANCE.subscribeAlways(MemberLeaveEvent.Kick.class, event -> {
            MyGroup group = GroupPool.get(new SingleEvent(event.getBot().getId(), event.getMember().getId(), event.getGroupId()));
            if (group.isMemberWatcher()) {
                StringBuilder builder = new StringBuilder();
                if (event.getOperator() != null) {
                    if (event.getOperator().getNameCard().equals("")) {
                        builder.append(event.getOperator().getNick());
                    } else {
                        builder.append(event.getOperator().getNameCard());
                    }
                    builder.append("（").append(event.getOperator().getId()).append("）");
                } else {
                    builder.append("管理员/群主");
                }
                builder.append("踢出成员").append(event.getMember().getNick()).append("（").append(event.getMember().getId()).append("）");
                event.getGroup().sendMessage(builder.toString());
            }
        });

        // 群成员主动退出
        GlobalEventChannel.INSTANCE.subscribeAlways(MemberLeaveEvent.Quit.class, event -> {
            MyGroup group = GroupPool.get(new SingleEvent(event.getBot().getId(), event.getMember().getId(), event.getGroupId()));
            if (group.isMemberWatcher()) {
                event.getGroup().sendMessage(String.format("我们此时此刻失去了一位成员%s（%d）",
                        event.getMember().getNick(),
                        event.getMember().getId())
                );
            }
        });

        // 群成员的名片改变
        GlobalEventChannel.INSTANCE.subscribeAlways(MemberCardChangeEvent.class, event -> {
            MyGroup group = GroupPool.get(new SingleEvent(event.getBot().getId(), event.getMember().getId(), event.getGroupId()));
            if (group.isMemberWatcher()) {
                String origin = event.getOrigin().equals("") ? event.getMember().getNick() + "（QQ昵称）" : event.getOrigin();
                String newName = event.getNew().equals("") ? event.getMember().getNick() + "（QQ昵称）" : event.getNew();
                event.getGroup().sendMessage(String.format("有一成员（%d）修改了群名片\n" +
                                "原始名字：%s\n" +
                                "新名字：%s\n",
                        event.getMember().getId(),
                        origin,
                        newName)
                );
            }
        });

        // 群成员的头衔改变
        GlobalEventChannel.INSTANCE.subscribeAlways(MemberSpecialTitleChangeEvent.class, event -> {
            MyGroup group = GroupPool.get(new SingleEvent(event.getBot().getId(), event.getMember().getId(), event.getGroupId()));
            if (group.isMemberWatcher()) {
                event.getGroup().sendMessage(String.format("群主授予成员%s（%d）新头衔\n" +
                                "原始头衔：%s\n" +
                                "新头衔：%s\n",
                        event.getMember().getNameCard(),
                        event.getMember().getId(),
                        event.getOrigin(),
                        event.getNew())
                );
            }
        });

        // 群成员的权限改变
        GlobalEventChannel.INSTANCE.subscribeAlways(MemberPermissionChangeEvent.class, event -> {
            MyGroup group = GroupPool.get(new SingleEvent(event.getBot().getId(), event.getMember().getId(), event.getGroupId()));
            if (group.isMemberWatcher()) {
                event.getGroup().sendMessage(String.format("成员%s（%d）权限变动\n" +
                                "原始权限：%s\n" +
                                "新权限：%s\n",
                        event.getMember().getNameCard(),
                        event.getMember().getId(),
                        event.getOrigin(),
                        event.getNew())
                );
            }
        });

        // 群成员申请入群
        GlobalEventChannel.INSTANCE.subscribeAlways(MemberJoinRequestEvent.class, event -> {
            MyGroup group = GroupPool.get(new SingleEvent(event.getBot().getId(), event.getFromId(), event.getGroupId()));
            if (group.isMemberWatcher()) {
                StringBuilder builder = new StringBuilder();
                builder.append(String.format("有新的群申请：%s（%d）\n",
                        event.getFromNick(),
                        event.getFromId()));
                if (event.getInvitor() != null) {
                    builder.append("邀请人：");
                    if (event.getInvitor().getNameCard().equals("")) {
                        builder.append(event.getInvitor().getNick());
                    } else {
                        builder.append(event.getInvitor().getNameCard());
                    }
                    builder.append("（").append(event.getInvitorId()).append("）\n");
                }
                builder.append("申请信息：\n").append(event.getMessage());
                Group eventGroup = event.getGroup();
                if (eventGroup != null) {
                    eventGroup.sendMessage(builder.toString());
                }
            }
        });

        // 新成员进群
        GlobalEventChannel.INSTANCE.subscribeAlways(MemberJoinEvent.class, event -> {
            SingleEvent singleEvent = new SingleEvent(event);
            MyGroup group = GroupPool.get(singleEvent);
            if (group.isWelcome()) {
                MessageChainBuilder messageChain = CommonUtil.getMessageChain(singleEvent, group.getWelcomeMessage());
                messageChain.add(0, new At(event.getMember().getId()));
                singleEvent.send(messageChain.asMessageChain());
            }
        });
    }

    /**
     * 机器人监控的事件注册
     */
    private static void botWatcher() {
        // 新的好友申请
        GlobalEventChannel.INSTANCE.subscribeAlways(NewFriendRequestEvent.class, event -> {
            SingleEvent singleEvent = new SingleEvent(event.getBot().getId(), event.getFromId());
            SimpleCombineBot bot = MyBot.getSimpleCombineBot(event.getBot().getId(), singleEvent);
            if (bot.getConfig().getBotSwitcher().isRemindFriend()) {
                singleEvent.setCombineBot(bot);
                StringBuilder builder = new StringBuilder();
                builder.append("<好友事件").append(event.getEventId()).append(">");
                builder.append("有新的好友申请：").append(event.getFromNick()).append("（").append(event.getFromId()).append("）\n");
                builder.append("申请消息：").append(event.getMessage()).append("\n");
                if (event.getFromGroup() != null) {
                    builder.append("来自群：").append(event.getFromGroup().getName()).append("（").append(event.getFromGroupId()).append("）");
                }
                singleEvent.sendMaster(builder.toString());
            }
            if (bot.getConfig().getBotSwitcher().isAllowFriend()) {
                event.accept();
            } else {
                RequestEventPool.put(event);
            }
        });

        // 新的群邀请
        GlobalEventChannel.INSTANCE.subscribeAlways(BotInvitedJoinGroupRequestEvent.class, event -> {
            SingleEvent singleEvent = new SingleEvent(event.getBot().getId(), event.getInvitorId());
            SimpleCombineBot bot = MyBot.getSimpleCombineBot(event.getBot().getId(), singleEvent);
            if (bot.getConfig().getBotSwitcher().isRemindGroup()) {
                singleEvent.setCombineBot(bot);
                singleEvent.sendMaster(String.format("<群事件%d>有新的群申请：%s（%d）\n" +
                                "邀请人：%s（%d）",
                        event.getEventId(),
                        event.getGroupName(),
                        event.getGroupId(),
                        event.getInvitorNick(),
                        event.getInvitorId()));
            }
            if (bot.getConfig().getBotSwitcher().isAllowGroup()) {
                event.accept();
                if (event.getInvitor() != null)
                    event.getInvitor().sendMessage("已同意请求");
            } else {
                RequestEventPool.put(event);
                if (event.getInvitor() != null)
                    event.getInvitor().sendMessage("当前设置不会自动机群，请前往官方群（" + bot.getConfig().getOfficialGroup() + "）寻找主人");
            }
        });

        // 成功添加了一个新的好友
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendAddEvent.class, event -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                event.getFriend().sendMessage("已添加好友");
                event.getFriend().sendMessage(HttpUtil.convention(event.getBot().getId()));
            }
        });

        // 成功进入一个群
        GlobalEventChannel.INSTANCE.subscribeAlways(BotJoinGroupEvent.class, event -> {
            SingleEvent singleEvent = new SingleEvent(event.getBot().getId(), 0L);
            SimpleCombineBot bot = MyBot.getSimpleCombineBot(event.getBot().getId(), singleEvent);
            event.getGroup().sendMessage("已加入群，若是对机器人疑问可以直接艾特我然后输入退群两个字，我会自动退出的。例如：@" + bot.getConfig().getName() + "退群");
        });

        // 被踢出一个群
        GlobalEventChannel.INSTANCE.subscribeAlways(BotLeaveEvent.Kick.class, event -> {
            SingleEvent singleEvent = new SingleEvent(event.getBot().getId(), 0L);
            SimpleCombineBot bot = MyBot.getSimpleCombineBot(event.getBot().getId(), singleEvent);
            if (bot.getConfig().getBotSwitcher().isRemindQuit()) {
                singleEvent.sendMaster(String.format(
                        "[%s]被踢出群<%s>(%d)，操作人：%s（%d）",
                        LocalDateTime.now(),
                        event.getGroup().getName(),
                        event.getGroupId(),
                        event.getOperator().getNick(),
                        event.getOperator().getId()
                ));
            }
        });

        // 被禁言
        GlobalEventChannel.INSTANCE.subscribeAlways(BotMuteEvent.class, event -> {
            SingleEvent singleEvent = new SingleEvent(event.getBot().getId(), 0L);
            SimpleCombineBot bot = MyBot.getSimpleCombineBot(event.getBot().getId(), singleEvent);
            if (bot.getConfig().getBotSwitcher().isRemindMute()) {
                singleEvent.sendMaster(String.format(
                        "[%s]在群<%s>(%d)通过QQ被禁言，操作人：%s（%d）",
                        LocalDateTime.now(),
                        event.getGroup().getName(),
                        event.getGroupId(),
                        event.getOperator().getNick(),
                        event.getOperator().getId()
                ));
            }
        });

        // 解除禁言
        GlobalEventChannel.INSTANCE.subscribeAlways(BotUnmuteEvent.class, event -> {
            SingleEvent singleEvent = new SingleEvent(event.getBot().getId(), 0L);
            SimpleCombineBot bot = MyBot.getSimpleCombineBot(event.getBot().getId(), singleEvent);
            if (bot.getConfig().getBotSwitcher().isRemindMute()) {
                singleEvent.sendMaster(String.format(
                        "[%s]在群<%s>(%d)通过QQ被解除禁言，操作人：%s（%d）",
                        LocalDateTime.now(),
                        event.getGroup().getName(),
                        event.getGroupId(),
                        event.getOperator().getNick(),
                        event.getOperator().getId()
                ));
            }
        });

        // 群消息撤回
        GlobalEventChannel.INSTANCE.subscribeAlways(MessageRecallEvent.GroupRecall.class, event -> {
            MyGroup group = GroupPool.get(new SingleEvent(event.getBot().getId(), event.getAuthorId(), event.getGroup().getId()));
            if (group.isRecallGuard()) {
                // 不是机器人撤回，并且还是自己撤回的，那么就进行防撤回处理
                if (event.getOperator() != null && event.getOperator().getId() == event.getAuthorId()) {
                    int[] ids = event.getMessageIds();
                    if (ids.length != 0) {
                        LocalDateTime time = MessagePool.getTime(ids[0]);
                        MessageChain chain = MessagePool.get(ids[0]);
                        if (time == null || chain == null) return;
                        String authorName = event.getAuthor().getNick();
                        String message = "成员<" + authorName + ">（" + event.getAuthorId() + "）试图撤回" +
                                CommonUtil.LocalDateTime2String(time) + "的消息\n" +
                                "--------\n";
                        MessageChainBuilder builder = new MessageChainBuilder();
                        builder.add(new PlainText(message));
                        builder.addAll(chain);
                        event.getGroup().sendMessage(builder.asMessageChain());
                    }
                }
            }
        });
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
        singleEvent.getConfig().save();
    }

    private static void awaken(SingleEvent singleEvent) {
        // 如果不在黑名单中就可以被唤醒，如果在黑名单中，就执行黑名单提醒
        if (!singleEvent.getConfig().isBlacklist(singleEvent.getSenderId(), singleEvent.getGroupId())) {
            LocalDateTime now = LocalDateTime.now();
            if (now.getHour() == 13 && now.getMinute() == 14) {
                singleEvent.send(CommonUtil.randomString(new ArrayList<>(Arrays.asList(
                        "13:14真是个特别的时间",
                        "13点14分这是不是你们人类说的一生一世"
                ))));
            } else if (now.getHour() == now.getMinute()) {
                singleEvent.send(CommonUtil.randomString(new ArrayList<>(List.of(
                        "悄悄告诉你，这一条消息发送的时候，小时与分钟是相等的呢"
                ))));
            } else if (now.getHour() < 6) {
                singleEvent.send(CommonUtil.randomString(new ArrayList<>(Arrays.asList(
                        "好困~",
                        "困死了，大坏蛋就知道打扰" + singleEvent.getBotName() + "睡觉",
                        "呜呜~能不能明天再找" + singleEvent.getBotName(),
                        "还不睡觉，坏孩子",
                        "该睡觉啦！（双手叉腰.jpg）",
                        singleEvent.getBotName() + "讨厌你，打扰人睡觉",
                        singleEvent.getBotName() + "正在呼呼大睡"
                ))));
            } else if (now.getHour() < 9) {
                singleEvent.send(CommonUtil.randomString(new ArrayList<>(Arrays.asList(
                        "早上好哦~" + singleEvent.getBotName() + "一直都在",
                        "我在~",
                        "你也这么早起来陪" + singleEvent.getBotName() + "了吗",
                        "又是元气满满的一天呢",
                        singleEvent.getBotName() + "会陪着你每个早晨的",
                        "大懒猪才起床，",
                        singleEvent.getBotName() + "会陪着你每个早晨的"
                ))));
            } else if (now.getHour() >= 11 && now.getHour() < 14) {
                singleEvent.send(CommonUtil.randomString(new ArrayList<>(Arrays.asList(
                        "中午好~",
                        "中午好呀",
                        "是不是该去吃午饭了呢",
                        singleEvent.getBotName() + "一直都在陪着你呢",
                        "今天中午吃点什么好呢",
                        "我在~",
                        "该午睡咯"
                ))));
            } else if (now.getHour() >= 17 && now.getHour() < 20) {
                singleEvent.send(CommonUtil.randomString(new ArrayList<>(Arrays.asList(
                        "晚上好~",
                        "晚上好哦~",
                        "是不是该去吃晚饭了呢",
                        singleEvent.getBotName() + "一直都在陪着你呢",
                        "今天晚上吃点什么好呢",
                        "我在~",
                        "吃得好饱鸭~"
                ))));
            } else if (now.getHour() > 22) {
                singleEvent.send(CommonUtil.randomString(new ArrayList<>(Arrays.asList(
                        "好困~",
                        "晚上好哦~",
                        "晚上好哦",
                        "晚上好",
                        singleEvent.getBotName() + "一直都在陪着你呢",
                        "都已经这么晚了呢",
                        singleEvent.getBotName() + "困了",
                        "该睡觉觉啦~",
                        "你要睡觉了吗"
                ))));
            } else {
                singleEvent.send(CommonUtil.randomString(new ArrayList<>(Arrays.asList(
                        "我在~",
                        "我在！",
                        "我在(*^▽^*)",
                        "我在∑(っ°Д°;)っ",
                        "我在ヾ(*>∀＜*)(ﾉ∀｀●)⊃",
                        "我在(*´ﾟ∀ﾟ｀)ﾉ ",
                        "我在ヾ(@^▽^@)ノ",
                        "我在(◕ᴗ◕✿)",
                        "我在(｡◕ˇ∀ˇ◕)",
                        "我在✧*｡٩(ˊᗜˋ*)و✧*｡",
                        "我在(=´▽｀)ゞ",
                        "我在ヾ(*>∀＜*)",
                        "‧★,:*:‧\\(￣▽￣)/‧:*‧°★*",
                        "我在",
                        "我一直都在呢",
                        singleEvent.getBotName() + "一直都在陪着你呢",
                        "我会陪着你的",
                        singleEvent.getBotName() + "勉为其难地理你一下"
                ))));
            }
        } else {
            inBlacklist(singleEvent);
        }
    }

    private static void checkAwaken(SingleEvent singleEvent) {
        if (singleEvent.getMessage().getPlainString().equals("") && singleEvent.getMessage().isBeAt()) {
            awaken(singleEvent);
        } else if (singleEvent.getMessage().getAt().size() == 0 && singleEvent.getMessage().plainEqual(singleEvent.getBotName())) {
            awaken(singleEvent);
        }
    }

    private static boolean globalSwitcher(SingleEvent singleEvent) {
        if (!singleEvent.isGroupMessage()) {
            return false;
        }
        // 全局开关
        if (singleEvent.isGroupMessage() && singleEvent.getMessage().plainBeAtEqual("退群")) {
            if (singleEvent.aboveGroupAdmin()) {
                if (singleEvent.getGroupId() != singleEvent.getConfig().getOfficialGroup()) {
                    singleEvent.send("再见啦~" + singleEvent.getBotName() + "会想你们的~");
                    singleEvent.quit();
                    if (singleEvent.getConfig().getBotSwitcher().isRemindQuit()) {
                        singleEvent.sendMaster(String.format(
                                "[%s]退出群<%s>(%d)，操作人：%s（%d）",
                                LocalDateTime.now(),
                                singleEvent.getGroupMessageEvent().getGroup().getName(),
                                singleEvent.getGroupId(),
                                singleEvent.getGroupMessageEvent().getSenderName(),
                                singleEvent.getSenderId()
                        ));
                    }
                    return true;
                } else {
                    singleEvent.send("当前群聊为官方群，无法退出");
                }
            } else {
                singleEvent.send("你无权执行该操作");
            }
        } else if (singleEvent.isGroupMessage() && singleEvent.getMessage().plainBeAtEqual("休眠")) {
            MyGroup group = GroupPool.get(singleEvent);
            if (singleEvent.aboveGroupAdmin()) {
                if (!group.isMute()) {
                    singleEvent.send("那" + singleEvent.getBotName() + "还是闭嘴吧");
                    group.setMute(true);
                    GroupPool.save(singleEvent);
                    if (singleEvent.getConfig().getBotSwitcher().isRemindMute()) {
                        singleEvent.sendMaster(String.format(
                                "[%s]在群<%s>(%d)禁言，操作人：%s（%d）",
                                LocalDateTime.now(),
                                singleEvent.getGroupMessageEvent().getGroup().getName(),
                                singleEvent.getGroupId(),
                                singleEvent.getGroupMessageEvent().getSenderName(),
                                singleEvent.getSenderId()
                        ));
                    }
                    return true;
                } else {
                    singleEvent.send(singleEvent.getBotName() + "正在休眠呢");
                }
            } else {
                singleEvent.send("你无权执行该操作");
            }
        } else if (singleEvent.isGroupMessage() && singleEvent.getMessage().plainBeAtEqual("解除休眠")) {
            if (singleEvent.aboveGroupAdmin()) {
                MyGroup group = GroupPool.get(singleEvent);
                if (group.isMute()) {
                    singleEvent.send("呜呜呜，憋死我了，终于可以说话了");
                    group.setMute(false);
                    GroupPool.save(singleEvent);
                    if (singleEvent.getConfig().getBotSwitcher().isRemindMute()) {
                        singleEvent.sendMaster(String.format(
                                "[%s]在群<%s>(%d)解除禁言，操作人：%s（%d）",
                                LocalDateTime.now(),
                                singleEvent.getGroupMessageEvent().getGroup().getName(),
                                singleEvent.getGroupId(),
                                singleEvent.getGroupMessageEvent().getSenderName(),
                                singleEvent.getSenderId()
                        ));
                    }
                    return true;
                } else {
                    singleEvent.send(singleEvent.getBotName() + "好像没有休眠呢");
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
                        MessageStatistics.getRemindTime(singleEvent.getSenderId()) ||
                        globalSwitcher(singleEvent) ||
                        singleEvent.getConfig().isMute(singleEvent)
        ) {
            return;
        }
        checkAwaken(singleEvent);
        // 如果是黑名单的话那么到此为止了
        if (singleEvent.getConfig().isBlacklist(singleEvent.getSenderId(), singleEvent.getGroupId())) {
            return;
        }
        // 限制模式判断（如果没有被艾特，并且还开启了限制模式就不会往下传播消息了）
        if (!singleEvent.getMessage().isBeAt() && singleEvent.getConfig().isLimit(singleEvent)) {
            return;
        }

        // 所有插件进行轮询
        for (BasePlugin plugin : plugins) {
            plugin.handle(singleEvent);
        }
    }

    public static void main(String[] args) {
        // 统一配置路径
        // todo: 这两个参数应该由args获取而非写死
//        String workdir = "C:/mirai";
//        Long botId = 477768027L;
        String workdir = "/home/project/xiaoqi/data";
        Long botId = 2034794240L;

        System.out.println("当前系统环境");
        System.out.println(System.getProperty("os.name").toLowerCase(Locale.US));
        System.out.println(System.getProperty("os.arch").toLowerCase(Locale.US));
        System.out.println(System.getProperty("os.version").toLowerCase(Locale.US));
        System.out.println("======");

        Bot bot = MyBot.getBot(new BotConfig(workdir, botId));
        registerPlugins();

        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
            // 缓存消息到缓存池。并且同步清空过期的缓存
            OnlineMessageSource.Incoming.FromGroup source = event.getSource();
            int[] ids = source.getIds();
            if (ids.length != 0) {
                MessagePool.put(ids[0], event.getMessage());
            }
            // 匿名用户不响应
            if (event.getSender() instanceof AnonymousMember) {
                return;
            }
            SingleEvent singleEvent = new SingleEvent(event);
            MyGroup group = GroupPool.get(singleEvent);
            String message = singleEvent.getMessage().getPlainString();
            boolean permission = true;
            for (String muteWord : group.getMuteWords()) {
                if (message.contains(muteWord) && !message.startsWith("删除屏蔽词")) {
                    MemberPermission botPermission = event.getGroup().getBotPermission();
                    if (botPermission == MemberPermission.MEMBER) {
                        singleEvent.send("发现屏蔽词“" + muteWord + "”但" + singleEvent.getBotName() + "无权撤回");
                    } else if (botPermission == MemberPermission.ADMINISTRATOR && event.getPermission() != MemberPermission.MEMBER) {
                        singleEvent.send("发现屏蔽词“" + muteWord + "”但对方是管理员/群主" + singleEvent.getBotName() + "无权撤回");
                    } else {
                        MessageSource.recall(event.getMessage());
                        singleEvent.send("发现屏蔽词“" + muteWord + "”予以撤回");
                    }
                    permission = false;
                    break;
                }
            }
            // 指令隧穿（不支持连续隧穿）
            for (String key : group.getTunnel().keySet()) {
                String string = singleEvent.getMessage().getPlainString();
                if (string.startsWith(key)) {
                    MyMessage myMessage = new MyMessage(singleEvent.getMessage());
                    myMessage.setPlainString((group.getTunnel().get(key) + string.substring(key.length())).trim());
                    singleEvent.setMessage(myMessage);
                    break;
                }
            }
            if (permission)
                handle(singleEvent);
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, event -> handle(new SingleEvent(event)));

        GlobalEventChannel.INSTANCE.subscribeAlways(NudgeEvent.class, event -> {
            if (event.getTarget().getId() != event.getBot().getId()) return;
            handle(new SingleEvent(event));
        });

        botWatcher();
        memberWatcher();
    }
}
