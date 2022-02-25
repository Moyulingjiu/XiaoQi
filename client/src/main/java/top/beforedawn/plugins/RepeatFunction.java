package top.beforedawn.plugins;

import top.beforedawn.config.GroupPool;
import top.beforedawn.models.bo.MyGroup;
import top.beforedawn.util.SingleEvent;

/**
 * 自动加一的模块
 */
public class RepeatFunction extends BasePlugin {
    public RepeatFunction() {
        pluginName = "repeat";
    }

    @Override
    public boolean before(SingleEvent singleEvent) {
        if (!singleEvent.isGroupMessage()) {
            return false;
        }
        MyGroup group = GroupPool.get(singleEvent);
        return group.isRepeat();
    }

    @Override
    public void handleCommon(SingleEvent singleEvent) {

    }

    @Override
    public void handleFriend(SingleEvent singleEvent) {

    }

    @Override
    public void handleGroup(SingleEvent singleEvent) {

    }
}
