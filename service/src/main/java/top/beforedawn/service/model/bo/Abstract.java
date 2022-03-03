package top.beforedawn.service.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Abstract {
    private Long id;
    private String text;
    private AbstractType type;
    private LocalDateTime modified;
    private Long modifiedId;
    private LocalDateTime create;
    private Long createId;
}
