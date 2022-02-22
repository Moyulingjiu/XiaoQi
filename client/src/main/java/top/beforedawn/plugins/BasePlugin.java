package top.beforedawn.plugins;

import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import top.beforedawn.models.bo.MyMessage;
import top.beforedawn.util.SimpleCombineBot;
import top.beforedawn.util.SingleEvent;

/**
 * 最基础的功能
 *
 * @author BeforeDawn
 */
public abstract class BasePlugin {
    protected MessageChainBuilder messages = new MessageChainBuilder();
    protected String pluginName = "default_plugin";

    public void handle(SingleEvent singleEvent) {
        singleEvent.setTitle(pluginName);
        handleCommon(singleEvent);
        if (singleEvent.isFriendMessage()) {
            handleFriend(singleEvent);
        } else if (singleEvent.isGroupMessage()) {
            handleGroup(singleEvent);
        }
        singleEvent.setTitle(null);
    }

    public abstract void handleCommon(SingleEvent singleEvent);
    public abstract void handleFriend(SingleEvent singleEvent);
    public abstract void handleGroup(SingleEvent singleEvent);
}
