package top.beforedawn.service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import top.beforedawn.service.mapper.AbstractPoMapper;
import top.beforedawn.service.model.bo.Abstract;
import top.beforedawn.service.model.po.AbstractPo;
import top.beforedawn.service.model.po.AbstractPoExample;
import top.beforedawn.service.util.Common;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AbstractDao {
    @Autowired
    private AbstractPoMapper abstractPoMapper;

    public Abstract select(Long id) {
        AbstractPo abstractPo = abstractPoMapper.selectByPrimaryKey(id);
        return Common.cloneVo(abstractPo, Abstract.class);
    }

    public ArrayList<Abstract> selectType(byte type) {
        AbstractPoExample example = new AbstractPoExample();
        AbstractPoExample.Criteria criteria = example.createCriteria();
        criteria.andTypeEqualTo(type);
        List<AbstractPo> abstractPos = abstractPoMapper.selectByExample(example);
        ArrayList<Abstract> ans = new ArrayList<>();
        for (AbstractPo abstractPo : abstractPos) {
            ans.add(Common.cloneVo(abstractPo, Abstract.class));
        }
        return ans;
    }

    public int insert(Abstract a) {
        return abstractPoMapper.insert(Common.cloneVo(a, AbstractPo.class));
    }

    public int delete(Long id) {
        return abstractPoMapper.deleteByPrimaryKey(id);
    }
}
