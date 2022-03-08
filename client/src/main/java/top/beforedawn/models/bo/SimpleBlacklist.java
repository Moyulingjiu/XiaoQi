package top.beforedawn.models.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 简单黑名单类
 *
 * @author 墨羽翎玖
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleBlacklist {
    Long key; // 主键
    String comment; // 备注
    Long remind = 0L; // 提醒次数
    LocalDateTime lastRemindTime; // 上次提醒
    Long createId; // 创建人
    LocalDateTime create; // 创建时间
    Long modifiedId; // 创建人
    LocalDateTime modified; // 创建时间

    public boolean needRemind() {
        boolean isRemind = true;
        if (lastRemindTime != null) {
            isRemind = false;
            Duration duration = Duration.between(lastRemindTime, LocalDateTime.now());
            long days = duration.toDays();
            // 间隔天数大于1天，并且最多提醒十次
            if (days >= 1 && remind < 10L) {
                isRemind = true;
            }
        }
        if (isRemind) {
            remind++;
            lastRemindTime = LocalDateTime.now();
        }
        return isRemind;
    }

    @Override
    public String toString() {
        return String.format("%d[%s]", key, comment);
    }
}
