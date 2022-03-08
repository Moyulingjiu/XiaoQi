package top.beforedawn.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.beforedawn.service.model.vo.DriftingBottleVo;
import top.beforedawn.service.model.vo.ret.DriftingBottleRetVo;
import top.beforedawn.service.service.BotService;
import top.beforedawn.service.service.DriftingBottleService;
import top.beforedawn.service.util.Common;
import top.beforedawn.service.util.ReturnNo;

@RestController
@CrossOrigin
@RequestMapping(value = "/drifting_bottle", produces = "application/json;charset=UTF-8")
public class DriftingBottleController {
    @Autowired
    DriftingBottleService driftingBottleService;
    @Autowired
    BotService botService;

    @GetMapping("/drifting_bottle")
    public Object select(
            @RequestParam Long userId,
            @RequestParam Long botId
    ) {
        if (botService.invalidBot(botId)) {
            return Common.decorate(ReturnNo.FORBIDDEN);
        }
        DriftingBottleRetVo bottleRetVo = driftingBottleService.get(userId);
        if (bottleRetVo == null)
            return Common.decorate(ReturnNo.RESOURCE_ID_NOT_EXIST);
        return Common.decorate(bottleRetVo);
    }

    @PostMapping("/drifting_bottle")
    public Object insert(
            @RequestBody DriftingBottleVo vo
    ) {
        if (vo.getBotId() == null || botService.invalidBot(vo.getBotId()) || vo.getUserId() == null) {
            return Common.decorate(ReturnNo.FORBIDDEN);
        }
        if (driftingBottleService.insert(vo))
            return Common.decorate(ReturnNo.OK);
        return Common.decorate(ReturnNo.INTERNAL_SERVER_ERR);
    }
}
