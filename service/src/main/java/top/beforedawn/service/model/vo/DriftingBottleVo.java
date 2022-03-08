package top.beforedawn.service.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriftingBottleVo {
    private Long botId;
    private Long userId;
    private String text;
    private Byte permanent;
}
