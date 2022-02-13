package top.beforedawn.plugins;

import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import top.beforedawn.models.bo.MyMessage;
import top.beforedawn.util.CommonUtil;
import top.beforedawn.util.SimpleCombineBot;

/**
 * 基础功能
 *
 * @author 墨羽翎玖
 */
public class BaseFunction extends BasePlugin {

    @Override
    public void handle(MyMessage message, SimpleCombineBot combineBot, GroupMessageEvent event) {
        if (message.plainEqual("骰子") || message.plainEqual("色子")) {
            messages = new MessageChainBuilder()
                    .append(new At(event.getSender().getId()))
                    .append(new PlainText(String.format("你投出的点数是：%d", CommonUtil.RandomInteger(1, 6))));
            event.getSubject().sendMessage(messages.asMessageChain());
        } else if (message.plainEqual("硬币")) {
            if (CommonUtil.RandomInteger() % 2 == 0) {
                messages = new MessageChainBuilder()
                        .append(new At(event.getSender().getId()))
                        .append(new PlainText("你抛出的是：正面"));
            } else {
                messages = new MessageChainBuilder()
                        .append(new At(event.getSender().getId()))
                        .append(new PlainText("你抛出的是：反面"));
            }
            event.getSubject().sendMessage(messages.asMessageChain());
        } else if (message.plainEqual("运势")) {
            int luck = 50;
            messages = new MessageChainBuilder()
                    .append(new At(event.getSender().getId()))
                    .append(new PlainText("你今天的运势是：" + luck));
            event.getSubject().sendMessage(messages.asMessageChain());
        }
    }

    @Override
    public void handle(MyMessage message, SimpleCombineBot combineBot, FriendMessageEvent event) {

    }
}
