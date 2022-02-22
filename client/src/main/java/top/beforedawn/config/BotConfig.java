package top.beforedawn.config;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import top.beforedawn.models.bo.Blacklist;
import top.beforedawn.models.bo.BotRemoteInformation;
import top.beforedawn.models.bo.SimpleBlacklist;
import top.beforedawn.models.bo.SystemRight;
import top.beforedawn.util.FileUtil;
import top.beforedawn.util.HttpUtil;
import top.beforedawn.util.SingleEvent;
import top.beforedawn.util.YamlReader;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * 机器人配置类
 *
 * @author 墨羽翎玖
 */
@Data
public class BotConfig {
    private final String configFilename = "config.yml";
    private YamlReader yamlReader;
    private LocalDateTime updateTime;

    private String workdir; // 工作路径
    private String cache; // 缓存路径

    private Long id; // id
    private String name; // 机器人名字
    private long qq; // QQ号
    private long officialGroup; // 官方群
    private String password; // 密码
    private Long keyId;
    private String keyValue;
    private Long keyUserId;
    private LocalDateTime keyValidBeginDate;
    private LocalDateTime keyValidEndDate;
    private String keyType;

    private long master; // 主人
    private ArrayList<Long> administrator = new ArrayList<>(); // 机器人管理员
    private ArrayList<Long> systemAdministrator = new ArrayList<>(); // 系统管理员
    private ArrayList<Long> systemSuperAdministrator = new ArrayList<>(); // 系统超级管理员

    private Blacklist blacklist = new Blacklist(); // 黑名单

    private BotSwitcher botSwitcher = new BotSwitcher(); // 所有开关

    private MessageStatistics statistics = new MessageStatistics(); // 消息统计

    /**
     * 构造函数，录入工作目录
     *
     * @param workdir the workdir
     */
    public BotConfig(String workdir) {
        setWorkdir(workdir);
        loadFile();
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
        master = botRemoteInformation.getMasterQq();
        keyId = botRemoteInformation.getKeyId();
        keyValue = botRemoteInformation.getKeyValue();
        keyUserId = botRemoteInformation.getKeyUserId();
        keyValidBeginDate = botRemoteInformation.getKeyValidBeginDate();
        keyValidEndDate = botRemoteInformation.getKeyValidEndDate();
        keyType = botRemoteInformation.getKeyType();

        // 读取配置信息
        botSwitcher.setAllowFriend(botRemoteInformation.getAllowFriend() != 0);
        botSwitcher.setAllowGroup(botRemoteInformation.getAllowGroup() != 0);
        botSwitcher.setHeart(botRemoteInformation.getHeart() != 0);
        botSwitcher.setHeartInterval(botRemoteInformation.getHeartInterval());

        botSwitcher.setRemindFriend(botRemoteInformation.getRemindFriend() != 0);
        botSwitcher.setRemindGroup(botRemoteInformation.getRemindGroup() != 0);
        botSwitcher.setRemindGroup(botRemoteInformation.getRemindGroup() != 0);
        botSwitcher.setRemindQuit(botRemoteInformation.getRemindQuit() != 0);
        botSwitcher.setClearBlacklist(botRemoteInformation.getClearBlacklist() != 0);

        Blacklist blacklist = HttpUtil.getBlacklist(singleEvent);
        if (blacklist != null) {
            this.blacklist = blacklist;
        }
    }

    /**
     * 加载本地数据
     */
    public void loadFile() {
        String content = FileUtil.readFile("");
        JSONObject jsonObject = JSONObject.parseObject(content);
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
        if (systemSuperAdministrator.contains(id)) {
            return SystemRight.SYSTEM_SUPER_ADMIN;
        } else if (systemAdministrator.contains(id)) {
            return SystemRight.SYSTEM_ADMIN;
        } else if (master == id) {
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
        // 官方群和主人不会被拉入私有黑名单
        if (userId == master || groupId == officialGroup) {
            return blacklist.isGlobalBlacklist(userId, groupId);
        }
        return blacklist.isBlacklist(userId, groupId) || blacklist.isGlobalBlacklist(userId, groupId);
    }

    /**
     * 是否为用户黑名单
     *
     * @param userId qq号
     * @return boolean
     */
    public boolean isUserBlacklist(Long userId) {
        if (userId == master) {
            return blacklist.isGlobalBlacklist(userId, 0L);
        }
        return blacklist.isBlacklist(userId, 0L) || blacklist.isGlobalBlacklist(userId, 0L);
    }

    /**
     * 是否为群黑名单
     *
     * @param groupId 群号
     * @return boolean
     */
    public boolean isGroupBlacklist(Long groupId) {
        if (groupId == officialGroup) {
            return blacklist.isGlobalBlacklist(0L, groupId);
        }
        return blacklist.isBlacklist(0L, groupId) || blacklist.isGlobalBlacklist(0L, groupId);
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

    public boolean isMute(Long id, Long groupId) {
        boolean ans = false;
        if (id != null && id != 0) {
            ans = ans || botSwitcher.getMuteFriend().contains(id);
        }
        if (groupId != null && groupId != 0) {
            ans = ans || botSwitcher.getMuteGroup().contains(groupId);
        }
        return ans;
    }
}
