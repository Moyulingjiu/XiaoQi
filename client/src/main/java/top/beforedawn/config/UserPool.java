package top.beforedawn.config;

import top.beforedawn.models.bo.MyUser;
import top.beforedawn.util.HttpUtil;
import top.beforedawn.util.SingleEvent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户池
 *
 * @author 墨羽翎玖
 */
public class UserPool {
    /**
     * 池中最大用户数量
     */
    public static final int POOL_MAX = 200;

    /**
     * 过期时间（单位：分钟）
     */
    public static final int EXPIRATION_TIME = 1;

    public static ConcurrentHashMap<Long, MyUser> users = new ConcurrentHashMap<>();

    public static int size() {
        return users.size();
    }

    /**
     * 存储用户
     *
     * @param user 用户
     */
    public static void put(MyUser user) {
        users.put(user.getQq(), user);
    }

    /**
     * 删除用户
     *
     * @param qq QQ
     */
    public static void remove(Long qq) {
        users.remove(qq);
    }

    /**
     * 清空
     */
    public static void clear() {
        users = new ConcurrentHashMap<>();
    }

    /**
     * 清理过期的用户
     */
    public static void clearUser() {
        if (users.size() >= POOL_MAX) {
            ConcurrentHashMap<Long, MyUser> newUsers = new ConcurrentHashMap<>();
            for (Long key : users.keySet()) {
                MyUser user = users.get(key);
                if (user.getUpdateTime() != null && Duration.between(user.getUpdateTime(), LocalDateTime.now()).toMinutes() < EXPIRATION_TIME) {
                    newUsers.put(key, user);
                }
            }
            users = newUsers;
        }
    }

    /**
     * 获取用户
     *
     * @param singleEvent 事件
     * @return 用户
     */
    public static MyUser getUser(SingleEvent singleEvent) {
        MyUser user = users.getOrDefault(singleEvent.getSenderId(), new MyUser());
        // 如果过期或者不在池子内就自动获取用户
        if (user.getUpdateTime() == null || Duration.between(user.getUpdateTime(), LocalDateTime.now()).toMinutes() > EXPIRATION_TIME) {
            clearUser();
            user = HttpUtil.getUser(singleEvent);
            user.setUpdateTime(LocalDateTime.now());
            users.put(user.getQq(), user);
        }
        return user;
    }
}
