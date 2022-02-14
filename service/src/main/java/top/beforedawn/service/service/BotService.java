package top.beforedawn.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.beforedawn.service.dao.BotDao;
import top.beforedawn.service.dao.AuthorizationDao;
import top.beforedawn.service.model.bo.Bot;
import top.beforedawn.service.model.bo.Authorization;
import top.beforedawn.service.model.bo.KeyType;

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

    public boolean invalidBot(Long botId) {
        Bot bot = botDao.selectByQq(botId);
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
}
