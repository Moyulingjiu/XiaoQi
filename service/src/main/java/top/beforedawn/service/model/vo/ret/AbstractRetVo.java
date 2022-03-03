package top.beforedawn.service.model.vo.ret;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.beforedawn.service.model.bo.AbstractType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbstractRetVo {
    private Long id;
    private String text;
    private AbstractType type;
    private LocalDateTime create;
    private Long createId;
}
