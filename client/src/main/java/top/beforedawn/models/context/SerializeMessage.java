package top.beforedawn.models.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 序列化消息
 *
 * @author 墨羽翎玖
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SerializeMessage {
    public enum MessageType {
        PLAIN,
        IMAGE,
        AT,
        EMOJI
    }

    private MessageType type;
    private String context;
}
