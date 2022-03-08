package top.beforedawn.plugins;

import top.beforedawn.config.GroupPool;
import top.beforedawn.models.bo.MessageLinearAnalysis;
import top.beforedawn.models.bo.MyGroup;
import top.beforedawn.util.HttpUtil;
import top.beforedawn.util.SingleEvent;

public class TalkFunction extends BasePlugin {

    @Override
    public void handleCommon(SingleEvent singleEvent) {
        boolean allowTalk = true;
        boolean allowSwear = true;
        if (singleEvent.isGroupMessage()) {
            MyGroup group = GroupPool.get(singleEvent);
            allowTalk = group.isTalk();
            allowSwear = group.isSwear();
        }
        if (allowTalk) {
            if (singleEvent.getMessage().plainEqual("文摘"))
                singleEvent.send(HttpUtil.getTalk(singleEvent, (byte) 0));
            else if (singleEvent.getMessage().plainEqual("情话"))
                singleEvent.send(HttpUtil.getTalk(singleEvent, (byte) 1));
            else if (singleEvent.getMessage().plainEqual("舔狗日记"))
                singleEvent.send(HttpUtil.getTalk(singleEvent, (byte) 3));
            else if (singleEvent.getMessage().plainEqual("笑话"))
                singleEvent.send(HttpUtil.getTalk(singleEvent, (byte) 4));
            else if (singleEvent.getMessage().plainEqual("鬼故事"))
                singleEvent.send(HttpUtil.getTalk(singleEvent, (byte) 5));

        }
        if (allowSwear) {
            if (
                    singleEvent.getMessage().plainEqual("骂我")
                            || singleEvent.getMessage().plainEqual("再骂")
                            || singleEvent.getMessage().plainEqual("你再骂")
                            || singleEvent.getMessage().plainEqual("骂我一句")
            )
                singleEvent.send(HttpUtil.getTalk(singleEvent, (byte) 2));
        }
        if (singleEvent.aboveSystemAdmin()) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            // 添加文摘
            if (singleEvent.getMessage().plainStartWith("添加文摘")) {
                analysis.pop("添加文摘");
                if (analysis.getText().length() >= 4) {
                    if (HttpUtil.sendTalk(singleEvent, analysis.getText(), 0)) {
                        singleEvent.send("添加成功");
                    }
                } else {
                    singleEvent.send("格式错误或句子太短");
                }
            }
            // 添加情话
            else if (singleEvent.getMessage().plainStartWith("添加情话")) {
                analysis.pop("添加情话");
                if (analysis.getText().length() >= 4) {
                    if (HttpUtil.sendTalk(singleEvent, analysis.getText(), 1)) {
                        singleEvent.send("添加成功");
                    }
                } else {
                    singleEvent.send("格式错误或句子太短");
                }
            }
            // 添加脏话
            else if (singleEvent.getMessage().plainStartWith("添加脏话")) {
                analysis.pop("添加脏话");
                if (analysis.getText().length() >= 4) {
                    if (HttpUtil.sendTalk(singleEvent, analysis.getText(), 2)) {
                        singleEvent.send("添加成功");
                    }
                } else {
                    singleEvent.send("格式错误或句子太短");
                }
            }
            // 添加舔狗日记
            else if (singleEvent.getMessage().plainStartWith("添加舔狗日记")) {
                analysis.pop("添加舔狗日记");
                if (analysis.getText().length() >= 4) {
                    if (HttpUtil.sendTalk(singleEvent, analysis.getText(), 3)) {
                        singleEvent.send("添加成功");
                    }
                } else {
                    singleEvent.send("格式错误或句子太短");
                }
            }
            // 添加笑话
            else if (singleEvent.getMessage().plainStartWith("添加笑话")) {
                analysis.pop("添加笑话");
                if (analysis.getText().length() >= 4) {
                    if (HttpUtil.sendTalk(singleEvent, analysis.getText(), 4)) {
                        singleEvent.send("添加成功");
                    }
                } else {
                    singleEvent.send("格式错误或句子太短");
                }
            }
            // 添加鬼故事
            else if (singleEvent.getMessage().plainStartWith("添加鬼故事")) {
                analysis.pop("添加鬼故事");
                if (analysis.getText().length() >= 4) {
                    if (HttpUtil.sendTalk(singleEvent, analysis.getText(), 5)) {
                        singleEvent.send("添加成功");
                    }
                } else {
                    singleEvent.send("格式错误或句子太短");
                }
            }
        }
    }

    @Override
    public void handleFriend(SingleEvent singleEvent) {

    }

    @Override
    public void handleGroup(SingleEvent singleEvent) {

    }
}
