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

    private static String showBlacklist(Map<Long, SimpleBlacklist> map, int page, int pageSize) {
        StringBuilder builder = new StringBuilder();
        boolean init = false;
        if (map.size() == 0) {
            return "暂无";
        }

        int total = map.keySet().size();
        if (total % pageSize != 0) total = total / pageSize + 1;
        else total = total / pageSize;
        if (page >= total || page < 0) return "页码超限";

        int index = 0;
        for (Long key : map.keySet()) {
            if (index < page * pageSize) continue;
            else if (index >= (page + 1) * pageSize) break;
            index++;
            SimpleBlacklist simpleBlacklist = map.get(key);
            if (!init) {
                init = true;
            } else {
                builder.append("\n");
            }
            builder.append(index).append("、");
            builder.append(simpleBlacklist.toString());
        }
        builder.append("\n--------\n").append("页码：").append(page + 1).append("/").append(total);
        return builder.toString();
    }

    public String showUser(int page, int pageSize) {
        return showBlacklist(users, page, pageSize);
    }

    public String showGroup(int page, int pageSize) {
        return showBlacklist(groups, page, pageSize);
    }

    public String showGlobalUser(int page, int pageSize) {
        return showBlacklist(globalUsers, page, pageSize);
    }

    public String showGlobalGroup(int page, int pageSize) {
        return showBlacklist(globalGroups, page, pageSize);
    }
}
