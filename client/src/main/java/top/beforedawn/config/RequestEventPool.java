package top.beforedawn.config;

import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * 申请事件的缓存池
 *
 * @author 墨羽翎玖
 */
public class RequestEventPool {
    public static final Map<Long, NewFriendRequestEvent> friendRequestEventMap = new HashMap<>();
    public static final Map<Long, BotInvitedJoinGroupRequestEvent> groupRequestEventMap = new HashMap<>();

    /**
     * 添加好友请求的事件
     *
     * @param event 事件
     */
    public static void put(NewFriendRequestEvent event) {
        if (event != null)
            friendRequestEventMap.put(event.getEventId(), event);
    }

    /**
     * 添加群请求的事件
     *
     * @param event 事件
     */
    public static void put(BotInvitedJoinGroupRequestEvent event) {
        if (event != null)
            groupRequestEventMap.put(event.getEventId(), event);
    }

    /**
     * 弹出好友请求的事件
     *
     * @param id 事件号
     * @return 好友事件
     */
    public static NewFriendRequestEvent popFriend(long id) {
        NewFriendRequestEvent event = friendRequestEventMap.get(id);
        if (event != null)
            friendRequestEventMap.remove(id);
        return event;
    }

    /**
     * 弹出群请求的事件
     *
     * @param id 事件号
     * @return 群申请事件
     */
    public static BotInvitedJoinGroupRequestEvent popGroup(long id) {
        BotInvitedJoinGroupRequestEvent event = groupRequestEventMap.get(id);
        if (event != null)
            groupRequestEventMap.remove(id);
        return event;
    }

    /**
     * 接受好友请求的事件
     *
     * @param id 事件号
     * @return boolean
     */
    public static boolean acceptFriend(long id) {
        NewFriendRequestEvent event = friendRequestEventMap.get(id);
        if (event == null)
            return false;
        event.accept();
        friendRequestEventMap.remove(id);
        return true;
    }

    /**
     * 拒绝好友请求的事件
     *
     * @param id 事件号
     * @return boolean
     */
    public static boolean rejectFriend(long id) {
        NewFriendRequestEvent event = friendRequestEventMap.get(id);
        if (event == null)
            return false;
        event.reject(false);
        friendRequestEventMap.remove(id);
        return true;
    }

    /**
     * 接受群申请请求的事件
     *
     * @param id 事件号
     * @return boolean
     */
    public static boolean acceptGroup(long id) {
        BotInvitedJoinGroupRequestEvent event = groupRequestEventMap.get(id);
        if (event == null)
            return false;
        event.accept();
        friendRequestEventMap.remove(id);
        return true;
    }

    /**
     * 拒绝群申请请求的事件
     *
     * @param id 事件号
     * @return boolean
     */
    public static boolean rejectGroup(long id) {
        BotInvitedJoinGroupRequestEvent event = groupRequestEventMap.get(id);
        if (event == null)
            return false;
        event.ignore();
        groupRequestEventMap.remove(id);
        return true;
    }
}
