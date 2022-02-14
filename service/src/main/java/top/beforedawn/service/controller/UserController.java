package top.beforedawn.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.beforedawn.service.model.vo.ret.UserRetVo;
import top.beforedawn.service.service.UserService;
import top.beforedawn.service.util.ReturnNo;
import top.beforedawn.service.util.ReturnObject;

@RestController
@CrossOrigin
@RequestMapping(value = "/user", produces = "application/json;charset=UTF-8")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/user/{qq}")
    public ReturnObject<UserRetVo> getUserByQq(
            @PathVariable Long qq,
            @RequestParam(required = false) Long botId
    ) {
        System.out.println(qq);
        UserRetVo user = userService.getUser(qq, botId);
        return new ReturnObject<>(ReturnNo.OK, user);
    }
}
