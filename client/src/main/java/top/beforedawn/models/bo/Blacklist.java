package top.beforedawn.models.bo;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 黑名单类
 * <p>
 * 名单中不允许出现有id为0的黑名单
 *
 * @author 墨羽翎玖
 */
@Data
public class Blacklist {
    Map<Long, SimpleBlacklist> groups = new HashMap<>();
    Map<Long, SimpleBlacklist> users = new HashMap<>();
    Map<Long, SimpleBlacklist> globalGroups = new HashMap<>();
    Map<Long, SimpleBlacklist> globalUsers = new HashMap<>();

    public boolean removeUser(Long userId) {
        if (!users.containsKey(userId)) {
            return false;
        }
        users.remove(userId);
        return true;
    }

    public boolean removeGroup(Long groupId) {
        if (!groups.containsKey(groupId)) {
            return false;
        }
        groups.remove(groupId);
        return true;
    }

    public boolean removeGlobalUser(Long userId) {
        if (!globalUsers.containsKey(userId)) {
            return false;
        }
        globalUsers.remove(userId);
        return true;
    }

    public boolean removeGlobalGroup(Long groupId) {
        if (!globalGroups.containsKey(groupId)) {
            return false;
        }
        globalGroups.remove(groupId);
        return true;
    }

    public void appendGroup(SimpleBlacklist group) {
        if (group != null && group.getKey() != null && group.getKey() > 0L) {
            groups.put(group.getKey(), group);
        }
    }

    public void appendUser(SimpleBlacklist user) {
        if (user != null && user.getKey() != null && user.getKey() > 0L) {
            users.put(user.getKey(), user);
        }
    }

    public void appendGlobalGroup(SimpleBlacklist group) {
        if (group != null && group.getKey() != null && group.getKey() > 0L) {
            globalGroups.put(group.getKey(), group);
        }
    }

    public void appendGlobalUser(SimpleBlacklist user) {
        if (user != null && user.getKey() != null && user.getKey() > 0L) {
            globalUsers.put(user.getKey(), user);
        }
    }

    public boolean isBlacklist(Long userId, Long groupId, SystemRight right) {
        if (right != SystemRight.SYSTEM_SUPER_ADMIN && right != SystemRight.SYSTEM_ADMIN && right != SystemRight.MASTER)
            return users.containsKey(userId) || groups.containsKey(groupId);
        return false;
    }

    public boolean isGlobalBlacklist(Long userId, Long groupId, SystemRight right) {
        if (right != SystemRight.SYSTEM_SUPER_ADMIN)
            return globalUsers.containsKey(userId) || globalGroups.containsKey(groupId);
        return false;
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

    private static String showBlacklist(Map<Long, SimpleBlacklist> map) {
        StringBuilder builder = new StringBuilder();
        boolean init = false;
        if (map.size() == 0) {
            return "暂无";
        }
        for (Long key : map.keySet()) {
            SimpleBlacklist simpleBlacklist = map.get(key);
            if (!init) {
                init = true;
            } else {
                builder.append("\n");
            }
            builder.append(simpleBlacklist.toString());
        }
        return builder.toString();
    }

    public String showUser() {
        return showBlacklist(users);
    }

    public String showGroup() {
        return showBlacklist(groups);
    }

    public String showGlobalUser() {
        return showBlacklist(globalUsers);
    }

    public String showGlobalGroup() {
        return showBlacklist(globalGroups);
    }
}
