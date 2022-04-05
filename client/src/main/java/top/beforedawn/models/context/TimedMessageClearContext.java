package top.beforedawn.models.context;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 清空定时消息的确认
 *
 * @author 墨羽翎玖
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TimedMessageClearContext extends Context {
    Long groupId;

    public TimedMessageClearContext() {
        maxStep = 1;
    }
}
