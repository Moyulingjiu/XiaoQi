package top.beforedawn.config;

import top.beforedawn.models.context.Context;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 上下文环境缓存池
 *
 * @author 墨羽翎玖
 */
public class ContextPool {
    private static ConcurrentHashMap<Long, Context> pool = new ConcurrentHashMap<>();

    public static void clear() {
        ConcurrentHashMap<Long, Context> newPool = new ConcurrentHashMap<>();
        for (Long key : pool.keySet()) {
            if (!pool.get(key).isOver())
                newPool.put(key, pool.get(key));
        }
        pool = newPool;
    }

    public static Context get(long userId) {
        return pool.get(userId);
    }

    public static void remove(long userId) {
        pool.remove(userId);
    }

    public static boolean contains(long userId) {
        return pool.containsKey(userId);
    }

    public static void put(Long userId, Context context) {
        if (userId == null || userId <= 0L) return;
        pool.put(userId, context);
    }
}
