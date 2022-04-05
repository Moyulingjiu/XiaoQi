package top.beforedawn.models.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.beforedawn.models.timed.BaseRepeatChecker;

import java.util.ArrayList;

/**
 * 定时消息上下文
 *
 * @author 墨羽翎玖
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class TimedMessageContext extends Context {
    public TimedMessageContext() {
        maxStep = 4;
    }

    private Long groupId;
    private String name;
    private ArrayList<SerializeMessage> reply;
    private String type;
    private BaseRepeatChecker checker;
}
