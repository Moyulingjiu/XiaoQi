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

    private boolean allowFriend = true; // 允许自动添加好友
    private boolean allowGroup = true; // 允许自动加群
    private boolean remindFriend = true; // 新好友提醒
    private boolean remindGroup = true; // 新群提醒
    private boolean remindMute = true; // 禁言提醒
    private boolean remindQuit = true; // 退群提醒

    private boolean heart = true; // 是否进行心跳
    private int heartInterval = 6; // 心跳报时间隔
}
