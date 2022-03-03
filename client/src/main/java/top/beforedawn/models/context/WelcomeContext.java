package top.beforedawn.models.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class WelcomeContext extends Context {
    public WelcomeContext() {
        maxStep = 1;
    }

    private Long groupId;
    private ArrayList<SerializeMessage> welcome;
}
