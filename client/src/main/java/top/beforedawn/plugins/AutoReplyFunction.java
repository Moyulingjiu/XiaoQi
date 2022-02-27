package top.beforedawn.plugins;

import top.beforedawn.config.GroupPool;
import top.beforedawn.models.bo.MessageLinearAnalysis;
import top.beforedawn.models.bo.MyGroup;
import top.beforedawn.models.reply.BaseAutoReply;
import top.beforedawn.models.reply.KeyMatchReply;
import top.beforedawn.models.reply.KeyReply;
import top.beforedawn.util.CommonUtil;
import top.beforedawn.util.SingleEvent;

import javax.swing.*;
import java.util.ArrayList;

/**
 * 自动回复的插件
 */
public class AutoReplyFunction extends BasePlugin {
    private static final int MAX_KEY_REPLY = 20;
    private static final int MAX_KEY = 100;
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
                singleEvent.send(autoReply.reply());
            }
        }
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
                    if (((KeyMatchReply) autoReply).getKey().equals(key)) {
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
                    keyReply.setKey(key);
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
                    if (((KeyMatchReply) autoReply).getKey().equals(key)) {
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
                    String key = ((KeyMatchReply) autoReply).getKey();
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
    }
}
