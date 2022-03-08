package top.beforedawn.service.model.vo.ret;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

/**
 * 全部黑名单的实体类
 *
 * @author 墨羽翎玖
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllBlacklistRetVo {
    private ArrayList<BlacklistRetVo> user;
    private ArrayList<BlacklistRetVo> group;

    public void addUser(BlacklistRetVo vo) {
        if (user == null) {
            user = new ArrayList<>();
        }
        user.add(vo);
    }

    public void addGroup(BlacklistRetVo vo) {
        if (group == null) {
            group = new ArrayList<>();
        }
        group.add(vo);
    }
}
