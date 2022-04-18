package top.beforedawn.models.context;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 群设置清空确认
 *
 * @author 墨羽翎玖
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GroupConfigClearContext extends Context {
    Long groupId;

    public GroupConfigClearContext() {
        maxStep = 1;
    }
}
