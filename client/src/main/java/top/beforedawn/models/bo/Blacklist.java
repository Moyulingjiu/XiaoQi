package top.beforedawn.models.bo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 黑名单类
 * <p>
 * 名单中不允许出现有id为0的黑名单
 *
 * @author 墨羽翎玖
 */
@Getter
public class Blacklist {
    Map<Long, SimpleBlacklist> groups = new HashMap<>();
    Map<Long, SimpleBlacklist> users = new HashMap<>();
    Map<Long, SimpleBlacklist> globalGroups = new HashMap<>();
    Map<Long, SimpleBlacklist> globalUsers = new HashMap<>();

    public void appendGroup(SimpleBlacklist group) {
        if (group != null && group.getKey() != null && group.getKey() != 0L) {
            groups.put(group.getKey(), group);
        }
    }

    public void appendUser(SimpleBlacklist user) {
        if (user != null && user.getKey() != null && user.getKey() != 0L) {
            users.put(user.getKey(), user);
        }
    }

    public void appendGlobalGroup(SimpleBlacklist group) {
        if (group != null && group.getKey() != null && group.getKey() != 0L) {
            globalGroups.put(group.getKey(), group);
        }
    }

    public void appendGlobalUser(SimpleBlacklist user) {
        if (user != null && user.getKey() != null && user.getKey() != 0L) {
            globalUsers.put(user.getKey(), user);
        }
    }

    public boolean isBlacklist(Long userId, Long groupId) {
        return users.containsKey(userId) || groups.containsKey(groupId);
    }

    public boolean isGlobalBlacklist(Long userId, Long groupId) {
        return globalUsers.containsKey(userId) || globalGroups.containsKey(groupId);
    }

    public SimpleBlacklist getUser(Long id) {
        return users.get(id);
    }

    public SimpleBlacklist getGroup(Long id) {
        return groups.get(id);
    }

    public SimpleBlacklist getGlobalUser(Long id) {
        return globalUsers.get(id);
    }

    public SimpleBlacklist getGlobalGroup(Long id) {
        return globalGroups.get(id);
    }
}
