package top.beforedawn.models.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    Long remind; // 提醒次数
    LocalDateTime lastRemindTime; // 上次提醒
    Long createId; // 创建人
}
