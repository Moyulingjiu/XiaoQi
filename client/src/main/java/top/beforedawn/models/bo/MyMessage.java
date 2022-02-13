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
     * 判断文字是否严格等于
     *
     * @param str 文字
     * @return boolean
     */
    public boolean plainEqual(String str) {
        return getPlainString().equals(str) && (at.size() == 0);
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
}