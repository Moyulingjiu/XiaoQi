package top.beforedawn.config;

import lombok.Getter;
import top.beforedawn.models.bo.Blacklist;
import top.beforedawn.models.bo.SimpleBlacklist;
import top.beforedawn.models.bo.SystemRight;
import top.beforedawn.util.CommonUtil;
import top.beforedawn.util.ConfigUtil;
import top.beforedawn.util.YamlReader;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 机器人配置类
 * @author 墨羽翎玖
 */
@Getter
public class BotConfig {
    private final String configFilename = "config.yml";
    private YamlReader yamlReader;

    private String name; // 机器人名字
    private long qq; // QQ号
    private long master; // 主人
    private long officialGroup; // 官方群
    private String password; // 密码
    private String workdir; // 工作路径
    private String cache; // 缓存路径
    private Blacklist memberBlacklist; // 成员黑名单
    private Blacklist groupBlacklist; // 群黑名单
    private ArrayList<Long> administrator = new ArrayList<>(); // 机器人管理员
    private ArrayList<Long> systemAdministrator = new ArrayList<>(); // 系统管理员
    private ArrayList<Long> systemSuperAdministrator = new ArrayList<>(); // 系统超级管理员
    private final BotSwitcher botSwitcher = new BotSwitcher();

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
     * 加载数据
     */
    private void loadFile() {
        yamlReader = new YamlReader(workdir + configFilename);
        Object obj;
        obj = yamlReader.getValueByKey("name");
        if (obj != null) {
            name = (String) obj;
        }
        obj = yamlReader.getValueByKey("qq");
        if (obj != null) {
            qq = (Long) obj;
        }
        obj = yamlReader.getValueByKey("master");
        if (obj != null) {
            master = (Long) obj;
        }
        obj = yamlReader.getValueByKey("official_group");
        if (obj != null) {
            officialGroup = (Long) obj;
        }
        obj = yamlReader.getValueByKey("password");
        if (obj != null) {
            password = (String) obj;
        }
        obj = yamlReader.getValueByKey("cache");
        if (obj != null) {
            cache = (String) obj;
        }
        obj = yamlReader.getValueByKey("blacklist_member");
        if (obj != null) {
            memberBlacklist = ConfigUtil.analysisBlacklist((ArrayList<Object>) obj);
        }
        obj = yamlReader.getValueByKey("blacklist_group");
        if (obj != null) {
            groupBlacklist = ConfigUtil.analysisBlacklist((ArrayList<Object>) obj);
        }
        obj = yamlReader.getValueByKey("admin");
        if (obj != null) {
            administrator = CommonUtil.decorateArrayList((ArrayList<Object>) obj);
        }
        obj = yamlReader.getValueByKey("system_administrator");
        if (obj != null) {
            systemAdministrator = CommonUtil.decorateArrayList((ArrayList<Object>) obj);
        }
        obj = yamlReader.getValueByKey("system_super_administrator");
        if (obj != null) {
            systemSuperAdministrator = CommonUtil.decorateArrayList((ArrayList<Object>) obj);
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

    public SystemRight checkRight(Long id) {
        if (id == null) {
            return SystemRight.MEMBER;
        }
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
}
