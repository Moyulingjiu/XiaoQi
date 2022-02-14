package top.beforedawn.service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import top.beforedawn.service.mapper.BotPoMapper;
import top.beforedawn.service.model.bo.Bot;
import top.beforedawn.service.model.po.BotPo;
import top.beforedawn.service.model.po.BotPoExample;
import top.beforedawn.service.util.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * 机器人Dao层
 *
 * @author 墨羽翎玖
 */
@Repository
public class BotDao {
    @Autowired
    private BotPoMapper botPoMapper;

    public Bot selectById(Long id) {
        BotPo botPo = botPoMapper.selectByPrimaryKey(id);
        return Common.cloneVo(botPo, Bot.class);
    }

    public Bot selectByQq(Long qq) {
        BotPoExample botPoExample = new BotPoExample();
        BotPoExample.Criteria criteria = botPoExample.createCriteria();
        criteria.andQqEqualTo(qq);
        List<BotPo> botPos = botPoMapper.selectByExample(botPoExample);
        Bot bot = null;
        if (botPos.size() == 1) {
            bot = Common.cloneVo(botPos.get(0), Bot.class);
        }
        return bot;
    }

    public ArrayList<Bot> selectByKey(Long keyId) {
        BotPoExample botPoExample = new BotPoExample();
        BotPoExample.Criteria criteria = botPoExample.createCriteria();
        criteria.andKeyIdEqualTo(keyId);
        List<BotPo> botPos = botPoMapper.selectByExample(botPoExample);
        ArrayList<Bot> bots = new ArrayList<>();
        for (BotPo botPo : botPos) {
            bots.add(Common.cloneVo(botPo, Bot.class));
        }
        return bots;
    }
}
