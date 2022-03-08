package top.beforedawn.service.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbstractVo {
    private String text;
    private Byte type;
    private Long userId;
    private Long botId;
}
