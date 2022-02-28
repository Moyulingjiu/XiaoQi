package top.beforedawn.service.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotVo {
    private Long operator;
    private Long botId;
    private String name;
    private Integer allowFriend;
    private Integer allowGroup;
    private Integer heart;
    private Integer heartInterval;
    private Integer remindFriend;
    private Integer remindGroup;
    private Integer remindMute;
    private Integer remindQuit;
    private Integer clearBlacklist;
}
