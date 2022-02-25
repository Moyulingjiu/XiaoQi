package top.beforedawn.plugins;

import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import top.beforedawn.config.GroupPool;
import top.beforedawn.models.bo.MyGroup;
import top.beforedawn.util.SingleEvent;

/**
 * 解锁闪照
 */
public class UnlockFlashFunction extends BasePlugin {
    @Override
    public boolean before(SingleEvent singleEvent) {
        if (singleEvent.isFriendMessage()) {
            return true;
        }
        if (!singleEvent.isGroupMessage()) {
            return false;
        }
        MyGroup group = GroupPool.get(singleEvent);
        return group.isUnlockFlashImage();
    }

    @Override
    public void handleCommon(SingleEvent singleEvent) {
        if (singleEvent.getMessage().getFlashImages().size() != 0) {
            for (Image flashImage : singleEvent.getMessage().getFlashImages()) {
                MessageChainBuilder builder = new MessageChainBuilder();
                builder.append(flashImage);
                singleEvent.send(builder.asMessageChain());
            }
        }
    }

    @Override
    public void handleFriend(SingleEvent singleEvent) {

    }

    @Override
    public void handleGroup(SingleEvent singleEvent) {

    }
}
