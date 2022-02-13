package top.beforedawn.models.bo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * 黑名单类
 *
 * @author 墨羽翎玖
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Blacklist {
    ArrayList<SimpleBlacklist> blacklists = new ArrayList<>();

    public SimpleBlacklist get(int index) {
        return blacklists.get(index);
    }

    public Integer length() {
        return blacklists.size();
    }

    public void append(SimpleBlacklist blacklist) {
        blacklists.add(blacklist);
    }

    public boolean contains(@NotNull Long key) {
        for (SimpleBlacklist blacklist : blacklists) {
            if (blacklist.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public SimpleBlacklist getByKey(@NotNull Long key) {
        for (SimpleBlacklist blacklist : blacklists) {
            if (blacklist.getKey().equals(key)) {
                return blacklist;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (SimpleBlacklist blacklist : blacklists) {
            builder.append(blacklist.toString());
        }
        return builder.toString();
    }
}
