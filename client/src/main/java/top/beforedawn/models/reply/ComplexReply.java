package top.beforedawn.models.reply;

import net.mamoe.mirai.message.data.MessageChain;
import top.beforedawn.models.bo.MyMessage;

public class ComplexReply extends BaseAutoReply {
    @Override
    public boolean check(MyMessage message) {
        return false;
    }

    @Override
    public MessageChain getReply() {
        return null;
    }
}
