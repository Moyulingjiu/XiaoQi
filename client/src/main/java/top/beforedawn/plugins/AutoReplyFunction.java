package top.beforedawn.plugins;

import top.beforedawn.config.GroupPool;
import top.beforedawn.models.bo.MyGroup;
import top.beforedawn.models.reply.BaseAutoReply;
import top.beforedawn.util.SingleEvent;

/**
 * 自动回复的插件
 */
public class AutoReplyFunction extends BasePlugin {
    @Override
    public boolean before(SingleEvent singleEvent) {
        return singleEvent.getGroupId() > 0L;
    }

    @Override
    public void handleCommon(SingleEvent singleEvent) {

    }

    @Override
    public void handleFriend(SingleEvent singleEvent) {

    }

    @Override
    public void handleGroup(SingleEvent singleEvent) {
        MyGroup group = GroupPool.get(singleEvent);
        for (BaseAutoReply autoReply : group.getAutoReplies()) {
            if (autoReply.check(singleEvent.getMessage())) {
                singleEvent.send(autoReply.getReply());
            }
        }
    }
}
