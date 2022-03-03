package top.beforedawn.plugins;

import net.mamoe.mirai.message.data.MessageChainBuilder;
import top.beforedawn.config.GroupPool;
import top.beforedawn.models.bo.MyGroup;
import top.beforedawn.util.SingleEvent;

/**
 * 部落冲突模块
 */
public class CocFunction extends BasePlugin {
    public CocFunction() {
        pluginName = "clash_of_clans";
    }

    public void sendCocImage(SingleEvent singleEvent, String imageName) {
        MessageChainBuilder messages = new MessageChainBuilder();
        messages.append(singleEvent.uploadImage(singleEvent.getConfig().getGlobalWorkdir() + "coc/" + imageName));
        singleEvent.send(messages.asMessageChain());
    }

    @Override
    public boolean before(SingleEvent singleEvent) {
        MyGroup group = GroupPool.get(singleEvent);
        return singleEvent.getConfig().isAllowCoc() && group.isCoc();
    }

    @Override
    public void handleCommon(SingleEvent singleEvent) {
    }

    @Override
    public void handleFriend(SingleEvent singleEvent) {

    }

    @Override
    public void handleGroup(SingleEvent singleEvent) {
        String message = singleEvent.getMessage().getPlainString().toLowerCase().replace(" ", "");
        if (!message.startsWith("coc")) {
            return;
        }
        switch (message) {
            /*
                其他图片
             */
            case "coc商人":
            case "coc商人刷新表":
            case "coc商店":
                sendCocImage(singleEvent, "other/商人数据.jpg");
                break;
            case "coc联赛":
            case "coc联赛奖励":
                sendCocImage(singleEvent, "other/联赛数据.jpg");
                break;
            case "coc部落战奖励":
                sendCocImage(singleEvent, "other/部落战奖励.png");
                break;
            case "coc闪电机制":
                sendCocImage(singleEvent, "other/闪电机制.jpg");
                break;
            case "coc闪震计算表":
                sendCocImage(singleEvent, "other/闪震计算表.png");
                break;
            case "coc夜世界奖励":
                sendCocImage(singleEvent, "other/夜世界奖励.png");
                break;
            case "coc援军等级限制":
                sendCocImage(singleEvent, "other/援军等级限制.png");
                break;

            /*
                主世界兵种
             */
            case "coc野蛮人":
                sendCocImage(singleEvent, "兵种介绍-野蛮人.jpg");
                break;
            case "coc弓箭手":
                sendCocImage(singleEvent, "兵种介绍-弓箭手.jpg");
                break;
            case "coc巨人":
            case "coc胖子":
                sendCocImage(singleEvent, "兵种介绍-巨人.jpg");
                break;
            case "coc哥布林":
                sendCocImage(singleEvent, "兵种介绍-哥布林.jpg");
                break;
            case "coc炸弹人":
                sendCocImage(singleEvent, "兵种介绍-炸弹人.jpg");
                break;
            case "coc气球兵":
            case "coc气球":
                sendCocImage(singleEvent, "兵种介绍-气球.jpg");
                break;
            case "coc法师":
                sendCocImage(singleEvent, "兵种介绍-法师.jpg");
                break;
            case "coc天使":
                sendCocImage(singleEvent, "兵种介绍-天使.jpg");
                break;
            case "coc飞龙":
            case "coc火龙":
                sendCocImage(singleEvent, "兵种介绍-火龙.jpg");
                break;
            case "coc皮卡超人":
            case "coc皮卡":
                sendCocImage(singleEvent, "兵种介绍-皮卡超人.jpg");
                break;
            case "coc飞龙宝宝":
            case "coc龙宝宝":
            case "coc龙宝":
                sendCocImage(singleEvent, "兵种介绍-火龙宝宝.jpg");
                break;
            case "coc掘地矿工":
            case "coc矿工":
                sendCocImage(singleEvent, "兵种介绍-矿工.jpg");
                break;
            case "coc雷电飞龙":
            case "coc雷龙":
                sendCocImage(singleEvent, "兵种介绍-雷龙.jpg");
                break;
            case "coc大雪怪":
                sendCocImage(singleEvent, "兵种介绍-大雪怪.jpg");
                break;

            /*
                主世界黑油生物
            */
            case "coc亡灵":
                sendCocImage(singleEvent, "兵种介绍-亡灵.jpg");
                break;
            case "coc野猪骑士":
            case "coc野猪":
                sendCocImage(singleEvent, "兵种介绍-野猪骑士.jpg");
                break;
            case "coc瓦基丽武神":
            case "coc女武神":
                sendCocImage(singleEvent, "兵种介绍-武神.jpg");
                break;
            case "coc戈仑石人":
            case "coc石人":
                sendCocImage(singleEvent, "兵种介绍-石头人.jpg");
                break;
            case "coc女巫":
                sendCocImage(singleEvent, "兵种介绍-女巫.jpg");
                break;
            case "coc熔岩猎犬":
            case "coc狗":
                sendCocImage(singleEvent, "兵种介绍-熔岩猎犬.jpg");
                break;
            case "coc巨石投手":
            case "coc蓝胖":
                sendCocImage(singleEvent, "兵种介绍-蓝胖.jpg");
                break;
            case "coc戈仑冰人":
            case "coc冰石头人":
            case "coc冰人":
                sendCocImage(singleEvent, "兵种介绍-冰石头.jpg");
                break;
            case "coc英雄猎手":
                sendCocImage(singleEvent, "兵种介绍-英雄猎手.jpg");
                break;

            /*
                法术
             */
            case "coc雷电法术":
            case "coc闪电法术":
            case "coc雷电":
            case "coc闪电":
                sendCocImage(singleEvent, "兵种介绍-雷电法术.jpg");
                break;
            case "coc治疗法术":
            case "coc治疗":
                sendCocImage(singleEvent, "兵种介绍-疗伤法术.jpg");
                break;
            case "coc狂暴法术":
            case "coc狂暴":
                sendCocImage(singleEvent, "兵种介绍-狂暴法术.jpg");
                break;
            case "coc弹跳法术":
            case "coc弹跳":
                sendCocImage(singleEvent, "兵种介绍-弹跳法术.jpg");
                break;
            case "coc冰冻法术":
            case "coc冰冻":
                sendCocImage(singleEvent, "兵种介绍-冰冻法术.jpg");
                break;
            case "coc镜像法术":
            case "coc镜像":
                sendCocImage(singleEvent, "兵种介绍-镜像法术.jpg");
                break;
            case "coc隐形法术":
            case "coc隐形":
                sendCocImage(singleEvent, "兵种介绍-隐形法术.jpg");
                break;
            case "coc毒药法术":
            case "coc毒药":
                sendCocImage(singleEvent, "兵种介绍-毒药法术.jpg");
                break;
            case "coc地震法术":
            case "coc地震":
                sendCocImage(singleEvent, "兵种介绍-地震法术.jpg");
                break;
            case "coc急速法术":
            case "coc急速":
                sendCocImage(singleEvent, "兵种介绍-急速法术.jpg");
                break;
            case "coc骷髅法术":
                sendCocImage(singleEvent, "兵种介绍-骷髅法术.jpg");
                break;
            case "coc蝙蝠法术":
                sendCocImage(singleEvent, "兵种介绍-蝙蝠法术.jpg");
                break;

            /*
                超级兵种
             */
            case "coc超级野蛮人":
            case "coc小黄毛":
                sendCocImage(singleEvent, "兵种介绍-超级野蛮人.jpg");
                break;
            case "coc超级弓箭手":
            case "coc超弓":
                sendCocImage(singleEvent, "兵种介绍-超级弓箭手.jpg");
                break;

            /*
                英雄
             */
        }
    }
}
