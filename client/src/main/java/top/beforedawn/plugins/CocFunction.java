package top.beforedawn.plugins;

import net.mamoe.mirai.message.data.MessageChainBuilder;
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
        return singleEvent.getConfig().isAllowCoc();
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
        // 主世界生物
        if (message.equals("coc野蛮人")) {
            // todo: 填充图片
        } else if (message.equals("coc弓箭手")) {
            // todo: 填充图片
        } else if (message.equals("coc巨人") || message.equals("coc胖子")) {
            sendCocImage(singleEvent, "巨人.jpg");
        } else if (message.equals("coc哥布林")) {
            // todo: 填充图片
        } else if (message.equals("coc炸弹人")) {
            // todo: 填充图片
        } else if (message.equals("coc气球兵") || message.equals("coc气球")) {
            sendCocImage(singleEvent, "气球兵.jpg");
        } else if (message.equals("coc法师")) {
            sendCocImage(singleEvent, "法师.jpg");
        } else if (message.equals("coc天使")) {
            sendCocImage(singleEvent, "天使.jpg");
        } else if (message.equals("coc飞龙") || message.equals("coc火龙")) {
            sendCocImage(singleEvent, "飞龙.jpg");
        } else if (message.equals("coc皮卡超人") || message.equals("coc皮卡")) {
            sendCocImage(singleEvent, "皮卡超人.jpg");
        } else if (message.equals("coc飞龙宝宝") || message.equals("coc龙宝宝") || message.equals("coc龙宝")) {
            sendCocImage(singleEvent, "飞龙宝宝.jpg");
        } else if (message.equals("coc掘地矿工") || message.equals("coc矿工")) {
            sendCocImage(singleEvent, "掘地矿工.jpg");
        } else if (message.equals("coc雷电飞龙") || message.equals("coc雷龙")) {
            sendCocImage(singleEvent, "雷电飞龙.jpg");
        } else if (message.equals("coc大雪怪")) {
            // todo: 填充图片
        }
        // 主世界黑油生物
        else if (message.equals("coc亡灵")) {
            sendCocImage(singleEvent, "亡灵.jpg");
        } else if (message.equals("coc野猪骑士") || message.equals("coc野猪")) {
            sendCocImage(singleEvent, "野猪骑士.jpg");
        } else if (message.equals("coc瓦基丽武神") || message.equals("coc女武神")) {
            sendCocImage(singleEvent, "瓦基丽武神.jpg");
        } else if (message.equals("coc戈仑石人") || message.equals("coc石人")) {
            // todo: 填充图片
        } else if (message.equals("coc女巫")) {
            sendCocImage(singleEvent, "女巫.jpg");
        } else if (message.equals("coc熔岩猎犬") || message.equals("coc狗")) {
            sendCocImage(singleEvent, "熔岩猎犬.jpg");
        } else if (message.equals("coc巨石投手") || message.equals("coc蓝胖")) {
            sendCocImage(singleEvent, "巨石投手.jpg");
        } else if (message.equals("coc戈仑冰人") || message.equals("coc冰人")) {
            // todo: 填充图片
        } else if (message.equals("coc英雄猎手")) {
            sendCocImage(singleEvent, "英雄猎手.jpg");
        }
    }
}
