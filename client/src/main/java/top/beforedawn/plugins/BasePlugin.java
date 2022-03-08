package top.beforedawn.plugins;

import net.mamoe.mirai.message.data.MessageChainBuilder;
import top.beforedawn.util.SingleEvent;

/**
 * 最基础的功能
 *
 * @author BeforeDawn
 */
public abstract class BasePlugin {
    protected MessageChainBuilder messages = new MessageChainBuilder();
    protected String pluginName = "default_plugin";

    /**
     * 执行入口
     * 执行顺序：before->common->friend->group->after
     * 1. 如果before的检验不能通过则不会继续向后执行。
     *
     * @param singleEvent 事件
     */
    public void handle(SingleEvent singleEvent) {
        singleEvent.setTitle(pluginName);
        if (!before(singleEvent))
            return;
        if (handContextCommon(singleEvent))
            return;
        handleCommon(singleEvent);
        if (singleEvent.isFriendMessage()) {
            if (handContextFriend(singleEvent))
                return;
            handleFriend(singleEvent);
        } else if (singleEvent.isGroupMessage()) {
            if (handContextGroup(singleEvent))
                return;
            handleGroup(singleEvent);
        }
        after(singleEvent);
        singleEvent.setTitle(null);
    }

    public boolean before(SingleEvent singleEvent) {
        return true;
    }

    public void after(SingleEvent singleEvent) {

    }

    public boolean handContextCommon(SingleEvent singleEvent) {
        return false;
    }

    public boolean handContextFriend(SingleEvent singleEvent) {
        return false;
    }

    public boolean handContextGroup(SingleEvent singleEvent) {
        return false;
    }

    public abstract void handleCommon(SingleEvent singleEvent);

    public abstract void handleFriend(SingleEvent singleEvent);

    public abstract void handleGroup(SingleEvent singleEvent);
}
