package top.beforedawn.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupEntryRule {
    private String code;

    public boolean pass(String message) {
        return code.equals(message);
    }
}
