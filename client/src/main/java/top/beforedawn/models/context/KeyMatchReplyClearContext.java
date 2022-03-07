package top.beforedawn.models.context;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 清空关键词回复的确认
 *
 * @author 墨羽翎玖
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class KeyMatchReplyClearContext extends Context {
    Long groupId;

    public KeyMatchReplyClearContext() {
        maxStep = 1;
    }
}
