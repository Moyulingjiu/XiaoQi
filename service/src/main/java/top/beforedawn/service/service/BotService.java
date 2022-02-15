package top.beforedawn.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.beforedawn.service.dao.BotDao;
import top.beforedawn.service.dao.AuthorizationDao;
import top.beforedawn.service.model.bo.Bot;
import top.beforedawn.service.model.bo.Authorization;
import top.beforedawn.service.model.bo.KeyType;
import top.beforedawn.service.model.vo.ret.BotRetVo;
import top.beforedawn.service.util.Common;

import java.time.LocalDateTime;

/**
 * BotService
 *
 * @author 墨羽翎玖
 */
@Service
public class BotService {
    @Autowired
    private BotDao botDao;
    @Autowired
    private AuthorizationDao authorizationDao;

    public boolean invalidBot(Long qq) {
        Bot bot = botDao.selectByQq(qq);
        if (bot == null) {
            return true;
        }
        Authorization key = authorizationDao.selectById(bot.getKeyId());
        if (key == null) {
            return true;
        }
        LocalDateTime now = LocalDateTime.now();
        if (key.getType() == KeyType.FOREVER) {
            return false;
        }
        return key.getValidBeginDate().isAfter(now) || key.getValidEndDate().isBefore(now);
    }

    public BotRetVo selectBotByQq(Long qq) {
        Bot bot = botDao.selectByQq(qq);
        BotRetVo botRetVo = Common.cloneVo(bot, BotRetVo.class);
        if (botRetVo != null) {
            Authorization key = authorizationDao.selectById(bot.getKeyId());
            if (key != null) {
                botRetVo.setKeyValue(key.getValue());
                botRetVo.setKeyUserId(key.getUserId());
                botRetVo.setKeyValidBeginDate(key.getValidBeginDate());
                botRetVo.setKeyValidEndDate(key.getValidEndDate());
                botRetVo.setKeyType(key.getType());
            }
        }
        return botRetVo;
    }
}
