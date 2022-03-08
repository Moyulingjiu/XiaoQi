package top.beforedawn.models.context;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 清空复杂回复的确认
 *
 * @author 墨羽翎玖
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ComplexReplyClearContext extends Context {
    Long groupId;

    public ComplexReplyClearContext() {
        maxStep = 1;
    }
}
