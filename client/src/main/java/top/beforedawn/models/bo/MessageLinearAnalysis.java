package top.beforedawn.models.bo;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 文本消息线性解析器
 *
 * @author 墨羽翎玖
 */
public class MessageLinearAnalysis {
    private String text;

    public MessageLinearAnalysis(MyMessage message) {
        text = message.getPlainString();
    }

    public MessageLinearAnalysis(String message) {
        text = message;
    }

    public MessageLinearAnalysis(StringBuilder message) {
        text = message.toString();
    }

    public ArrayList<String> split() {
        if (text.equals("")) {
            return new ArrayList<>();
        }
        String[] s = text.split(" ");
        return new ArrayList<>(Arrays.asList(s));
    }

    public void pop(String str) {
        if (text.startsWith(str)) {
            text = text.substring(str.length()).trim();
        }
    }
}
