package top.beforedawn.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.beforedawn.service.model.vo.ret.UserRetVo;
import top.beforedawn.service.service.BotService;
import top.beforedawn.service.service.UserService;
import top.beforedawn.service.util.Common;
import top.beforedawn.service.util.ReturnNo;
import top.beforedawn.service.util.ReturnObject;

@RestController
@CrossOrigin
@RequestMapping(value = "/user", produces = "application/json;charset=UTF-8")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    BotService botService;

    @GetMapping("/user/{qq}")
    public Object getUserByQq(
            @PathVariable Long qq,
            @RequestParam() Long botId
    ) {
        if (botService.invalidBot(botId)) {
            return Common.decorate(ReturnNo.FORBIDDEN);
        }
        UserRetVo user = userService.getUser(qq, botId);
        return Common.decorate(ReturnNo.OK, user);
    }

    @GetMapping("/luck/{qq}")
    public Object getUserLuckByQq (
            @PathVariable Long qq,
            @RequestParam Long botId
    ) {
        if (botService.invalidBot(botId)) {
            return Common.decorate(ReturnNo.FORBIDDEN);
        }
        UserRetVo user = userService.getLuck(qq, botId);
        return Common.decorate(ReturnNo.OK, user);
    }
}
