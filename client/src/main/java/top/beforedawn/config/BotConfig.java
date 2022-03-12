package top.beforedawn.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import top.beforedawn.models.bo.*;
import top.beforedawn.util.FileUtil;
import top.beforedawn.util.HttpUtil;
import top.beforedawn.util.SingleEvent;
import top.beforedawn.util.YamlReader;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

/**
 * 机器人配置类
 *
 * @author 墨羽翎玖
 */
@Data
public class BotConfig {
    /**
     * 过期时间（单位：分钟）
     */
    public static final int EXPIRATION_TIME = 1;

    /**
     * 版本信息
     */
    public static final String VERSION = "1.0.0-beta";

    /**
     * 每分钟最大的消息数量
     */
    public static final int MAX_MESSAGE = 60;

    private final String configFilename = "config.json";
    private final String statisticsFilename = "statistics.json";
    private final String groupFilename = "group/{groupId}.json";

    private YamlReader yamlReader;
    private LocalDateTime updateTime; // 上一次更新的时间
    private LocalDateTime saveTime; // 上一保存的时间

    private String globalWorkdir; // 全局工作路径
    private String workdir; // 工作路径
    private String cache; // 缓存路径

    private Long id; // id
    private String name; // 机器人名字
    private long qq; // QQ号
    private Long officialGroup; // 官方群
    private String password; // 密码
    private Long keyId;
    private String keyValue;
    private Long keyUserId;
    private LocalDateTime keyValidBeginDate;
    private LocalDateTime keyValidEndDate;
    private String keyType;
    private boolean allowCoc; // 允许使用coc模块（部落冲突）
    private boolean allowRpg; // 允许使用rpg模块（rpg游戏）
    private boolean allowTrpg; // 允许使用trpg模块【还未制作】
    private boolean allowPic; // 允许使用pic模块（搜图）
    private boolean allowAssistant; // 允许使用个人助理模块

    private long master; // 主人
    private ArrayList<Long> administrator = new ArrayList<>(); // 机器人管理员

    private Blacklist blacklist = new Blacklist(); // 黑名单

    private BotSwitcher botSwitcher = new BotSwitcher(); // 所有开关

    private MessageStatistics statistics = new MessageStatistics(); // 消息统计

    /**
     * 构造函数，录入工作目录
     * 其真实工作目录为：“工作目录/QQ号/”
     *
     * @param workdir the workdir
     */
    public BotConfig(String workdir, Long botId) {
        setWorkdir(workdir);
        globalWorkdir = this.workdir;
        this.workdir += botId + "/";
        cache = this.workdir + "cache";
        // 1、加载本地的数据
        loadFile();
        // 2、加载云端的数据
        SingleEvent singleEvent = new SingleEvent();
        singleEvent.setBotId(botId);
        update(singleEvent);
    }

    /**
     * 更新远程的数据
     */
    public void update(SingleEvent singleEvent) {
        updateTime = LocalDateTime.now();
        BotRemoteInformation botRemoteInformation = HttpUtil.getBot(singleEvent);
        if (botRemoteInformation == null) {
            return;
        }
        id = botRemoteInformation.getId();
        qq = botRemoteInformation.getQq();
        password = botRemoteInformation.getPassword();
        name = botRemoteInformation.getName();
        officialGroup = botRemoteInformation.getOfficialGroup();
        master = botRemoteInformation.getMasterQq();
        keyId = botRemoteInformation.getKeyId();
        keyValue = botRemoteInformation.getKeyValue();
        keyUserId = botRemoteInformation.getKeyUserId();
        keyValidBeginDate = botRemoteInformation.getKeyValidBeginDate();
        keyValidEndDate = botRemoteInformation.getKeyValidEndDate();
        keyType = botRemoteInformation.getKeyType();
        allowCoc = botRemoteInformation.getAllowCoc() == 1;
        allowTrpg = botRemoteInformation.getAllowTrpg() == 1;
        allowRpg = botRemoteInformation.getAllowRpg() == 1;
        allowPic = botRemoteInformation.getAllowPic() == 1;
        allowAssistant = botRemoteInformation.getAllowAssistant() == 1;

        // 读取配置信息
        botSwitcher.setAllowFriend(botRemoteInformation.getAllowFriend() == 1);
        botSwitcher.setAllowGroup(botRemoteInformation.getAllowGroup() == 1);
        botSwitcher.setHeart(botRemoteInformation.getHeart() == 1);
        botSwitcher.setHeartInterval(botRemoteInformation.getHeartInterval());

        botSwitcher.setRemindFriend(botRemoteInformation.getRemindFriend() == 1);
        botSwitcher.setRemindGroup(botRemoteInformation.getRemindGroup() == 1);
        botSwitcher.setRemindGroup(botRemoteInformation.getRemindGroup() == 1);
        botSwitcher.setRemindQuit(botRemoteInformation.getRemindQuit() == 1);
        botSwitcher.setClearBlacklist(botRemoteInformation.getClearBlacklist() == 1);

        // 读取黑名单
        Blacklist blacklist = HttpUtil.getBlacklist(singleEvent);
        if (blacklist != null) {
            this.blacklist.setGlobalUsers(blacklist.getGlobalUsers());
            this.blacklist.setGlobalGroups(blacklist.getGlobalGroups());
        }
    }

