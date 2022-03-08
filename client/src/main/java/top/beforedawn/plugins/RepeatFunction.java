package top.beforedawn.plugins;

import top.beforedawn.config.GroupPool;
import top.beforedawn.models.bo.MyGroup;
import top.beforedawn.models.bo.MyMessage;
import top.beforedawn.util.SingleEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * 自动加一的模块
 */
public class RepeatFunction extends BasePlugin {
    private static final int MAX_POOL = 200;
    public static Map<Long, MyMessage> lastReceived = new HashMap<>();
    public static Map<Long, MyMessage> lastSend = new HashMap<>();

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
        MyMessage message1 = lastReceived.get(singleEvent.getGroupId());
        if (message1 != null && message1.equals(singleEvent.getMessage())) {
            MyMessage message2 = lastSend.get(singleEvent.getGroupId());
            if (message2 == null || !message2.equals(singleEvent.getMessage())) {
                singleEvent.send(singleEvent.getGroupMessageEvent().getMessage());
                lastSend.put(singleEvent.getGroupId(), singleEvent.getMessage());
            }
        }
        // 超出存储上限直接清空
        if (lastReceived.size() > MAX_POOL) {
            lastReceived = new HashMap<>();
            lastSend = new HashMap<>();
        }
        lastReceived.put(singleEvent.getGroupId(), singleEvent.getMessage());
    }
}
