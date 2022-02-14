package top.beforedawn.service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import top.beforedawn.service.mapper.AuthorizationPoMapper;
import top.beforedawn.service.model.bo.Authorization;
import top.beforedawn.service.model.po.AuthorizationPo;
import top.beforedawn.service.model.po.AuthorizationPoExample;
import top.beforedawn.service.util.Common;

import java.util.List;

/**
 * Authorization Dao层
 *
 * @author 墨羽翎玖
 */
@Repository
public class AuthorizationDao {
    @Autowired
    private AuthorizationPoMapper authorizationPoMapper;

    public Authorization selectById(Long id) {
        AuthorizationPo authorizationPo = authorizationPoMapper.selectByPrimaryKey(id);
        return Common.cloneVo(authorizationPo, Authorization.class);
    }

    public Authorization selectByKey(String key) {
        AuthorizationPoExample example = new AuthorizationPoExample();
        AuthorizationPoExample.Criteria criteria = example.createCriteria();
        criteria.andValueEqualTo(key);
        List<AuthorizationPo> keyPos = authorizationPoMapper.selectByExample(example);
        Authorization key1 = null;
        if (keyPos.size() == 1) {
            key1 = Common.cloneVo(keyPos.get(0), Authorization.class);
        }
        return key1;
    }
}
