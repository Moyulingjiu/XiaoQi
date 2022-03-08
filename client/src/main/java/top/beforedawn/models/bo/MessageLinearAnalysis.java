package top.beforedawn.models.bo;

import lombok.Data;
import top.beforedawn.util.CommonUtil;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 文本消息线性解析器
 *
 * @author 墨羽翎玖
 */
@Data
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

    public int getSeconds() {
        if (text.length() == 0) return 0;
        int ans = CommonUtil.getInteger(text);
        if (ans != 0) return ans;
        int total = 0;
        int temp = 0;
        char[] chars = text.toCharArray();
        for (char c : chars) {
            switch (c){
                case '0':
                case '零':
                    temp = 0;
                    break;
                case '1':
                case '一':
                    temp = 1;
                    break;
                case '2':
                case '二':
                case '两':
                    temp = 2;
                    break;
                case '3':
                case '三':
                    temp = 3;
                    break;
                case '4':
                case '四':
                    temp = 4;
                    break;
                case '5':
                case '五':
                    temp = 5;
                    break;
                case '6':
                case '六':
                    temp = 6;
                    break;
                case '7':
                case '七':
                    temp = 7;
                    break;
                case '8':
                case '八':
                    temp = 8;
                    break;
                case '9':
                case '九':
                    temp = 9;
                    break;
                case '十':
                    if (temp > 0) {
                        ans += temp * 10;
                        temp = 0;
                    } else {
                        ans += 10;
                    }
                    break;
                case '百':
                    ans += temp * 100;
                    temp = 0;
                    break;
                case '千':
                    ans += temp * 1000;
                    temp = 0;
                    break;
                case '万':
                    ans += temp * 10000;
                    temp = 0;
                    break;
                case '天':
                    ans += temp;
                    total += ans * 24 * 3600;
                    temp = 0;
                    ans = 0;
                    break;
                case '小':
                    ans += temp;
                    total += ans * 3600;
                    temp = 0;
                    ans = 0;
                    break;
                case '分':
                    ans += temp;
                    total += ans * 60;
                    temp = 0;
                    ans = 0;
                    break;
                case '秒':
                    ans += temp;
                    total += ans;
                    temp = 0;
                    ans = 0;
                    break;
            }
        }
        return total;
    }
}
