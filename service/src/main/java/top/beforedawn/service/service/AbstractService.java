package top.beforedawn.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.beforedawn.service.dao.AbstractDao;
import top.beforedawn.service.model.bo.Abstract;
import top.beforedawn.service.model.bo.AbstractType;
import top.beforedawn.service.model.bo.UserRight;
import top.beforedawn.service.model.vo.ret.AbstractRetVo;
import top.beforedawn.service.util.Common;
import top.beforedawn.service.util.ReturnNo;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class AbstractService {
    @Autowired
    private AbstractDao abstractDao;
    @Autowired
    private UserService userService;

    public ReturnNo delete(long id, long userId) {
        if (userService.getRight(userId) == UserRight.NORMAL) return ReturnNo.FORBIDDEN;
        if (abstractDao.delete(id) > 0) return ReturnNo.OK;
        return ReturnNo.RESOURCE_ID_NOT_EXIST;
    }

    public boolean insert(Abstract a, Long userId) {
        if (a.getText() == null) return false;
        if (a.getType() == null) return false;
        if (userId == null) return false;
        if (userService.getRight(userId) == UserRight.NORMAL) return false;
        a.setCreate(LocalDateTime.now());
        a.setCreateId(userId);
        a.setModified(LocalDateTime.now());
        a.setModifiedId(userId);
        return abstractDao.insert(a) > 0;
    }

    public AbstractRetVo get(AbstractType type) {
        ArrayList<Abstract> abstracts = abstractDao.selectType((byte) type.ordinal());
        if (abstracts.size() == 0) return null;
        int index = Common.randomInteger(abstracts.size());
        return Common.cloneVo(abstracts.get(index), AbstractRetVo.class);
    }

    public AbstractRetVo selectById(Long id) {
        if (id == null) return null;
        return Common.cloneVo(abstractDao.select(id), AbstractRetVo.class);
    }
}
