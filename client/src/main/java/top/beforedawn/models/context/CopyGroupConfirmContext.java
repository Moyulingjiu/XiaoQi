package top.beforedawn.models.context;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 复制群回复的确认
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CopyGroupConfirmContext extends Context {
    Long groupId;

    public CopyGroupConfirmContext() {
        maxStep = 1;
    }
}
