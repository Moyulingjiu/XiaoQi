package top.beforedawn.plugins;

import net.mamoe.mirai.message.data.MessageChainBuilder;
import top.beforedawn.config.BackgroundTask;
import top.beforedawn.config.ContextPool;
import top.beforedawn.config.GroupPool;
import top.beforedawn.models.bo.MessageLinearAnalysis;
import top.beforedawn.models.bo.MyGroup;
import top.beforedawn.models.context.*;
import top.beforedawn.models.reply.BaseAutoReply;
import top.beforedawn.models.reply.ComplexReply;
import top.beforedawn.models.reply.KeyMatchReply;
import top.beforedawn.models.reply.KeyReply;
import top.beforedawn.models.timed.*;
import top.beforedawn.util.CommonUtil;
import top.beforedawn.util.SingleEvent;

import java.util.ArrayList;

/**
 * 自动回复的插件
 */
public class AutoReplyFunction extends BasePlugin {
    private static final int MAX_KEY_REPLY = 20;
    private static final int MAX_KEY = 100;
    private static final int COMPLEX_REPLY_PAGE_SIZE = 20;
    private static final int PAGE_SIZE = 25;
    private static final int TIMED_MESSAGE_PAGE_SIZE = 20;

    public AutoReplyFunction() {
        pluginName = "auto_reply";
    }

    public void reply(SingleEvent singleEvent) {
        if (!singleEvent.isGroupMessage()) {
            return;
        }
        MyGroup group = GroupPool.get(singleEvent);
        if (!group.isAutoReply()) {
            return;
        }
        for (BaseAutoReply autoReply : group.getAutoReplies()) {
            if (autoReply.check(singleEvent.getMessage())) {
                singleEvent.send(autoReply.reply(singleEvent));
            }
        }
    }

