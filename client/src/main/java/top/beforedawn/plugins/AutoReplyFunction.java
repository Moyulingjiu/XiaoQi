package top.beforedawn.plugins;

import top.beforedawn.config.GroupPool;
import top.beforedawn.models.bo.MyGroup;
import top.beforedawn.models.reply.BaseAutoReply;
import top.beforedawn.util.SingleEvent;

/**
 * 自动回复的插件
 */
public class AutoReplyFunction extends BasePlugin {
    public AutoReplyFunction() {
        pluginName = "auto_reply";
    }

    @Override
    public boolean before(SingleEvent singleEvent) {
        if (!singleEvent.isGroupMessage()) {
            return false;
        }
        MyGroup group = GroupPool.get(singleEvent);
        return group.isAutoReply();
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
