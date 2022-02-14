package top.beforedawn.plugins;

import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import top.beforedawn.models.bo.MyMessage;
import top.beforedawn.models.bo.MyUser;
import top.beforedawn.util.CommonUtil;
import top.beforedawn.util.FunctionUtil;
import top.beforedawn.util.SimpleCombineBot;
import top.beforedawn.util.SingleEvent;

/**
 * 基础功能
 *
 * @author 墨羽翎玖
 */
public class BaseFunction extends BasePlugin {
    @Override
    public void handleCommon(SingleEvent singleEvent) {
        if (singleEvent.getMessage().plainEqual("骰子") || singleEvent.getMessage().plainEqual("色子")) {
            singleEvent.sendAt(String.format("你投出的点数是：%d", CommonUtil.RandomInteger(1, 6)));
        } else if (singleEvent.getMessage().plainEqual("硬币")) {
            if (CommonUtil.RandomInteger() % 2 == 0) {
                singleEvent.sendAt("你抛出的是：正面");
            } else {
                singleEvent.sendAt("你抛出的是：反面");
            }
        } else if (singleEvent.getMessage().plainEqual("运势")) {
            MyUser user = FunctionUtil.getLuck(singleEvent.getSenderId(), singleEvent.getBotId());
            int luck = user.getLuck();
            singleEvent.sendAt("你今天的运势是：" + luck);
        }
    }

    @Override
    public void handleFriend(SingleEvent singleEvent) {

    }

    @Override
    public void handleGroup(SingleEvent singleEvent) {

    }
}