    @Override
    public boolean handContextGroup(SingleEvent singleEvent) {
        Context context = ContextPool.get(singleEvent.getSenderId());
        // 添加复杂回复
        if (context instanceof ComplexReplyContext) {
            if (singleEvent.getMessage().plainEqual("*取消创建*")) {
                ContextPool.remove(singleEvent.getSenderId());
                singleEvent.send("已为您取消创建");
                return true;
            }
            Long groupId;
            MyGroup group;
            switch (context.getStep()) {
                case 0:
                    String message = singleEvent.getMessage().getPlainString().strip();

                    groupId = singleEvent.getGroupId();
                    singleEvent.setGroupId(((ComplexReplyContext) context).getGroupId());
                    group = GroupPool.get(singleEvent);
                    singleEvent.setGroupId(groupId);

                    for (BaseAutoReply autoReply : group.getAutoReplies())
                        if (autoReply instanceof ComplexReply)
                            if (((ComplexReply) autoReply).getKey().equals(message)) {
                                singleEvent.send("已经有触发词：" + message + "\n" +
                                        "将会自动覆盖复杂回复，你可以输入“*取消创建*”来取消覆盖");
                                break;
                            }

                    ((ComplexReplyContext) context).setKey(message);
                    singleEvent.send("触发词：" + ((ComplexReplyContext) context).getKey() + "\n" +
                            singleEvent.getBotName() + "已为您记录下来了，请问你的回复内容是什么？（可以文字+表情+图片，不可以包含艾特）");
                    break;
                case 1:
                    ((ComplexReplyContext) context).setReply(CommonUtil.getSerializeMessage(singleEvent.getConfig().getWorkdir() + "image/", singleEvent.getMessage().getOrigin()));
                    singleEvent.send(singleEvent.getBotName() + "记录下来了，请问这条消息需要艾特谁吗（全体成员/触发人/QQ号，这三种都是可以的哦~如果QQ号为0表示不艾特）？");
                    break;
                case 2:
                    ((ComplexReplyContext) context).setType(ComplexReplyContext.AtType.NONE);
                    if (singleEvent.getMessage().plainEqual("全体成员"))
                        ((ComplexReplyContext) context).setType(ComplexReplyContext.AtType.ALL);
                    else if (singleEvent.getMessage().plainEqual("触发人"))
                        ((ComplexReplyContext) context).setType(ComplexReplyContext.AtType.TRIGGER);
                    else if (!singleEvent.getMessage().plainEqual("0")) {
                        MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
                        ArrayList<Long> at = new ArrayList<>();
                        for (String s : analysis.split()) {
                            long temp = CommonUtil.getLong(s);
                            if (temp > 0L) at.add(temp);
                        }
                        at.addAll(singleEvent.getMessage().getAt());
                        if (at.size() > 0) {
                            ((ComplexReplyContext) context).setAt(at);
                            ((ComplexReplyContext) context).setType(ComplexReplyContext.AtType.NORMAL);
                        }
                    }

                    // 添加复杂回复
                    ComplexReply complexReply = new ComplexReply((ComplexReplyContext) context);
                    ContextPool.remove(singleEvent.getSenderId());
                    groupId = singleEvent.getGroupId();
                    singleEvent.setGroupId(((ComplexReplyContext) context).getGroupId());
                    group = GroupPool.get(singleEvent);

                    boolean flag = true;
                    for (BaseAutoReply autoReply : group.getAutoReplies())
                        if (autoReply instanceof ComplexReply)
                            if (((ComplexReply) autoReply).getKey().equals(complexReply.getKey())) {
                                // 删除原来的图片文件
                                if (!CommonUtil.removeImageFile(((ComplexReply) autoReply).getReply())) {
                                    singleEvent.sendMaster("在删除复杂回复的时候，遇见了一个未知错误，无法删除文件");
                                }
                                group.getAutoReplies().remove(autoReply);
                                group.add(complexReply);
                                singleEvent.send("覆盖成功！");
                                flag = false;
                                break;
                            }

                    if (flag) {
                        group.add(complexReply);
                        singleEvent.send("添加成功");
                    }
                    GroupPool.save(singleEvent);

                    singleEvent.setGroupId(groupId);
                    break;
            }
            context.next();
            return true;
        }
        // 复制群回复
        else if (context instanceof CopyGroupConfirmContext) {
            if (!CommonUtil.isConfirmMessage(singleEvent.getMessage().getPlainString())) {
                singleEvent.send("已取消复制");
                ContextPool.remove(singleEvent.getSenderId());
                return true;
            }
            Long groupId = singleEvent.getGroupId();
            // 获取两个群
            singleEvent.setGroupId(((CopyGroupConfirmContext) context).getOriginGroup());
            MyGroup origin = GroupPool.get(singleEvent);
            singleEvent.setGroupId(((CopyGroupConfirmContext) context).getGroupId());
            MyGroup target = GroupPool.get(singleEvent);
            singleEvent.setGroupId(groupId);

            origin.setAutoReplies(target.getAutoReplies());
            origin.setTunnel(target.getTunnel());
            origin.setMuteWords(target.getMuteWords());
            GroupPool.save(singleEvent);
            // 这里必须移除，让下次重新加载。避免一边修改同步影响到另一边。
            GroupPool.remove(singleEvent.getGroupId());

            singleEvent.send("已复制目标群的自定义回复、指令隧穿、屏蔽词");
            ContextPool.remove(singleEvent.getSenderId());
            return true;
        }
        // 清空复杂回复
        else if (context instanceof ComplexReplyClearContext) {
            ContextPool.remove(singleEvent.getSenderId());
            if (!CommonUtil.isConfirmMessage(singleEvent.getMessage().getPlainString())) {
                singleEvent.send("已取消清空");
                return true;
            }
            Long groupId = singleEvent.getGroupId();
            singleEvent.setGroupId(((ComplexReplyClearContext) context).getGroupId());
            MyGroup group = GroupPool.get(singleEvent);
            ArrayList<BaseAutoReply> newAutoReplies = new ArrayList<>();
            for (BaseAutoReply autoReply : group.getAutoReplies()) {
                if (!(autoReply instanceof ComplexReply)) {
                    newAutoReplies.add(autoReply);
                }
            }
            group.setAutoReplies(newAutoReplies);
            GroupPool.save(singleEvent);
            singleEvent.setGroupId(groupId);
            singleEvent.send("清空复杂回复成功！");
            return true;
        }
        // 清空回复
        else if (context instanceof KeyReplyClearContext) {
            ContextPool.remove(singleEvent.getSenderId());
            if (!CommonUtil.isConfirmMessage(singleEvent.getMessage().getPlainString())) {
                singleEvent.send("已取消清空");
                return true;
            }
            Long groupId = singleEvent.getGroupId();
            singleEvent.setGroupId(((KeyReplyClearContext) context).getGroupId());
            MyGroup group = GroupPool.get(singleEvent);
            ArrayList<BaseAutoReply> newAutoReplies = new ArrayList<>();
            for (BaseAutoReply autoReply : group.getAutoReplies()) {
                if (!(autoReply instanceof KeyReply)) {
                    newAutoReplies.add(autoReply);
                }
            }
            group.setAutoReplies(newAutoReplies);
            GroupPool.save(singleEvent);
            singleEvent.setGroupId(groupId);
            singleEvent.send("清空回复成功！");
            return true;
        }
        // 清空关键词回复
        else if (context instanceof KeyMatchReplyClearContext) {
            ContextPool.remove(singleEvent.getSenderId());
            if (!CommonUtil.isConfirmMessage(singleEvent.getMessage().getPlainString())) {
                singleEvent.send("已取消清空");
                return true;
            }
            Long groupId = singleEvent.getGroupId();
            singleEvent.setGroupId(((KeyMatchReplyClearContext) context).getGroupId());
            MyGroup group = GroupPool.get(singleEvent);
            ArrayList<BaseAutoReply> newAutoReplies = new ArrayList<>();
            for (BaseAutoReply autoReply : group.getAutoReplies()) {
                if (!(autoReply instanceof KeyMatchReply)) {
                    newAutoReplies.add(autoReply);
                }
            }
            group.setAutoReplies(newAutoReplies);
            GroupPool.save(singleEvent);
            singleEvent.setGroupId(groupId);
            singleEvent.send("清空关键词成功！");
            return true;
        }
        // 清空定时消息
        else if (context instanceof TimedMessageClearContext) {
            ContextPool.remove(singleEvent.getSenderId());
            if (!CommonUtil.isConfirmMessage(singleEvent.getMessage().getPlainString())) {
                singleEvent.send("已取消清空");
                return true;
            }
            Long groupId = singleEvent.getGroupId();
            singleEvent.setGroupId(((TimedMessageClearContext) context).getGroupId());
            MyGroup group = GroupPool.get(singleEvent);
            // 取消注册的事件
            for (GroupTimedMessage timedMessage : group.getTimedMessages()) {
                BackgroundTask.getInstance().remove(timedMessage);
            }
            group.setTimedMessages(new ArrayList<>());
            GroupPool.save(singleEvent, ((TimedMessageClearContext) context).getGroupId());
            singleEvent.setGroupId(groupId);
            singleEvent.send("清空定时消息成功！");
            return true;
        }
        // 添加定时消息
        else if (context instanceof TimedMessageContext) {
            if (singleEvent.getMessage().plainEqual("*取消创建*")) {
                ContextPool.remove(singleEvent.getSenderId());
                singleEvent.send("已取消创建");
                return true;
            }
            Long groupId = singleEvent.getGroupId();
            singleEvent.setGroupId(((TimedMessageContext) context).getGroupId());
            MyGroup group = GroupPool.get(singleEvent);
            singleEvent.setGroupId(groupId);

            switch (context.getStep()) {
                case 0:
                    String name = singleEvent.getMessage().getPlainString();
                    if (group.timedMessageContains(name)) {
                        singleEvent.send("已存在同名定时消息！请重新输入名字，或者输入“*取消创建*”来取消创建");
                        return true;
                    }
                    ((TimedMessageContext) context).setName(name);
                    singleEvent.send("请输入重复的周期：每天/每周/每月/每年");
                    break;
                case 1:
                    String repeat = singleEvent.getMessage().getPlainString();
                    ((TimedMessageContext) context).setType(repeat);
                    switch (repeat) {
                        case "每天":
                            singleEvent.send(
                                    "请输入每天a点b分（24小时制），或者输入“*取消创建*”来取消创建\n" +
                                            "例如：每天20点0分，输入“20点0分”"
                            );
                            break;
                        case "每周":
                            singleEvent.send(
                                    "请输入每周的星期a b点c分（24小时制），或者输入“*取消创建*”来取消创建\n" +
                                            "例如：每周二的16点22分，输入“周二16点22分”"
                            );
                            break;
                        case "每月":
                            singleEvent.send(
                                    "请输入每月的a日b点c分（24小时制），或者输入“*取消创建*”来取消创建\n" +
                                            "例如：每月22日14点5分，输入“22日14点5分”"
                            );
                            break;
                        case "每年":
                            singleEvent.send(
                                    "请输入每年的a月b日c点d分（24小时制），或者输入“*取消创建*”来取消创建\n" +
                                            "例如：每年1月3日0点5分，输入“1月1日0点5分”"
                            );
                            break;
                        default:
                            singleEvent.send("非法输入，请重新输入，或者输入“*取消创建*”来取消创建");
                            return true;
                    }
                    break;
                case 2:
                    String time = singleEvent.getMessage().getPlainString();
                    switch (((TimedMessageContext) context).getType()) {
                        case "每天":
                            if (!time.matches("^\\d{1,2}点\\d{1,2}分$")) {
                                singleEvent.send("非法输入，请重新输入，或者输入“*取消创建*”来取消创建");
                                return true;
                            }
                            String[] times = time.split("点");
                            int hour = Integer.parseInt(times[0]);
                            int minute = Integer.parseInt(times[1].split("分")[0]);
                            if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                                singleEvent.send("非法输入，请重新输入，或者输入“*取消创建*”来取消创建");
                                return true;
                            }
                            ((TimedMessageContext) context).setChecker(new DailyChecker(hour, minute));
                            break;
                        case "每周":
                            if (!time.matches("^周[一二三四五六日天]\\d{1,2}点\\d{1,2}分$")) {
                                singleEvent.send("非法输入，请重新输入，或者输入“*取消创建*”来取消创建");
                                return true;
                            }
                            int week = 0;
                            switch (time.substring(0, 2)) {
                                case "周一":
                                    week = 1;
                                    break;
                                case "周二":
                                    week = 2;
                                    break;
                                case "周三":
                                    week = 3;
                                    break;
                                case "周四":
                                    week = 4;
                                    break;
                                case "周五":
                                    week = 5;
                                    break;
                                case "周六":
                                    week = 6;
                                    break;
                                case "周日":
                                case "周天":
                                    week = 7;
                                    break;
                            }
                            times = time.substring(2).split("点");
                            hour = Integer.parseInt(times[0]);
                            minute = Integer.parseInt(times[1].split("分")[0]);
                            if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                                singleEvent.send("非法输入，请重新输入，或者输入“*取消创建*”来取消创建");
                                return true;
                            }
                            ((TimedMessageContext) context).setChecker(new WeeklyChecker(week, hour, minute));
                            break;
                        case "每月":
                            if (!time.matches("^\\d{1,2}日\\d{1,2}点\\d{1,2}分$")) {
                                singleEvent.send("非法输入，请重新输入，或者输入“*取消创建*”来取消创建");
                                return true;
                            }
                            times = time.split("日");
                            int day = Integer.parseInt(times[0]);
                            times = times[1].split("点");
                            hour = Integer.parseInt(times[0]);
                            minute = Integer.parseInt(times[1].split("分")[0]);
                            if (day <= 0 || day > 31 || hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                                singleEvent.send("非法输入，请重新输入，或者输入“*取消创建*”来取消创建");
                                return true;
                            }
                            ((TimedMessageContext) context).setChecker(new MonthlyChecker(day, hour, minute));
                            break;
                        case "每年":
                            if (!time.matches("^\\d{1,2}月\\d{1,2}日\\d{1,2}点\\d{1,2}分$")) {
                                singleEvent.send("非法输入，请重新输入，或者输入“*取消创建*”来取消创建");
                                return true;
                            }
                            times = time.split("月");
                            int month = Integer.parseInt(times[0]);
                            times = times[1].split("日");
                            day = Integer.parseInt(times[0]);
                            times = times[1].split("点");
                            hour = Integer.parseInt(times[0]);
                            minute = Integer.parseInt(times[1].split("分")[0]);
                            if (month <= 0 || month > 12 || day <= 0 || day > 31 || hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                                singleEvent.send("非法输入，请重新输入，或者输入“*取消创建*”来取消创建");
                                return true;
                            }
                            ((TimedMessageContext) context).setChecker(new YearlyChecker(month, day, hour, minute));
                            break;
                        default:
                            singleEvent.send("[FATAL]严重内部错误！请及时联系管理员！");
                            ContextPool.remove(singleEvent.getSenderId());
                            return true;
                    }
                    singleEvent.send("当前重复周期" + ((TimedMessageContext) context).getChecker() + "\n" +
                            "请输入消息内容，或者输入“*取消创建*”来取消创建");
                    break;
                case 3:
                    ((TimedMessageContext) context).setReply(CommonUtil.getSerializeMessage(singleEvent.getConfig().getWorkdir() + "image/", singleEvent.getMessage().getOrigin()));
                    GroupTimedMessage groupTimedMessage = new GroupTimedMessage();
                    groupTimedMessage.setGroupId(((TimedMessageContext) context).getGroupId());
                    groupTimedMessage.setName(((TimedMessageContext) context).getName());

                    groupTimedMessage.setReply(((TimedMessageContext) context).getReply());
                    groupTimedMessage.setChecker(((TimedMessageContext) context).getChecker());
                    groupTimedMessage.setLastTime(null);

                    group.getTimedMessages().add(groupTimedMessage);
                    BackgroundTask.getInstance().put(groupTimedMessage);
                    ContextPool.remove(singleEvent.getSenderId());
                    GroupPool.save(singleEvent, group.getId());
                    singleEvent.send("创建成功！");
                    break;
            }
            context.next();
            return true;
        }
        return false;
    }

    @Override
    public void handleCommon(SingleEvent singleEvent) {

    }

    @Override
    public void handleFriend(SingleEvent singleEvent) {

    }

    @Override
    public void handleGroup(SingleEvent singleEvent) {
        reply(singleEvent);

        MyGroup group = GroupPool.get(singleEvent);
        // 复制群回复
        if (singleEvent.getMessage().plainStartWith("复制回复")) {
            if (!singleEvent.aboveGroupMaster()) {
                singleEvent.send("你无权复制回复");
                return;
            }
            if (ContextPool.contains(singleEvent.getSenderId())) {
                return;
            }
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("复制回复");
            long groupId = CommonUtil.getLong(analysis.getText());
            if (groupId <= 0L) return;
            Long temp = singleEvent.getGroupId();
            singleEvent.setGroupId(groupId);
            MyGroup group1 = GroupPool.get(singleEvent);
            singleEvent.setGroupId(temp);
            if (!group1.isAllowCopyAutoReply()) {
                singleEvent.send("目标群不允许复制回复");
                return;
            }
            CopyGroupConfirmContext context = new CopyGroupConfirmContext();
            context.setOriginGroup(singleEvent.getGroupId());
            context.setGroupId(groupId);
            ContextPool.put(singleEvent.getSenderId(), context);
            singleEvent.send(CommonUtil.confirmMessage());
        }
        // 添加回复
        else if (singleEvent.getMessage().plainStartWith("添加回复")) {
            if (!singleEvent.aboveGroupAdmin()) {
                singleEvent.send("你无权添加回复");
                return;
            }
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("添加回复");
            ArrayList<String> split = analysis.split();
            String key = null;
            String reply = null;
            long at = 0L;
            if (split.size() >= 2) {
                key = split.get(0).strip();
                reply = split.get(1).strip();
                if (split.size() == 3) {
                    at = CommonUtil.getLong(split.get(2));
                }
            }
            if (key == null || reply == null || at < 0L) {
                singleEvent.send("添加回复的格式错误");
                return;
            }
            boolean needInsert = true;
            long number = 0;
            for (BaseAutoReply autoReply : group.getAutoReplies()) {
                if (autoReply instanceof KeyReply) {
                    number++;
                    if (((KeyReply) autoReply).getKey().equals(key)) {
                        if (((KeyReply) autoReply).getReply().size() >= MAX_KEY_REPLY) {
                            singleEvent.send("当前触发词的回复超出" + MAX_KEY_REPLY + "个限制，无法继续添加");
                            return;
                        }
                        if (((KeyReply) autoReply).addReply(reply, at)) {
                            GroupPool.save(singleEvent);
                            singleEvent.send("添加“" + key + "-" + reply + "”成功！");
                        } else {
                            singleEvent.send("已经存在该词组配对");
                        }
                        needInsert = false;
                        break;
                    }
                }
            }
            if (needInsert) {
                if (number <= MAX_KEY) {
                    KeyReply keyReply = new KeyReply();
                    keyReply.setKey(key);
                    keyReply.addReply(reply, at);
                    group.add(keyReply);
                    GroupPool.save(singleEvent);
                    singleEvent.send("添加“" + key + "-" + reply + "”成功！");
                } else {
                    singleEvent.send("当前触发词超出" + MAX_KEY + "个限制，无法继续添加");
                }
            }
        }
        // 删除回复
        else if (singleEvent.getMessage().plainStartWith("删除回复")) {
            if (!singleEvent.aboveGroupAdmin()) {
                singleEvent.send("你无权删除回复");
                return;
            }
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("删除回复");
            ArrayList<String> split = analysis.split();
            String key = null;
            String reply = null;
            if (split.size() == 2) {
                key = split.get(0).strip();
                reply = split.get(1).strip();
            }
            if (key == null || reply == null) {
                singleEvent.send("删除回复的格式错误");
                return;
            }
            boolean notDelete = true;
            for (BaseAutoReply autoReply : group.getAutoReplies()) {
                if (autoReply instanceof KeyReply) {
                    if (((KeyReply) autoReply).getKey().equals(key)) {
                        if (((KeyReply) autoReply).removeReply(reply)) {
                            if (((KeyReply) autoReply).getReply().size() == 0) {
                                group.getAutoReplies().remove(autoReply);
                            }
                            GroupPool.save(singleEvent);
                            singleEvent.send("删除“" + key + "-" + reply + "”成功！");
                            notDelete = false;
                            break;
                        }
                    }
                }
            }
            if (notDelete) {
                singleEvent.send("未找到该词组配对");
            }
        }
        // 清空回复
        else if (singleEvent.getMessage().plainEqual("清空回复")) {
            if (!singleEvent.aboveGroupMaster()) {
                singleEvent.send("你无权清空回复");
                return;
            }
            if (ContextPool.contains(singleEvent.getSenderId())) {
                return;
            }
            KeyReplyClearContext context = new KeyReplyClearContext();
            context.setGroupId(singleEvent.getGroupId());
            ContextPool.put(singleEvent.getSenderId(), context);
            singleEvent.send(CommonUtil.confirmMessage());
        }
        // 查看回复
        else if (singleEvent.getMessage().plainStartWith("查看回复")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("查看回复");
            ArrayList<String> split = analysis.split();
            int page = 0;
            if (split.size() == 1) {
                page = CommonUtil.getInteger(split.get(0)) - 1;
                if (page < 0) {
                    singleEvent.send("页码范围有误");
                    return;
                }
            }
            StringBuilder builder = new StringBuilder();
            int index = 0;
            int total = 0;
            boolean init = false;
            for (BaseAutoReply autoReply : group.getAutoReplies()) {
                if (autoReply instanceof KeyReply) {
                    String key = ((KeyReply) autoReply).getKey();
                    total += ((KeyReply) autoReply).getReply().size();
                    if (index >= (page + 1) * PAGE_SIZE) {
                        continue;
                    }
                    for (String reply : ((KeyReply) autoReply).getReply()) {
                        if (index >= page * PAGE_SIZE && index < (page + 1) * PAGE_SIZE) {
                            init = true;
                            builder.append(index + 1).append(".").append(key).append(" - ").append(reply).append("\n");
                        }
                        index++;
                    }
                }
            }
            if (total % PAGE_SIZE != 0) {
                total = total / PAGE_SIZE + 1;
            } else {
                total = total / PAGE_SIZE;
            }
            if (!init) {
                if (total != 0) {
                    singleEvent.send("页码超限，总计：" + total + "页");
                } else {
                    singleEvent.send("暂无任何回复");
                }
                return;
            }
            builder.append("--------").append("\n页码：").append(page + 1).append("/").append(total);
            singleEvent.send(builder.toString());
        }
        // 添加关键词
        else if (singleEvent.getMessage().plainStartWith("添加关键词")) {
            if (!singleEvent.aboveGroupAdmin()) {
                singleEvent.send("你无权添加关键词");
                return;
            }
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("添加关键词");
            ArrayList<String> split = analysis.split();
            String key = null;
            String reply = null;
            long at = 0L;
            if (split.size() >= 2) {
                key = split.get(0).strip();
                reply = split.get(1).strip();
                if (split.size() == 3) {
                    at = CommonUtil.getLong(split.get(2));
                }
            }
            if (key == null || reply == null || at < 0L) {
                singleEvent.send("添加关键词的格式错误");
                return;
            }
            boolean needInsert = true;
            long number = 0;
            for (BaseAutoReply autoReply : group.getAutoReplies()) {
                if (autoReply instanceof KeyMatchReply) {
                    number++;
                    if (((KeyMatchReply) autoReply).getKeyMatch().equals(key)) {
                        if (((KeyMatchReply) autoReply).getReply().size() >= MAX_KEY_REPLY) {
                            singleEvent.send("当前关键词的回复超出" + MAX_KEY_REPLY + "个限制，无法继续添加");
                            return;
                        }
                        if (((KeyMatchReply) autoReply).addReply(reply, at)) {
                            GroupPool.save(singleEvent);
                            singleEvent.send("添加“" + key + "-" + reply + "”成功！");
                        } else {
                            singleEvent.send("已经存在该关键词组配对");
                        }
                        needInsert = false;
                        break;
                    }
                }
            }
            if (needInsert) {
                if (number <= MAX_KEY) {
                    KeyMatchReply keyReply = new KeyMatchReply();
                    keyReply.setKeyMatch(key);
                    keyReply.addReply(reply, at);
                    group.add(keyReply);
                    GroupPool.save(singleEvent);
                    singleEvent.send("添加关键词“" + key + "-" + reply + "”成功！");
                } else {
                    singleEvent.send("当前关键触发词超出" + MAX_KEY + "个限制，无法继续添加");
                }
            }
        }
        // 删除关键词
        else if (singleEvent.getMessage().plainStartWith("删除关键词")) {
            if (!singleEvent.aboveGroupAdmin()) {
                singleEvent.send("你无权删除关键词");
                return;
            }
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("删除关键词");
            ArrayList<String> split = analysis.split();
            String key = null;
            String reply = null;
            if (split.size() == 2) {
                key = split.get(0).strip();
                reply = split.get(1).strip();
            }
            if (key == null || reply == null) {
                singleEvent.send("删除回复的格式错误");
                return;
            }
            boolean notDelete = true;
            for (BaseAutoReply autoReply : group.getAutoReplies()) {
                if (autoReply instanceof KeyMatchReply) {
                    if (((KeyMatchReply) autoReply).getKeyMatch().equals(key)) {
                        if (((KeyMatchReply) autoReply).removeReply(reply)) {
                            if (((KeyMatchReply) autoReply).getReply().size() == 0) {
                                group.getAutoReplies().remove(autoReply);
                            }
                            GroupPool.save(singleEvent);
                            singleEvent.send("删除关键词“" + key + "-" + reply + "”成功！");
                            notDelete = false;
                            break;
                        }
                    }
                }
            }
            if (notDelete) {
                singleEvent.send("未找到该关键词词组配对");
            }
        }
        // 清空关键词
        else if (singleEvent.getMessage().plainEqual("清空关键词")) {
            if (!singleEvent.aboveGroupMaster()) {
                singleEvent.send("你无权清空关键词");
                return;
            }
            if (ContextPool.contains(singleEvent.getSenderId())) {
                return;
            }
            KeyMatchReplyClearContext context = new KeyMatchReplyClearContext();
            context.setGroupId(singleEvent.getGroupId());
            ContextPool.put(singleEvent.getSenderId(), context);
            singleEvent.send(CommonUtil.confirmMessage());
        }
        // 查看关键词
        else if (singleEvent.getMessage().plainStartWith("查看关键词")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("查看关键词");
            ArrayList<String> split = analysis.split();
            int page = 0;
            if (split.size() == 1) {
                page = CommonUtil.getInteger(split.get(0)) - 1;
                if (page < 0) {
                    singleEvent.send("页码范围有误");
                    return;
                }
            }
            StringBuilder builder = new StringBuilder();
            int index = 0;
            int total = 0;
            boolean init = false;
            for (BaseAutoReply autoReply : group.getAutoReplies()) {
                if (autoReply instanceof KeyMatchReply) {
                    String key = ((KeyMatchReply) autoReply).getKeyMatch();
                    total += ((KeyMatchReply) autoReply).getReply().size();
                    if (index >= (page + 1) * PAGE_SIZE) {
                        continue;
                    }
                    for (String reply : ((KeyMatchReply) autoReply).getReply()) {
                        if (index >= page * PAGE_SIZE && index < (page + 1) * PAGE_SIZE) {
                            init = true;
                            builder.append(index + 1).append(".").append(key).append(" - ").append(reply).append("\n");
                        }
                        index++;
                    }
                }
            }
            if (total % PAGE_SIZE != 0) {
                total = total / PAGE_SIZE + 1;
            } else {
                total = total / PAGE_SIZE;
            }
            if (!init) {
                if (total != 0) {
                    singleEvent.send("页码超限，总计：" + total + "页");
                } else {
                    singleEvent.send("暂无任何关键词");
                }
                return;
            }
            builder.append("--------").append("\n页码：").append(page + 1).append("/").append(total);
            singleEvent.send(builder.toString());
        }
        // 添加复杂回复
        else if (singleEvent.getMessage().plainEqual("添加复杂回复")) {
            if (!singleEvent.aboveGroupAdmin()) {
                singleEvent.send("你无权添加复杂回复");
                return;
            }
            if (ContextPool.contains(singleEvent.getSenderId())) {
                return;
            }
            ComplexReplyContext complexReplyContext = new ComplexReplyContext();
            complexReplyContext.setGroupId(singleEvent.getGroupId());
            complexReplyContext.setCreateId(singleEvent.getSenderId());
            ContextPool.put(singleEvent.getSenderId(), complexReplyContext);
            singleEvent.send("请在" + singleEvent.getBotName() + "的指引下完成复杂回复的添加~请问你的触发该回复的触发词是什么呢？（只能包含文本消息，你可以随时输入“*取消创建*”来取消，星号不可以省略哦~）");
        }
        // 删除复杂回复
        else if (singleEvent.getMessage().plainStartWith("删除复杂回复")) {
            if (!singleEvent.aboveGroupAdmin()) {
                singleEvent.send("你无权删除复杂回复");
                return;
            }
            String message = singleEvent.getMessage().getPlainString().substring(6).strip();
            boolean isDelete = false;
            for (BaseAutoReply autoReply : group.getAutoReplies()) {
                if (autoReply instanceof ComplexReply) {
                    if (((ComplexReply) autoReply).getKey().equals(message)) {
                        if (!CommonUtil.removeImageFile(((ComplexReply) autoReply).getReply())) {
                            singleEvent.sendMaster("在删除复杂回复的时候，遇见了一个未知错误，无法删除文件");
                        }
                        group.getAutoReplies().remove(autoReply);
                        singleEvent.send("删除成功~");
                        isDelete = true;
                        break;
                    }
                }
            }
            if (!isDelete) {
                singleEvent.send("未找到匹配项");
            }
        }
        // 清空复杂回复
        else if (singleEvent.getMessage().plainEqual("清空复杂回复")) {
            if (!singleEvent.aboveGroupMaster()) {
                singleEvent.send("你无权复杂回复");
                return;
            }
            if (ContextPool.contains(singleEvent.getSenderId())) {
                return;
            }
            ComplexReplyClearContext context = new ComplexReplyClearContext();
            context.setGroupId(singleEvent.getGroupId());
            ContextPool.put(singleEvent.getSenderId(), context);
            singleEvent.send(CommonUtil.confirmMessage());
        }
        // 查看复杂回复
        else if (singleEvent.getMessage().plainStartWith("查看复杂回复")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("查看复杂回复");
            int page = CommonUtil.getInteger(analysis.getText());
            if (page > 0) page--;
            int total = 0;
            boolean init = false;
            StringBuilder builder = new StringBuilder();
            for (BaseAutoReply autoReply : group.getAutoReplies()) {
                if (autoReply instanceof ComplexReply) {
                    if (total >= page * COMPLEX_REPLY_PAGE_SIZE && total < (page + 1) * COMPLEX_REPLY_PAGE_SIZE) {
                        init = true;
                        builder.append(total + 1).append(".").append(((ComplexReply) autoReply).getKey()).append("\n");
                    }
                    total++;
                }
            }
            if (total == 0) {
                singleEvent.send("暂无复杂回复");
                return;
            }
            if (total % COMPLEX_REPLY_PAGE_SIZE != 0) total = total / COMPLEX_REPLY_PAGE_SIZE + 1;
            else total = total / COMPLEX_REPLY_PAGE_SIZE;
            builder.append("--------").append("\n页码：").append(page + 1).append("/").append(total);
            if (init) {
                singleEvent.send(builder.toString());
            } else {
                singleEvent.send("页码超限，总计：" + total + "页");
            }
        }
        // 查看定时消息
        else if (singleEvent.getMessage().plainStartWith("查看定时消息")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("查看定时消息");
            int page = CommonUtil.getInteger(analysis.getText());
            if (page > 0) page--;
            int total = 0;
            boolean init = false;
            StringBuilder builder = new StringBuilder();
            for (GroupTimedMessage timedMessage : group.getTimedMessages()) {
                if (total >= page * TIMED_MESSAGE_PAGE_SIZE && total < (page + 1) * TIMED_MESSAGE_PAGE_SIZE) {
                    init = true;
                    builder.append(total + 1).append(".").append(timedMessage.toString()).append("\n");
                }
                total++;
            }
            if (total == 0) {
                singleEvent.send("暂无定时消息");
                return;
            }
            if (total % TIMED_MESSAGE_PAGE_SIZE != 0) total = total / TIMED_MESSAGE_PAGE_SIZE + 1;
            else total = total / TIMED_MESSAGE_PAGE_SIZE;
            builder.append("--------").append("\n页码：").append(page + 1).append("/").append(total);
            if (init) {
                singleEvent.send(builder.toString());
            } else {
                singleEvent.send("页码超限，总计：" + total + "页");
            }
        }
        // 发送定时消息
        else if (singleEvent.getMessage().plainStartWith("发送定时消息")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("发送定时消息");
            String name = analysis.getText();

            for (GroupTimedMessage timedMessage : group.getTimedMessages()) {
                if (timedMessage.getName().equals(name)) {
                    MessageChainBuilder messageChain = CommonUtil.getMessageChain(singleEvent, timedMessage.getReply());
                    singleEvent.send(messageChain.asMessageChain());
                    singleEvent.send("--------\n" +
                            "定时消息“" + name + "”如上");
                    return;
                }
            }
            singleEvent.send("没有这个定时消息");
        }
        // 清空定时消息
        else if (singleEvent.getMessage().plainEqual("清空定时消息")) {
            if (!singleEvent.aboveGroupMaster()) {
                singleEvent.send("你无权清空定时消息");
                return;
            }
            if (ContextPool.contains(singleEvent.getSenderId())) {
                return;
            }
            TimedMessageClearContext context = new TimedMessageClearContext();
            context.setGroupId(singleEvent.getGroupId());
            ContextPool.put(singleEvent.getSenderId(), context);
            singleEvent.send(CommonUtil.confirmMessage());
        }
        // 添加定时消息
        else if (singleEvent.getMessage().plainEqual("添加定时消息")) {
            if (!singleEvent.aboveGroupAdmin()) {
                singleEvent.send("你无权添加定时消息");
                return;
            }
            if (ContextPool.contains(singleEvent.getSenderId())) {
                return;
            }
            TimedMessageContext context = new TimedMessageContext();
            context.setGroupId(singleEvent.getGroupId());
            ContextPool.put(singleEvent.getSenderId(), context);
            singleEvent.send("请给定时消息，取一个唯一标识的名字，保证你看见名字可以明白这是什么消息哦！（你可以在任意时刻输入“*取消创建*”来取消创建，前后的星号不可以省略）");
        }
        // 删除定时消息
        else if (singleEvent.getMessage().plainStartWith("删除定时消息")) {
            if (!singleEvent.aboveGroupAdmin()) {
                singleEvent.send("你无权删除定时消息");
                return;
            }
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("删除定时消息");
            String name = analysis.getText();

            for (GroupTimedMessage timedMessage : group.getTimedMessages()) {
                if (timedMessage.getName().equals(name)) {
                    group.getTimedMessages().remove(timedMessage);
                    BackgroundTask.getInstance().remove(timedMessage);
                    GroupPool.save(singleEvent);
                    singleEvent.send("删除成功");
                    return;
                }
            }
            singleEvent.send("没有这个定时消息");
        }
    }
}
