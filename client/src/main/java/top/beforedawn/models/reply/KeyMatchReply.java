package top.beforedawn.models.reply;

import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import top.beforedawn.models.bo.MyMessage;

public class KeyMatchReply extends BaseAutoReply {
    private String key;
    private String reply;
    private long atId;

    @Override
    public boolean check(MyMessage message) {
        return message.getPlainString().contains(key);
    }

    @Override
    public MessageChain getReply() {
        MessageChainBuilder messages = new MessageChainBuilder();
        messages.append(new PlainText(reply));
        if (atId > 0L) {
            messages.append(new At(atId));
        }
        return messages.asMessageChain();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public long getAtId() {
        return atId;
    }

    public void setAtId(long atId) {
        this.atId = atId;
    }

    public KeyMatchReply() {
    }

    public KeyMatchReply(String key, String reply, long atId) {
        this.key = key;
        this.reply = reply;
        this.atId = atId;
    }
}
