package top.beforedawn.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.beforedawn.service.model.vo.ret.AllBlacklistRetVo;
import top.beforedawn.service.model.vo.ret.BlacklistRetVo;
import top.beforedawn.service.service.BlacklistService;
import top.beforedawn.service.service.BotService;
import top.beforedawn.service.util.Common;
import top.beforedawn.service.util.ReturnNo;

@RestController
@CrossOrigin
@RequestMapping(value = "/blacklist", produces = "application/json;charset=UTF-8")
public class BlacklistController {
    @Autowired
    BlacklistService blacklistService;

    @Autowired
    BotService botService;

    @GetMapping("/blacklists")
    public Object getAllBlacklist(
            @RequestParam() Long botId
    ) {
        if (botService.invalidBot(botId)) {
            return Common.decorate(ReturnNo.FORBIDDEN);
        }
        AllBlacklistRetVo all = blacklistService.selectAll();
        return Common.decorate(ReturnNo.OK, all);
    }

    @GetMapping("/blacklist/{id}")
    public Object getBlacklist(
            @PathVariable Long id,
            @RequestParam() Long botId
    ) {
        if (botService.invalidBot(botId)) {
            return Common.decorate(ReturnNo.FORBIDDEN);
        }
        BlacklistRetVo vo = blacklistService.selectById(id);
        return Common.decorate(ReturnNo.OK, vo);
    }
}
