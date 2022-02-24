package top.beforedawn.models.reply;

import net.mamoe.mirai.message.data.MessageChain;
import top.beforedawn.models.bo.MyMessage;

public abstract class BaseAutoReply {
    /**
     * 检验是否符合标准
     *
     * @return 返回
     */
    public abstract boolean check(MyMessage message);

    /**
     * 获取回复
     *
     * @return 消息链
     */
    public abstract MessageChain getReply();
}
