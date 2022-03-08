package top.beforedawn.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.beforedawn.service.model.bo.Abstract;
import top.beforedawn.service.model.bo.AbstractType;
import top.beforedawn.service.model.vo.AbstractVo;
import top.beforedawn.service.model.vo.ret.AbstractRetVo;
import top.beforedawn.service.service.AbstractService;
import top.beforedawn.service.service.BotService;
import top.beforedawn.service.util.Common;
import top.beforedawn.service.util.ReturnNo;

@RestController
@CrossOrigin
@RequestMapping(value = "/abstract", produces = "application/json;charset=UTF-8")
public class AbstractController {
    @Autowired
    BotService botService;
    @Autowired
    AbstractService abstractService;

    @DeleteMapping("/abstract/{id}")
    public Object delete(
            @PathVariable Long id,
            @RequestParam Long botId,
            @RequestParam Long userId
    ) {
        if (botService.invalidBot(botId)) return Common.decorate(ReturnNo.FORBIDDEN);
        ReturnNo returnNo = abstractService.delete(id, userId);
        return Common.decorate(returnNo);
    }

    @PostMapping("/abstract")
    public Object insert(
            @RequestBody AbstractVo vo
    ) {
        if (botService.invalidBot(vo.getBotId())) return Common.decorate(ReturnNo.FORBIDDEN);
        if (abstractService.insert(Common.cloneVo(vo, Abstract.class), vo.getUserId()))
            return Common.decorate(ReturnNo.OK);
        return Common.decorate(ReturnNo.FORBIDDEN);
    }

    @GetMapping("/abstract")
    public Object select(
            @RequestParam Long botId,
            @RequestParam Byte type
    ) {
        if (botService.invalidBot(botId)) return Common.decorate(ReturnNo.FORBIDDEN);
        AbstractType[] values = AbstractType.values();
        if (type < 0 || type >= values.length) return Common.decorate(ReturnNo.RESOURCE_ID_NOT_EXIST);
        AbstractRetVo abstractRetVo = abstractService.get(values[type]);
        if (abstractRetVo == null) return Common.decorate(ReturnNo.RESOURCE_ID_NOT_EXIST);
        return Common.decorate(abstractRetVo);
    }

    @GetMapping("/abstract/{id}")
    public Object selectById(
            @PathVariable Long id,
            @RequestParam Long botId
    ) {
        if (botService.invalidBot(botId)) return Common.decorate(ReturnNo.FORBIDDEN);
        AbstractRetVo abstractRetVo = abstractService.selectById(id);
        if (abstractRetVo == null) return Common.decorate(ReturnNo.RESOURCE_ID_NOT_EXIST);
        return Common.decorate(abstractRetVo);
    }
}
