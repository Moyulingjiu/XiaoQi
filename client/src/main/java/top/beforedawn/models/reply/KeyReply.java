package top.beforedawn.models.reply;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import top.beforedawn.models.bo.MyMessage;
import top.beforedawn.util.CommonUtil;
import top.beforedawn.util.SingleEvent;

import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyReply extends BaseAutoReply {
    private String key;
    private ArrayList<String> reply = new ArrayList<>();
    private ArrayList<Long> atId = new ArrayList<>();

    @Override
    public boolean check(MyMessage message) {
        return message.getPlainString().equals(key);
    }

    @Override
    public MessageChain reply(SingleEvent singleEvent) {
        MessageChainBuilder messages = new MessageChainBuilder();
        int index = CommonUtil.randomInteger(reply.size());
        messages.append(new PlainText(reply.get(index)));
        if (atId.get(index) > 0L) {
            messages.append(new At(atId.get(index)));
        }
        return messages.asMessageChain();
    }

    public boolean removeReply(String reply) {
        for (int i = 0; i < this.reply.size(); i++) {
            if (this.reply.get(i).equals(reply)) {
                this.reply.remove(i);
                atId.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean addReply(String reply, long atId) {
        for (String s : this.reply) {
            if (s.equals(reply)) {
                return false;
            }
        }
        this.atId.add(atId);
        this.reply.add(reply);
        return true;
    }
}
