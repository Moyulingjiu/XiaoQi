package top.beforedawn.util;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 环境
 *
 * @author 墨羽翎玖
 */
@Data
public class Context {
    private static Map<Long, Context> cache = new HashMap<>();

    public static Context getInstance(Long userId) {
        if (userId == null) {
            return null;
        }
        if (!cache.containsKey(userId)) {
            cache.put(userId, new Context(userId));
        }
        return cache.get(userId);
    }

    private Long userId;
    private Map<String, Object> context;

    public Context(Long userId) {
        this.userId = userId;
    }

    public Object get(String str) {
        return context.getOrDefault(str, null);
    }

    public void put(String str, Object obj) {
        context.put(str, obj);
    }
}
