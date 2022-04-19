package top.beforedawn.models.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.beforedawn.config.GroupEntryRule;
import top.beforedawn.models.context.SerializeMessage;
import top.beforedawn.models.reply.BaseAutoReply;
import top.beforedawn.models.timed.GroupTimedMessage;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 群的抽象类
 *
 * @author 墨羽翎玖
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyGroup {
    private Long id = 0L;
    private String name = "";
    private LocalDateTime updateTime = LocalDateTime.now(); // 更新时间

    private boolean mute = false; // 禁言
    private boolean limit = false; // 处在限制模式的群
    private ArrayList<String> muteWords = new ArrayList<>(); // 屏蔽词

    // 插件相关的开关
    private boolean baseFunction = true; // 基础功能
    private boolean nudge = false; // 戳一戳
    private boolean unlockFlashImage = false; // 解除闪照
    private boolean recallGuard = false; // 防撤回
    private boolean memberWatcher = false; // 开启成员监控
    private boolean allowCopyAutoReply = false; // 允许复制自定义回复
    private boolean selfReply = true; // 智能回复
    private boolean talk = true; // 文摘、情话、笑话、恐怖故事
    private boolean swear = false; // 脏话
    private boolean autoReply = true; // 自定义回复（开关）
    private ArrayList<BaseAutoReply> autoReplies = new ArrayList<>(); // 自定义回复
    private ArrayList<GroupTimedMessage> timedMessages = new ArrayList<>(); // 定时消息
    private boolean repeat = false; // 自动加一
    private boolean coc = false; // 部落冲突查询
    private boolean driftingBottle = true; // 漂流瓶
    private boolean rpg = true; // RPG游戏
    private boolean rpgLimit = false; // RPG游戏限制模式

    private boolean groupEntry = false; // 自动审核入群
    private GroupEntryRule groupEntryRule = new GroupEntryRule(); // 审核规则

    private boolean welcome = false; // 入群欢迎
    private ArrayList<SerializeMessage> welcomeMessage = new ArrayList<>(); // 欢迎词

    private Map<String, String> tunnel = new HashMap<>(); // 指令隧穿

    public void add(BaseAutoReply autoReply) {
        autoReplies.add(autoReply);
    }

    public boolean timedMessageContains(String name) {
        for (GroupTimedMessage timedMessage : timedMessages) {
            if (timedMessage.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
