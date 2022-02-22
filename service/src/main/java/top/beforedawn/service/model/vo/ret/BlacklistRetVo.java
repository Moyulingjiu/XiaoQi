package top.beforedawn.service.model.vo.ret;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.beforedawn.service.model.bo.BlacklistType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistRetVo {
    private Long id;
    private Long key;
    private String comment;
    private Long remind;
    private LocalDateTime lastRemindTime;
    private Byte valid;
    private LocalDateTime modified;
    private Long modifiedId;
    private LocalDateTime create;
    private Long createId;
}
