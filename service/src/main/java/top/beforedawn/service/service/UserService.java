package top.beforedawn.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.beforedawn.service.dao.UserDao;
import top.beforedawn.service.model.bo.User;
import top.beforedawn.service.model.bo.UserRight;
import top.beforedawn.service.model.vo.ret.UserRetVo;
import top.beforedawn.service.util.Common;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    public void insertUser(User user, Long botId) {
        user.setCreate(LocalDateTime.now());
        user.setCreateId(botId);
        user.setModified(LocalDateTime.now());
        user.setModifiedId(botId);
        user.setRight(UserRight.NORMAL);
        user.setPoint(0);
        int insert = userDao.insert(user);
        if (insert != 1) {
            System.out.println("插入失败");
        }
    }

    public UserRetVo getUser(Long qq, Long botId) {
        User user = userDao.selectByQq(qq);
        if (user == null) {
            insertUser(new User(qq), botId);
            user = userDao.selectByQq(qq);
        }
        return Common.cloneVo(user, UserRetVo.class);
    }

    public UserRetVo getLuck(Long qq, Long botId) {
        User user = userDao.selectByQq(qq);
        if (user == null) {
            insertUser(new User(qq), botId);
            user = userDao.selectByQq(qq);
        }
        boolean needRoll = false;
        if (user.getLastLuck() == null) {
            needRoll = true;
        } else {
            Duration duration = Duration.between(user.getLastLuck(), LocalDateTime.now());
            if (duration.toDays() >= 1) {
                needRoll = true;
            }
        }
        if (needRoll) {
            Random r = new Random();
            double luck = 15 * r.nextGaussian() + 50;
            if (luck < 0.0) {
                luck = 0.0;
            } else if (luck > 100.0) {
                luck = 100.0;
            }
            user.setLuck((int) luck);
            user.setLastLuck(LocalDateTime.now());
            user.setModifiedId(botId);
            user.setModified(LocalDateTime.now());
            userDao.updateById(user);
        }
        return Common.cloneVo(user, UserRetVo.class);
    }
}
