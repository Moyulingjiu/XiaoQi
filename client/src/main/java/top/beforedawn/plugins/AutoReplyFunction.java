package top.beforedawn.plugins;

import top.beforedawn.config.ContextPool;
import top.beforedawn.config.GroupPool;
import top.beforedawn.models.bo.MessageLinearAnalysis;
import top.beforedawn.models.bo.MyGroup;
import top.beforedawn.models.context.ComplexReplyContext;
import top.beforedawn.models.context.Context;
import top.beforedawn.models.context.SerializeMessage;
import top.beforedawn.models.reply.BaseAutoReply;
import top.beforedawn.models.reply.ComplexReply;
import top.beforedawn.models.reply.KeyMatchReply;
import top.beforedawn.models.reply.KeyReply;
import top.beforedawn.util.CommonUtil;
import top.beforedawn.util.SingleEvent;

import java.util.ArrayList;

/**
 * 自动回复的插件
 */
public class AutoReplyFunction extends BasePlugin {
    private static final int MAX_KEY_REPLY = 20;
    private static final int MAX_KEY = 100;
    private static final int COMPLEX_REPLY_PAGE_SIZE = 30;
    private static final int PAGE_SIZE = 25;

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
        // 复杂回复
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
        if (singleEvent.getMessage().plainStartWith("添加回复")) {
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
        } else if (singleEvent.getMessage().plainStartWith("删除回复")) {
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
        } else if (singleEvent.getMessage().plainStartWith("查看回复")) {
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
        } else if (singleEvent.getMessage().plainStartWith("添加关键词")) {
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
        } else if (singleEvent.getMessage().plainStartWith("删除关键词")) {
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
        } else if (singleEvent.getMessage().plainStartWith("查看关键词")) {
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
        // 复杂回复
        else if (singleEvent.getMessage().plainEqual("添加复杂回复")) {
            if (ContextPool.contains(singleEvent.getSenderId())) {
                return;
            }
            ComplexReplyContext complexReplyContext = new ComplexReplyContext();
            complexReplyContext.setGroupId(singleEvent.getGroupId());
            complexReplyContext.setCreateId(singleEvent.getSenderId());
            ContextPool.put(singleEvent.getSenderId(), complexReplyContext);
            singleEvent.send("请在" + singleEvent.getBotName() + "的指引下完成复杂回复的添加~请问你的触发该回复的触发词是什么呢？（只能包含文本消息，你可以随时输入“*取消创建*”来取消，星号不可以省略哦~）");
        }
        else if (singleEvent.getMessage().plainStartWith("删除复杂回复")) {
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
        else if (singleEvent.getMessage().plainStartWith("查看复杂回复")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("查看复杂回复");
            int page = CommonUtil.getInteger(analysis.getText());
            if (page > 0) page--;
            int total = 0;
            StringBuilder builder = new StringBuilder();
            for (BaseAutoReply autoReply : group.getAutoReplies()) {
                if (autoReply instanceof ComplexReply) {
                    if (total >= page * COMPLEX_REPLY_PAGE_SIZE && total < (page + 1) * COMPLEX_REPLY_PAGE_SIZE) {
                        builder.append(total + 1).append(".").append(((ComplexReply) autoReply).getKey()).append("\n");
                    }
                    total++;
                }
            }
            if (total == 0) {
                singleEvent.send("暂无复杂回复");
                return;
            }
            builder.append("--------").append("\n页码：").append(page + 1).append("/").append(total);
            singleEvent.send(builder.toString());
        }
    }
}
