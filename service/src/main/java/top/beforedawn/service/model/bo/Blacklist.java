package top.beforedawn.service.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 黑名单实体类
 *
 * @author 墨羽翎玖
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Blacklist {
    private Long id;
    private Long key;
    private BlacklistType type;
    private String comment;
    private Long remind;
    private LocalDateTime lastRemindTime;
    private Byte valid;
    private LocalDateTime modified;
    private Long modifiedId;
    private LocalDateTime create;
    private Long createId;
}
