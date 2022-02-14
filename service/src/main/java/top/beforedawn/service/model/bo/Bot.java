package top.beforedawn.service.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 机器人抽象类
 *
 * @author 墨羽翎玖
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bot {
    private Long id;
    private Long qq;
    private String password;
    private String name;
    private Long masterQq;
    private Long keyId;
    private Boolean valid; // 是否还是一个有效的机器人
    private Integer allowFriend;
    private Integer allowGroup;
    private Integer heart;
    private Integer heartInterval;
    private Integer remindFriend;
    private Integer remindGroup;
    private Integer remindMute;
    private Integer remindQuit;
    private Integer clearBlacklist;
    private LocalDateTime modified;
    private Long modifiedId;
    private LocalDateTime create;
    private Long createId;
}
