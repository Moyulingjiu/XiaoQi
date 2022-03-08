package top.beforedawn.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.beforedawn.service.dao.BotDao;
import top.beforedawn.service.dao.AuthorizationDao;
import top.beforedawn.service.model.bo.Bot;
import top.beforedawn.service.model.bo.Authorization;
import top.beforedawn.service.model.bo.KeyType;
import top.beforedawn.service.model.bo.UserRight;
import top.beforedawn.service.model.vo.BotVo;
import top.beforedawn.service.model.vo.ret.BotRetVo;
import top.beforedawn.service.model.vo.ret.UserRetVo;
import top.beforedawn.service.util.Common;
import top.beforedawn.service.util.ReturnNo;

import java.time.LocalDateTime;
import java.util.Objects;

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
    @Autowired
    private UserService userService;

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
                botRetVo.setAllowCoc(key.getAllowCoc());
                botRetVo.setAllowRpg(key.getAllowRpg());
                botRetVo.setAllowPic(key.getAllowPic());
                botRetVo.setAllowAssistant(key.getAllowAssistant());
            }
        }
        return botRetVo;
    }

    public ReturnNo updateBotByQq(long qq, BotVo botVo) {
        Bot bot = botDao.selectByQq(qq);
        if (bot == null)
            return ReturnNo.INTERNAL_SERVER_ERR;
        if (!Objects.equals(botVo.getOperator(), bot.getMasterQq())) {
            if (userService.getRight(botVo.getOperator()) == UserRight.NORMAL) {
                return ReturnNo.FORBIDDEN;
            }
        }
        if (botVo.getName() != null)
            bot.setName(botVo.getName());
        if (botVo.getAllowFriend() != null)
            bot.setAllowFriend(botVo.getAllowFriend());
        if (botVo.getAllowGroup() != null)
            bot.setAllowGroup(botVo.getAllowGroup());
        if (botVo.getHeart() != null)
            bot.setHeart(botVo.getHeart());
        if (botVo.getHeartInterval() != null)
            bot.setHeartInterval(botVo.getHeartInterval());
        if (botVo.getRemindFriend() != null)
            bot.setRemindFriend(botVo.getRemindFriend());
        if (botVo.getRemindGroup() != null)
            bot.setRemindGroup(botVo.getRemindGroup());
        if (botVo.getRemindMute() != null)
            bot.setRemindMute(botVo.getRemindMute());
        if (botVo.getRemindQuit() != null)
            bot.setRemindQuit(botVo.getRemindQuit());
        if (botVo.getClearBlacklist() != null)
            bot.setClearBlacklist(botVo.getClearBlacklist());
        bot.setModifiedId(botVo.getOperator());
        bot.setModified(LocalDateTime.now());
        botDao.update(bot);
        return ReturnNo.OK;
    }
}
