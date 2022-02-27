package top.beforedawn.models.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.beforedawn.config.GroupEntryRule;
import top.beforedawn.models.reply.BaseAutoReply;

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
    private LocalDateTime updateTime; // 更新时间

    private boolean mute = false; // 禁言
    private boolean limit = false; // 处在限制模式的群

    private boolean nudge = false; // 戳一戳
    private boolean unlockFlashImage = false; // 解除闪照
    private boolean recallGuard = false; // 防撤回
    private boolean memberWatcher = false; // 开启成员监控

    private boolean autoReply = false; // 自定义回复（开关）
    private ArrayList<BaseAutoReply> autoReplies = new ArrayList<>(); // 自定义回复
    private boolean repeat = false; // 自动加一

    private boolean groupEntry = false; // 自动审核入群
    private GroupEntryRule groupEntryRule = new GroupEntryRule(); // 审核规则

    public void add(BaseAutoReply autoReply) {
        autoReplies.add(autoReply);
    }
}
