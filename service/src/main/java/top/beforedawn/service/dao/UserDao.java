package top.beforedawn.service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import top.beforedawn.service.mapper.UserPoMapper;
import top.beforedawn.service.model.bo.User;
import top.beforedawn.service.model.po.UserPo;
import top.beforedawn.service.model.po.UserPoExample;
import top.beforedawn.service.util.Common;

import java.util.List;

/**
 * 用户Dao层
 *
 * @author 墨羽翎玖
 */
@Repository
public class UserDao {
    @Autowired
    private UserPoMapper userPoMapper;

    /**
     * 新增User
     *
     * @param user 用户
     * @return 改变的数量
     */
    public int insert(User user) {
        return userPoMapper.insert(Common.cloneVo(user, UserPo.class));
    }

    /**
     * 通过id修改用户
     *
     * @param user 用户
     */
    public void updateById(User user) {
        userPoMapper.updateByPrimaryKey(Common.cloneVo(user, UserPo.class));
    }

    /**
     * 通过id获取用户
     *
     * @param id id
     * @return 用户
     */
    public User selectById(Long id) {
        UserPo userPo = userPoMapper.selectByPrimaryKey(id);
        return Common.cloneVo(userPo, User.class);
    }

    /**
     * 通过qq获取用户
     *
     * @param qq qq号
     * @return 用户
     */
    public User selectByQq(Long qq) {
        UserPoExample example = new UserPoExample();
        UserPoExample.Criteria criteria = example.createCriteria();
        criteria.andQqEqualTo(qq);
        List<UserPo> userPos = userPoMapper.selectByExample(example);
        User user = null;
        if (userPos.size() == 1) {
            user = Common.cloneVo(userPos.get(0), User.class);
        }
        return user;
    }
}
