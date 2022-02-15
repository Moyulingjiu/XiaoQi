package top.beforedawn.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.beforedawn.service.model.vo.ret.BotRetVo;
import top.beforedawn.service.service.BotService;
import top.beforedawn.service.util.Common;
import top.beforedawn.service.util.ReturnNo;

@RestController
@CrossOrigin
@RequestMapping(value = "/bot", produces = "application/json;charset=UTF-8")
public class BotController {
    @Autowired
    BotService botService;

    @GetMapping("/bot/{qq}")
    public Object getBotByQq(
            @PathVariable Long qq
    ) {
        BotRetVo botRetVo = botService.selectBotByQq(qq);
        if (botRetVo == null) {
            return Common.decorate(ReturnNo.RESOURCE_ID_NOT_EXIST);
        }
        return Common.decorate(botRetVo);
    }
}
