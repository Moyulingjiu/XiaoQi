package top.beforedawn.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.beforedawn.service.dao.UserDao;
import top.beforedawn.service.model.bo.User;
import top.beforedawn.service.model.vo.ret.UserRetVo;
import top.beforedawn.service.util.Common;

import java.time.LocalDateTime;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    public void insertUser(User user, Long botId) {
        user.setCreate(LocalDateTime.now());
        user.setCreateId(botId);
        user.setModified(LocalDateTime.now());
        user.setModifiedId(botId);
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
}
