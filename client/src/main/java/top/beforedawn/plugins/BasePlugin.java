package top.beforedawn.plugins;

import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import top.beforedawn.models.bo.MyMessage;
import top.beforedawn.util.SimpleCombineBot;

/**
 * 最基础的功能
 *
 * @author BeforeDawn
 */
public abstract class BasePlugin {
    protected MessageChainBuilder messages = new MessageChainBuilder();

    public abstract void handle(MyMessage message, SimpleCombineBot combineBot, GroupMessageEvent event);
    public abstract void handle(MyMessage message, SimpleCombineBot combineBot, FriendMessageEvent event);
}
