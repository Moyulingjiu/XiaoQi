package top.beforedawn.models.reply;

import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import top.beforedawn.models.bo.MyMessage;
import top.beforedawn.util.CommonUtil;

import java.util.ArrayList;

public class KeyMatchReply extends BaseAutoReply {
    private String key;
    private ArrayList<String> reply = new ArrayList<>();
    private long atId;

    @Override
    public boolean check(MyMessage message) {
        return message.getPlainString().contains(key);
    }

    @Override
    public MessageChain getReply() {
        MessageChainBuilder messages = new MessageChainBuilder();
        int index = CommonUtil.randomInteger(reply.size());
        messages.append(new PlainText(reply.get(index)));
        if (atId > 0L) {
            messages.append(new At(atId));
        }
        return messages.asMessageChain();
    }

    public void setReply(ArrayList<String> reply) {
        this.reply = reply;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getAtId() {
        return atId;
    }

    public void setAtId(long atId) {
        this.atId = atId;
    }

    public KeyMatchReply() {
    }

    public KeyMatchReply(String key, ArrayList<String> reply, long atId) {
        this.key = key;
        this.reply = reply;
        this.atId = atId;
    }
}
