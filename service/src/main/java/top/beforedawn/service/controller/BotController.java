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

    @GetMapping("/convention")
    public Object getConvention(
            @RequestParam() Long botId
    ) {
        if (botService.invalidBot(botId)) {
            return Common.decorate(ReturnNo.FORBIDDEN);
        }
        String convention = "小柒系列机器人使用公约：\n" +
                "1. 不得利用机器人进行违法违规行为，后果由使用者自行承担。\n" +
                "2. 不得利用机器人故意恶心、人身攻击他人，挑起争端。\n" +
                "3. 加入生态的机器人主人有义务监督成员履行公约。\n" +
                "4. 使用者使用小柒意味着同意本公约。\n" +
                "5. 机器人创造者保留本公约最终解释权，并且有权在使用者违反上述规定时停止或收回其使用权。";
        return Common.decorate(ReturnNo.OK, convention);
    }

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
