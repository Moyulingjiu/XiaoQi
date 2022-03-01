package top.beforedawn.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import top.beforedawn.models.bo.MyGroup;
import top.beforedawn.models.context.SerializeMessage;
import top.beforedawn.models.reply.BaseAutoReply;
import top.beforedawn.models.reply.ComplexReply;
import top.beforedawn.models.reply.KeyMatchReply;
import top.beforedawn.models.reply.KeyReply;
import top.beforedawn.util.FileUtil;
import top.beforedawn.util.SingleEvent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 群池
 *
 * @author 墨羽翎玖
 */
public class GroupPool {
    /**
     * 池中最大群数量
     */
    public static final int POOL_MAX = 100;

    /**
     * 过期时间（单位：分钟）
     */
    public static final int EXPIRATION_TIME = 1;

    private static Map<Long, MyGroup> groups = new HashMap<>();

    public static int size() {
        return groups.size();
    }

    /**
     * 存储群
     *
     * @param group 群
     */
    public static void put(MyGroup group) {
        groups.put(group.getId(), group);
    }

    /**
     * 清理对应的群
     *
     * @param groupId 群号
     */
    public static void remove(Long groupId) {
        groups.remove(groupId);
    }

    /**
     * 自动清除太久没有使用的群
     */
    public static void clearGroup() {
        if (groups.size() >= POOL_MAX) {
            Map<Long, MyGroup> newGroups = new HashMap<>();
            for (Long key : groups.keySet()) {
                MyGroup group = groups.get(key);
                if (group.getUpdateTime() != null && Duration.between(group.getUpdateTime(), LocalDateTime.now()).toMinutes() < EXPIRATION_TIME) {
                    newGroups.put(key, group);
                }
            }
            groups = newGroups;
        }
    }

    public static MyGroup get(SingleEvent singleEvent) {
        MyGroup group = groups.get(singleEvent.getGroupId());
        if (group == null) {
            group = load(singleEvent, singleEvent.getGroupId());
            group.setId(singleEvent.getGroupId());
        }
        clearGroup();
        group.setUpdateTime(LocalDateTime.now());
        if (group.getId() > 0L) {
            groups.put(group.getId(), group);
        }
        return group;
    }

