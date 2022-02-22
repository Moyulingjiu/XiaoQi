package top.beforedawn.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.beforedawn.service.dao.BlacklistDao;
import top.beforedawn.service.model.bo.Blacklist;
import top.beforedawn.service.model.bo.BlacklistType;
import top.beforedawn.service.model.vo.ret.AllBlacklistRetVo;
import top.beforedawn.service.model.vo.ret.BlacklistRetVo;
import top.beforedawn.service.util.Common;

import java.util.ArrayList;

@Service
public class BlacklistService {
    @Autowired
    private BlacklistDao blacklistDao;

    public BlacklistRetVo selectById(Long id) {
        Blacklist blacklist = blacklistDao.selectById(id);
        return Common.cloneVo(blacklist, BlacklistRetVo.class);
    }

    public AllBlacklistRetVo selectAll() {
        ArrayList<Blacklist> blacklists = blacklistDao.selectAll();
        AllBlacklistRetVo allBlacklistRetVo = new AllBlacklistRetVo();
        for (Blacklist blacklist : blacklists) {
            if (blacklist.getValid() == 0) {
                continue;
            }
            if (blacklist.getType() == BlacklistType.USER) {
                allBlacklistRetVo.addUser(Common.cloneVo(blacklist, BlacklistRetVo.class));
            } else if (blacklist.getType() == BlacklistType.GROUP) {
                allBlacklistRetVo.addGroup(Common.cloneVo(blacklist, BlacklistRetVo.class));
            }
        }
        return allBlacklistRetVo;
    }

    public boolean enable(Long id, Long botId) {
        return blacklistDao.enable(id, botId) == 1;
    }

    public boolean disable(Long id, Long botId) {
        return blacklistDao.disable(id, botId) == 1;
    }
}
