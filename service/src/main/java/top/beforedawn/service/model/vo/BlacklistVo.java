package top.beforedawn.service.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistVo {
    private Long userId;
    private Long groupId;
    private Byte type;
    private Long key;
    private String comment;
}
