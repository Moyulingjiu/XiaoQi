package top.beforedawn.service.model.vo.ret;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.beforedawn.service.model.bo.KeyType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotRetVo {
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
    private String keyValue;
    private Long keyUserId;
    private LocalDateTime keyValidBeginDate;
    private LocalDateTime keyValidEndDate;
    private KeyType keyType;
    private Byte allowCoc;
    private Byte allowRpg;
    private Byte allowPic;
    private Byte allowAssistant;
    private LocalDateTime modified;
    private Long modifiedId;
    private LocalDateTime create;
    private Long createId;
}
