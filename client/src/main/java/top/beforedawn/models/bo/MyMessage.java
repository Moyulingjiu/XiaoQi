package top.beforedawn.models.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 自己的消息类
 *
 * @author 墨羽翎玖
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyMessage {
    boolean beAt = false;
    ArrayList<String> plain = new ArrayList<>();
    Set<Long> at = new HashSet<>();

    public String getPlainString() {
        StringBuilder builder = new StringBuilder();
        for (String s : plain) {
            builder.append(s);
        }
        return builder.toString().trim();
    }

    /**
     * 克隆消息
     *
     * @return 消息
     */
    public MyMessage clone() {
        MyMessage message = new MyMessage();
        message.setBeAt(beAt);
        message.setAt(new HashSet<>(at));
        message.setPlain(new ArrayList<>(plain));
        return message;
    }

    /**
     * 文本严格等于并且还需要被艾特
     *
     * @param str 文本信息
     * @return boolean
     */
    public boolean plainBeAtEqual(String str) {
        return plainEqual(str) && beAt;
    }

    /**
     * 判断文字是否严格等于
     *
     * @param str 文字
     * @return boolean
     */
    public boolean plainEqual(String str) {
        return getPlainString().equals(str) && (at.size() == 0);
    }

    /**
     * 判断文字是否以str开头
     *
     * @param str 文本
     * @return boolean
     */
    public boolean plainStartWith(String str) {
        return getPlainString().startsWith(str) && (at.size() == 0);
    }

    /**
     * 判断文字是否等于（而不关心at）
     *
     * @param str 文字
     * @return boolean
     */
    public boolean plainEqualWithoutAt(String str) {
        return getPlainString().equals(str);
    }

    /**
     * 判断文字是否以str开头（而不关心at）
     *
     * @param str 文本
     * @return boolean
     */
    public boolean plainStartWithoutAt(String str) {
        return getPlainString().startsWith(str);
    }

}
