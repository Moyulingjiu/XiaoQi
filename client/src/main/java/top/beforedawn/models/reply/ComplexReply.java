package top.beforedawn.models.reply;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.AtAll;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import top.beforedawn.models.bo.MyMessage;
import top.beforedawn.models.context.ComplexReplyContext;
import top.beforedawn.models.context.SerializeMessage;
import top.beforedawn.util.CommonUtil;
import top.beforedawn.util.SingleEvent;

import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplexReply extends BaseAutoReply {
    private Long createId;
    private String key;
    private ArrayList<SerializeMessage> reply;
    private ComplexReplyContext.AtType type;
    private ArrayList<Long> at;

    public ComplexReply(ComplexReplyContext context) {
        createId = context.getCreateId();
        key = context.getKey();
        reply = context.getReply();
        type = context.getType();
        at = context.getAt();
    }

    @Override
    public boolean check(MyMessage message) {
        return key.equals(message.getPlainString());
    }

    @Override
    public MessageChain reply(SingleEvent singleEvent) {
        MessageChainBuilder chainBuilder = new MessageChainBuilder();
        switch (type) {
            case NONE:
                break;
            case ALL:
                chainBuilder.append(AtAll.INSTANCE);
                break;
            case TRIGGER:
                chainBuilder.append(new At(singleEvent.getSenderId()));
                break;
            case NORMAL:
                for (Long id : at) {
                    chainBuilder.append(new At(id));
                }
                break;
        }
        chainBuilder.addAll(CommonUtil.getMessageChain(singleEvent, reply));
        return chainBuilder.asMessageChain();
    }
}
