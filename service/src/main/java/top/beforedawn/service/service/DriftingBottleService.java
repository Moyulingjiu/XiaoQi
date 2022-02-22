package top.beforedawn.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.beforedawn.service.dao.DriftingBottleDao;
import top.beforedawn.service.model.bo.DriftingBottle;
import top.beforedawn.service.model.vo.DriftingBottleVo;
import top.beforedawn.service.model.vo.ret.DriftingBottleRetVo;
import top.beforedawn.service.util.Common;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class DriftingBottleService {
    @Autowired
    private DriftingBottleDao driftingBottleDao;

    public boolean insert(Long userId, DriftingBottleVo driftingBottleVo) {
        DriftingBottle driftingBottle = Common.cloneVo(driftingBottleVo, DriftingBottle.class);
        driftingBottle.setValid((byte) 1);
        driftingBottle.setCreate(LocalDateTime.now());
        driftingBottle.setCreateId(userId);
        driftingBottle.setModified(LocalDateTime.now());
        driftingBottle.setModifiedId(userId);
        int insert = driftingBottleDao.insert(driftingBottle);
        return insert != 0;
    }

    public DriftingBottleRetVo select(Long id) {
        DriftingBottle driftingBottle = driftingBottleDao.selectById(id);
        return Common.cloneVo(driftingBottle, DriftingBottleRetVo.class);
    }

    /**
     * 随机捡漂流瓶
     *
     * @param userId 用户id
     * @return 随机漂流瓶
     */
    public DriftingBottleRetVo get(Long userId) {
        ArrayList<DriftingBottle> driftingBottles = driftingBottleDao.selectAll();
        if (driftingBottles.size() == 0) {
            return null;
        }
        int i = Common.randomInteger(driftingBottles.size());
        DriftingBottle driftingBottle = driftingBottles.get(i);
        driftingBottle.setModifiedId(userId);
        driftingBottle.setModified(LocalDateTime.now());
        if (driftingBottle.getPermanent() != 1)
            driftingBottle.setValid((byte) 0);
        driftingBottleDao.updateById(driftingBottle);
        return Common.cloneVo(driftingBottle, DriftingBottleRetVo.class);
    }
}
