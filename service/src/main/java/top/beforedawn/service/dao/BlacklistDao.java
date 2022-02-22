package top.beforedawn.service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import top.beforedawn.service.mapper.BlacklistPoMapper;
import top.beforedawn.service.model.bo.Blacklist;
import top.beforedawn.service.model.po.BlacklistPo;
import top.beforedawn.service.model.po.BlacklistPoExample;
import top.beforedawn.service.util.Common;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 黑名单 dao 层
 *
 * @author 墨羽翎玖
 */
@Repository
public class BlacklistDao {
    @Autowired
    private BlacklistPoMapper blacklistPoMapper;

    public Blacklist selectById(Long id) {
        BlacklistPo blacklistPo = blacklistPoMapper.selectByPrimaryKey(id);
        return Common.cloneVo(blacklistPo, Blacklist.class);
    }

    public ArrayList<Blacklist> selectAll() {
        BlacklistPoExample example = new BlacklistPoExample();
        BlacklistPoExample.Criteria criteria = example.createCriteria();
        criteria.andValidEqualTo((byte) 1);
        List<BlacklistPo> blacklistPos = blacklistPoMapper.selectByExample(example);
        ArrayList<Blacklist> blacklists = new ArrayList<>();
        for (BlacklistPo blacklistPo : blacklistPos) {
            blacklists.add(Common.cloneVo(blacklistPo, Blacklist.class));
        }
        return blacklists;
    }

    public int insert(Blacklist blacklist) {
        BlacklistPo blacklistPo = Common.cloneVo(blacklist, BlacklistPo.class);
        return blacklistPoMapper.insert(blacklistPo);
    }

    public int update(Blacklist blacklist) {
        if (blacklist.getId() == null) {
            return 0;
        }
        BlacklistPo blacklistPo = Common.cloneVo(blacklist, BlacklistPo.class);
        return blacklistPoMapper.updateByPrimaryKey(blacklistPo);
    }

    public int enable(Long id, Long botId) {
        BlacklistPo blacklistPo = blacklistPoMapper.selectByPrimaryKey(id);
        if (blacklistPo == null) {
            return 0;
        }
        if (blacklistPo.getValid() == 1) {
            return 1;
        }
        blacklistPo.setValid((byte) 1);
        blacklistPo.setModifiedId(botId);
        blacklistPo.setModified(LocalDateTime.now());
        return blacklistPoMapper.updateByPrimaryKey(blacklistPo);
    }

    public int disable(Long id, Long botId) {
        BlacklistPo blacklistPo = blacklistPoMapper.selectByPrimaryKey(id);
        if (blacklistPo == null) {
            return 0;
        }
        if (blacklistPo.getValid() == 0) {
            return 1;
        }
        blacklistPo.setValid((byte) 0);
        blacklistPo.setModifiedId(botId);
        blacklistPo.setModified(LocalDateTime.now());
        return blacklistPoMapper.updateByPrimaryKey(blacklistPo);
    }
}
