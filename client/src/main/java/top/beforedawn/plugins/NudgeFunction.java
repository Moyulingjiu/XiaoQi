package top.beforedawn.plugins;

import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import top.beforedawn.config.GroupPool;
import top.beforedawn.models.bo.MyGroup;
import top.beforedawn.util.CommonUtil;
import top.beforedawn.util.SingleEvent;

/**
 * 戳一戳事件
 */
public class NudgeFunction extends BasePlugin {
    public NudgeFunction() {
        pluginName = "nudge";
    }

    @Override
    public boolean before(SingleEvent singleEvent) {
        if (singleEvent.getGroupId() == 0L) {
            return singleEvent.getMessage().isBeNudge() && singleEvent.getMessage().isBeAt();
        } else {
            MyGroup group = GroupPool.get(singleEvent);
            return singleEvent.getMessage().isBeNudge() && singleEvent.getMessage().isBeAt() && group.isNudge();
        }
    }

    @Override
    public void handleCommon(SingleEvent singleEvent) {
        String filepath = singleEvent.getConfig().getGlobalWorkdir() + "nudge/";
        MessageChainBuilder builder = new MessageChainBuilder();
        switch (CommonUtil.randomInteger(4)) {
            case 0:
                builder.append(new PlainText("你再戳？你再戳？"));
                builder.append(singleEvent.uploadImage(filepath + "打.gif"));
                break;
            case 1:
                builder.append(singleEvent.uploadImage(filepath + "质疑.jpg"));
                break;
            default:
                builder.append(new PlainText("别戳啦~"));
        }
        singleEvent.send(builder.asMessageChain());
    }

    @Override
    public void handleFriend(SingleEvent singleEvent) {

    }

    @Override
    public void handleGroup(SingleEvent singleEvent) {

    }
}