    /**
     * 保存到本地文件
     */
    public void save() {
        saveTime = LocalDateTime.now();
        statistics.save(workdir + statisticsFilename);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("master", master);
        jsonObject.put("admin", administrator);
        jsonObject.put("officialGroup", officialGroup);

        JSONObject blacklist = new JSONObject();
        ArrayList<SimpleBlacklist> users = new ArrayList<>();
        for (Long key : this.blacklist.getUsers().keySet()) {
            users.add(this.blacklist.getUsers().get(key));
        }
        blacklist.put("user", users);
        ArrayList<SimpleBlacklist> groups = new ArrayList<>();
        for (Long key : this.blacklist.getGroups().keySet()) {
            users.add(this.blacklist.getGroups().get(key));
        }
        blacklist.put("group", groups);
        jsonObject.put("blacklist", blacklist);

        // 全局开关
        jsonObject.put("switcher", botSwitcher);

        //写入到JSON文件
        FileUtil.writeFile(workdir + configFilename, jsonObject.toJSONString());
    }

    /**
     * 加载本地数据
     */
    public void loadFile() {
        saveTime = LocalDateTime.now();
        statistics.load(workdir + statisticsFilename);
        String content = FileUtil.readFile(workdir + configFilename);
        if (content.equals("")) {
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(content);

        // 读取主人和管理员
        JSONArray jsonAdmin = jsonObject.getJSONArray("admin");
        for (int i = 0; i < jsonAdmin.size(); i++) {
            try {
                administrator.add(jsonAdmin.getLong(i));
            }  catch (JSONException ignored) {

            }
        }


        // 读取黑名单
        for (Object user : jsonObject.getJSONObject("blacklist").getJSONArray("user")) {
            SimpleBlacklist simpleBlacklist = HttpUtil.analysisBlacklist((JSONObject) user);
            blacklist.appendUser(simpleBlacklist);
        }
        for (Object group : jsonObject.getJSONObject("blacklist").getJSONArray("group")) {
            SimpleBlacklist simpleBlacklist = HttpUtil.analysisBlacklist((JSONObject) group);
            blacklist.appendGroup(simpleBlacklist);
        }
    }

    /**
     * 设置工作目录
     *
     * @param workdir 工作目录
     */
    public void setWorkdir(String workdir) {
        this.workdir = workdir.replace("\\", "/");
        if (!this.workdir.endsWith("/")) {
            this.workdir += "/";
        }
    }

    /**
     * 获取用户的权限
     *
     * @param id 用户的qq号
     * @return 权限
     */
    public SystemRight checkRight(Long id) {
        if (id == null) {
            return SystemRight.MEMBER;
        }
        // 权限由上往下覆盖。比如一个人是系统超级管理员，就算是某个机器人的主人那也是返回系统超级管理员
        SingleEvent singleEvent = new SingleEvent();
        singleEvent.setSenderId(id);
        singleEvent.setBotId(qq);
        MyUser user = UserPool.getUser(singleEvent);
        if (user.getRight() != null) {
            if (user.getRight().equals("SUPER_ADMIN")) {
                return SystemRight.SYSTEM_SUPER_ADMIN;
            } else if (user.getRight().equals("ADMIN")) {
                return SystemRight.SYSTEM_ADMIN;
            }
        }
        if (master == id) {
            return SystemRight.MASTER;
        } else if (administrator.contains(id)) {
            return SystemRight.ADMIN;
        }
        return SystemRight.MEMBER;
    }

    /**
     * 判断是否是黑名单
     *
     * @param userId  用户qq号
     * @param groupId 群号
     * @return boolean
     */
    public boolean isBlacklist(Long userId, Long groupId) {
        if (blacklist.isBlacklist(userId, 0L, checkRight(userId))) {
            return true;
        }
        // 官方群和主人不会被拉入私有黑名单
        if (userId == master || Objects.equals(groupId, officialGroup)) {
            return blacklist.isGlobalBlacklist(userId, groupId, checkRight(userId));
        }
        return blacklist.isBlacklist(userId, groupId, checkRight(userId)) || blacklist.isGlobalBlacklist(userId, groupId, checkRight(userId));
    }

    /**
     * 是否为用户黑名单
     *
     * @param userId qq号
     * @return boolean
     */
    public boolean isUserBlacklist(Long userId) {
        if (userId == master) {
            return blacklist.isGlobalBlacklist(userId, 0L, checkRight(userId));
        }
        return blacklist.isBlacklist(userId, 0L, checkRight(userId)) || blacklist.isGlobalBlacklist(userId, 0L, checkRight(userId));
    }

    /**
     * 是否为群黑名单
     *
     * @param groupId 群号
     * @return boolean
     */
    public boolean isGroupBlacklist(Long groupId) {
        if (groupId == officialGroup) {
            return blacklist.isGlobalBlacklist(0L, groupId, SystemRight.MEMBER);
        }
        return blacklist.isBlacklist(0L, groupId, SystemRight.MEMBER) || blacklist.isGlobalBlacklist(0L, groupId, SystemRight.MEMBER);
    }

    public SimpleBlacklist getBlacklistUser(Long id) {
        if (id == master) {
            return null;
        }
        return blacklist.getUser(id);
    }

    public SimpleBlacklist getBlacklistGroup(Long id) {
        if (id == officialGroup) {
            return null;
        }
        return blacklist.getGroup(id);
    }

    public SimpleBlacklist getBlacklistGlobalUser(Long id) {
        return blacklist.getGlobalUser(id);
    }

    public SimpleBlacklist getBlacklistGlobalGroup(Long id) {
        return blacklist.getGlobalGroup(id);
    }

    public boolean isMute(SingleEvent singleEvent) {
        MyGroup group = GroupPool.get(singleEvent);
        if (group != null) {
            return group.isMute();
        }
        return false;
    }

    public boolean isLimit(SingleEvent singleEvent) {
        MyGroup group = GroupPool.get(singleEvent);
        if (group != null) {
            return group.isLimit();
        }
        return false;
    }
}