    public static MyGroup load(SingleEvent singleEvent, Long groupId) {
        if (groupId == null || groupId <= 0L) {
            return new MyGroup();
        }
        String filename = singleEvent.getConfig().getWorkdir() + singleEvent.getConfig().getGroupFilename().replace("{groupId}", "" + groupId);
        String content = FileUtil.readFile(filename);
        if (content.equals("")) {
            MyGroup group = new MyGroup();
            group.setId(groupId);
            return group;
        }
        MyGroup group = new MyGroup();
        JSONObject jsonObject = JSONObject.parseObject(content);
        group.setId(jsonObject.getLong("id"));
        group.setName(jsonObject.getString("name"));

        Boolean bool = jsonObject.getBoolean("mute");
        group.setMute(bool != null && bool);
        bool = jsonObject.getBoolean("limit");
        group.setLimit(bool != null && bool);
        bool = jsonObject.getBoolean("nudge");
        group.setNudge(bool != null && bool);
        bool = jsonObject.getBoolean("unlockFlashImage");
        group.setUnlockFlashImage(bool != null && bool);
        bool = jsonObject.getBoolean("recallGuard");
        group.setRecallGuard(bool != null && bool);
        bool = jsonObject.getBoolean("memberWatcher");
        group.setMemberWatcher(bool != null && bool);
        bool = jsonObject.getBoolean("groupEntry");
        group.setGroupEntry(bool != null && bool);
        group.setGroupEntryRule(jsonObject.getObject("groupEntryRule", GroupEntryRule.class));

        bool = jsonObject.getBoolean("autoReply");
        group.setAutoReply(bool != null && bool);
        bool = jsonObject.getBoolean("repeat");
        group.setRepeat(bool != null && bool);
        bool = jsonObject.getBoolean("coc");
        group.setCoc(bool != null && bool);
        bool = jsonObject.getBoolean("driftingBottle");
        group.setDriftingBottle(bool != null && bool);
        bool = jsonObject.getBoolean("rpg");
        group.setRpg(bool != null && bool);
        bool = jsonObject.getBoolean("rpgLimit");
        group.setRpgLimit(bool != null && bool);
        bool = jsonObject.getBoolean("welcome");
        group.setWelcome(bool != null && bool);
        JSONArray welcomeMessage = jsonObject.getJSONArray("welcomeMessage");
        if (welcomeMessage != null) {
            for (int i = 0; i < welcomeMessage.size(); i++) {
                group.getWelcomeMessage().add(welcomeMessage.getObject(i, SerializeMessage.class));
            }
        }

        JSONArray muteWordsJson = jsonObject.getJSONArray("muteWords");
        if (muteWordsJson != null) {
            for (int i = 0; i < muteWordsJson.size(); i++) {
                group.getMuteWords().add(muteWordsJson.getString(i));
            }
        }

        ArrayList<BaseAutoReply> autoReplies = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("autoReplies");
        for (int i = 0; i < jsonArray.size(); i++) {
            if (jsonArray.getObject(i, BaseAutoReply.class) != null) {
                autoReplies.add(jsonArray.getObject(i, BaseAutoReply.class));
            } else if (jsonArray.getObject(i, ComplexReply.class) != null) {
                autoReplies.add(jsonArray.getObject(i, ComplexReply.class));
            } else if (jsonArray.getObject(i, KeyReply.class) != null) {
                autoReplies.add(jsonArray.getObject(i, KeyReply.class));
            } else if (jsonArray.getObject(i, KeyMatchReply.class) != null) {
                autoReplies.add(jsonArray.getObject(i, KeyMatchReply.class));
            }
        }
        group.setAutoReplies(autoReplies);

        return group;
    }

    public static void save(SingleEvent singleEvent) {
        if (groups.containsKey(singleEvent.getGroupId()) && singleEvent.isGroupMessage()) {
            MyGroup group = groups.get(singleEvent.getGroupId());
            group.setName(singleEvent.getGroupMessageEvent().getGroup().getName());
        }
        save(singleEvent, singleEvent.getGroupId());
    }

    public static void save(SingleEvent singleEvent, Long groupId) {
        if (groupId == null || groupId <= 0L) {
            return;
        }
        String filename = singleEvent.getConfig().getWorkdir() + singleEvent.getConfig().getGroupFilename().replace("{groupId}", "" + groupId);
        MyGroup group = groups.getOrDefault(groupId, new MyGroup());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", group.getId());
        jsonObject.put("name", group.getName());

        jsonObject.put("mute", group.isMute());
        jsonObject.put("limit", group.isLimit());
        jsonObject.put("nudge", group.isNudge());
        jsonObject.put("unlockFlashImage", group.isUnlockFlashImage());
        jsonObject.put("recallGuard", group.isRecallGuard());
        jsonObject.put("memberWatcher", group.isMemberWatcher());
        jsonObject.put("groupEntry", group.isGroupEntry());
        jsonObject.put("groupEntryRule", group.getGroupEntryRule());

        jsonObject.put("autoReply", group.isAutoReply());
        jsonObject.put("autoReplies", group.getAutoReplies());
        jsonObject.put("repeat", group.isRepeat());
        jsonObject.put("coc", group.isCoc());
        jsonObject.put("driftingBottle", group.isDriftingBottle());
        jsonObject.put("rpg", group.isRpg());
        jsonObject.put("rpgLimit", group.isRpgLimit());
        jsonObject.put("muteWords", group.getMuteWords());

        jsonObject.put("welcome", group.isWelcome());
        jsonObject.put("welcomeMessage", group.getWelcomeMessage());

        FileUtil.writeFile(filename, jsonObject.toJSONString());
    }
}
