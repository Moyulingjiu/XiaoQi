package top.beforedawn.service.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * key的抽象类
 *
 * @author 墨羽翎玖
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Authorization {
    private Long id;
    private String value;
    private Long userId;
    private LocalDateTime validBeginDate;
    private LocalDateTime validEndDate;
    private KeyType type;
    private LocalDateTime modified;
    private Long modifiedId;
    private LocalDateTime create;
    private Long createId;
}
