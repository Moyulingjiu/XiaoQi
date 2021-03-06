package top.beforedawn.models.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class ComplexReplyContext extends Context {
    public enum AtType {
        NONE,
        ALL,
        TRIGGER,
        NORMAL
    }

    public ComplexReplyContext() {
        maxStep = 3;
    }

    private Long groupId;
    private Long createId;
    private String key;
    private ArrayList<SerializeMessage> reply;
    private AtType type;
    private ArrayList<Long> at;
}
