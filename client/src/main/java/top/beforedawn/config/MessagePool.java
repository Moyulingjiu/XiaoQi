package top.beforedawn.config;

import net.mamoe.mirai.message.data.MessageChain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息缓存
 *
 * @author 墨羽翎玖
 */
public class MessagePool {
    private static final int VALID_MINUTE = 5;
    private static Map<Integer, MessageChain> cache = new HashMap<>();
    private static Map<Integer, LocalDateTime> cacheTime = new HashMap<>();
    private static LocalDateTime lastUpdate = LocalDateTime.now();

    /**
     * 清理过期的缓存
     */
    public static void clear() {
        if (Duration.between(lastUpdate, LocalDateTime.now()).toMinutes() <= VALID_MINUTE) {
            return;
        }
        Map<Integer, MessageChain> newCache = new HashMap<>();
        Map<Integer, LocalDateTime> newCacheTime = new HashMap<>();
        for (Integer key : cacheTime.keySet()) {
            if (Duration.between(cacheTime.get(key), LocalDateTime.now()).toMinutes() <= VALID_MINUTE) {
                newCache.put(key, cache.get(key));
                newCacheTime.put(key, cacheTime.get(key));
            }
        }
        lastUpdate = LocalDateTime.now();
        cache = newCache;
        cacheTime = newCacheTime;
    }

    /**
     * 放入缓存
     *
     * @param id    id号
     * @param chain 消息链
     */
    public static void put(int id, MessageChain chain) {
        clear();
        cache.put(id, chain);
        cacheTime.put(id, LocalDateTime.now());
    }

    /**
     * 获取时间
     *
     * @param id id号
     * @return 消息对应的时间
     */
    public static LocalDateTime getTime(int id) {
        return cacheTime.get(id);
    }

    /**
     * 获取消息
     *
     * @param id id号
     * @return 消息链
     */
    public static MessageChain get(int id) {
        MessageChain chain = cache.get(id);
        clear();
        return chain;
    }
}
