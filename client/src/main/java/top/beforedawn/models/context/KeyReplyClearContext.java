package top.beforedawn.models.context;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 关键词清空上下文
 *
 * @author 墨羽翎玖
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class KeyReplyClearContext extends Context {
    Long groupId;

    public KeyReplyClearContext() {
        maxStep = 1;
    }
}
