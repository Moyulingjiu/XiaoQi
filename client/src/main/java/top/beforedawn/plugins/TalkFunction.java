package top.beforedawn.plugins;

import top.beforedawn.config.GroupPool;
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
            else if (singleEvent.getMessage().plainEqual("恐怖故事"))
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
    }

    @Override
    public void handleFriend(SingleEvent singleEvent) {

    }

    @Override
    public void handleGroup(SingleEvent singleEvent) {

    }
}
