package top.beforedawn.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 机器人的开关
 *
 * @author 墨羽翎玖
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotSwitcher {
    private boolean clearBlacklist; // 自动退出（删除）黑名单群（好友）

    private boolean allowFriend; // 允许自动添加好友
    private boolean allowGroup; // 允许自动加群
    private boolean remindFriend; // 新好友提醒
    private boolean remindGroup; // 新群提醒
    private boolean remindMute = true; // 禁言提醒
    private boolean remindQuit; // 退群提醒

    private boolean heart; // 是否进行心跳
    private int heartInterval; // 心跳报时间隔

    private Set<Long> muteGroup = new HashSet<>(); // 所有禁言的群
    private Set<Long> muteFriend = new HashSet<>(); // 所有禁言的人
    private Set<Long> limit = new HashSet<>(); // 处在限制模式的群

    private Set<Long> unlockFlashImage = new HashSet<>(); // 解除闪照的群
    private Set<Long> recallGuard = new HashSet<>(); // 防撤回的群
    private Set<Long> memberWatcher = new HashSet<>(); // 开启成员监控的群

    private Map<Long, GroupEntryRule> groupEntryRuleMap = new HashMap<>(); // 自动审核入群
}
