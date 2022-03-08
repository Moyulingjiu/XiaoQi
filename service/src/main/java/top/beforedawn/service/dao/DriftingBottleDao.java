package top.beforedawn.service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import top.beforedawn.service.mapper.DriftingBottlePoMapper;
import top.beforedawn.service.model.bo.DriftingBottle;
import top.beforedawn.service.model.po.DriftingBottlePo;
import top.beforedawn.service.model.po.DriftingBottlePoExample;
import top.beforedawn.service.util.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * 漂流瓶Dao层
 *
 * @author 墨羽翎玖
 */
@Repository
public class DriftingBottleDao {
    @Autowired
    DriftingBottlePoMapper driftingBottlePoMapper;

    /**
     * 添加漂流瓶
     *
     * @param driftingBottle 漂流瓶
     * @return 改变的数量
     */
    public int insert(DriftingBottle driftingBottle) {
        return driftingBottlePoMapper.insert(Common.cloneVo(driftingBottle, DriftingBottlePo.class));
    }

    /**
     * 通过id修改漂流瓶
     *
     * @param driftingBottle 用户
     */
    public void updateById(DriftingBottle driftingBottle) {
        driftingBottlePoMapper.updateByPrimaryKey(Common.cloneVo(driftingBottle, DriftingBottlePo.class));
    }

    public DriftingBottle selectById(Long id) {
        DriftingBottlePo driftingBottlePo = driftingBottlePoMapper.selectByPrimaryKey(id);
        return Common.cloneVo(driftingBottlePo, DriftingBottle.class);
    }

    public ArrayList<DriftingBottle> selectAll() {
        DriftingBottlePoExample example = new DriftingBottlePoExample();
        DriftingBottlePoExample.Criteria criteria = example.createCriteria();
        criteria.andValidEqualTo((byte) 1);
        List<DriftingBottlePo> driftingBottlePos = driftingBottlePoMapper.selectByExample(example);
        ArrayList<DriftingBottle> driftingBottles = new ArrayList<>();
        for (DriftingBottlePo driftingBottlePo : driftingBottlePos) {
            driftingBottles.add(Common.cloneVo(driftingBottlePo, DriftingBottle.class));
        }
        return driftingBottles;
    }
}
