package top.beforedawn.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import top.beforedawn.models.timed.*;
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
    public static final int POOL_MAX = 50;

    /**
     * 过期时间（单位：分钟）
     */
    public static final int EXPIRATION_TIME = 1;

    public static Map<Long, MyGroup> groups = new HashMap<>();

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


        bool = jsonObject.getBoolean("allowCopyAutoReply");
        group.setAllowCopyAutoReply(bool != null && bool);
        bool = jsonObject.getBoolean("selfReply");
        group.setSelfReply(bool != null && bool);
        bool = jsonObject.getBoolean("talk");
        group.setTalk(bool != null && bool);
        bool = jsonObject.getBoolean("swear");
        group.setSwear(bool != null && bool);
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

        // 屏蔽词
        JSONArray muteWordsJson = jsonObject.getJSONArray("muteWords");
        if (muteWordsJson != null) {
            for (int i = 0; i < muteWordsJson.size(); i++) {
                group.getMuteWords().add(muteWordsJson.getString(i));
            }
        }

        // 定时任务
        JSONArray timedMessages = jsonObject.getJSONArray("timedMessages");
        if (timedMessages != null) {
            for (int i = 0; i < timedMessages.size(); i++) {
//                group.getTimedMessages().add(timedMessages.getObject(i, GroupTimedMessage.class));
                JSONObject object = timedMessages.getJSONObject(i);
                GroupTimedMessage groupTimedMessage = new GroupTimedMessage();
                groupTimedMessage.setGroupId(object.getLong("groupId"));
                groupTimedMessage.setName(object.getString("name"));
                groupTimedMessage.setReply(object.getObject("reply", ArrayList.class));
                groupTimedMessage.setLastTime(object.getObject("lastTime", LocalDateTime.class));

                boolean flag = false;
                try {
                    groupTimedMessage.setChecker(object.getObject("checker", YearlyChecker.class));
                    if (groupTimedMessage.getChecker().valid())
                        flag = true;
                } catch (JSONException ignored) {
                }
                if (!flag) {
                    try {
                        groupTimedMessage.setChecker(object.getObject("checker", MonthlyChecker.class));
                        if (groupTimedMessage.getChecker().valid())
                            flag = true;
                    } catch (JSONException ignored) {
                    }
                }
                if (!flag) {
                    try {
                        groupTimedMessage.setChecker(object.getObject("checker", WeeklyChecker.class));
                        if (groupTimedMessage.getChecker().valid())
                            flag = true;
                    } catch (JSONException ignored) {
                    }
                }
                if (!flag) {
                    try {
                        groupTimedMessage.setChecker(object.getObject("checker", DailyChecker.class));
                        if (groupTimedMessage.getChecker().valid())
                            flag = true;
                    } catch (JSONException ignored) {
                    }
                }
                if (flag) {
                    group.getTimedMessages().add(groupTimedMessage);
                    // 不用担心重复注册
                    BackgroundTask.getInstance().put(groupTimedMessage);
                }
            }
        }

        // 自定义回复
        ArrayList<BaseAutoReply> autoReplies = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("autoReplies");
        for (int i = 0; i < jsonArray.size(); i++) {
            try {
                KeyReply reply = jsonArray.getObject(i, KeyReply.class);
                if (reply != null && reply.valid()) {
                    autoReplies.add(reply);
                    continue;
                }
            } catch (JSONException ignored) {

            }
            try {
                KeyMatchReply matchReply = jsonArray.getObject(i, KeyMatchReply.class);
                if (matchReply != null && matchReply.valid()) {
                    autoReplies.add(matchReply);
                    continue;
                }
            } catch (JSONException ignored) {

            }
            try {
                ComplexReply complexReply = jsonArray.getObject(i, ComplexReply.class);
                if (complexReply != null && complexReply.valid()) {
                    autoReplies.add(complexReply);
                }
            } catch (JSONException ignored) {

            }
        }
        group.setAutoReplies(autoReplies);

        Map<String, String> tunnel = jsonObject.getObject("tunnel", Map.class);
        if (tunnel != null)
            group.setTunnel(tunnel);

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

        jsonObject.put("allowCopyAutoReply", group.isAllowCopyAutoReply());
        jsonObject.put("selfReply", group.isSelfReply());
        jsonObject.put("talk", group.isTalk());
        jsonObject.put("swear", group.isSwear());
        jsonObject.put("autoReply", group.isAutoReply());
        jsonObject.put("autoReplies", group.getAutoReplies());
        jsonObject.put("timedMessages", group.getTimedMessages());
        jsonObject.put("repeat", group.isRepeat());
        jsonObject.put("coc", group.isCoc());
        jsonObject.put("driftingBottle", group.isDriftingBottle());
        jsonObject.put("rpg", group.isRpg());
        jsonObject.put("rpgLimit", group.isRpgLimit());
        jsonObject.put("muteWords", group.getMuteWords());

        jsonObject.put("welcome", group.isWelcome());
        jsonObject.put("welcomeMessage", group.getWelcomeMessage());

        jsonObject.put("tunnel", group.getTunnel());

        FileUtil.writeFile(filename, jsonObject.toJSONString());
    }
}
