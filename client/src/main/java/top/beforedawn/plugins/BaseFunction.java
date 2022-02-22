package top.beforedawn.plugins;

import top.beforedawn.models.bo.MyUser;
import top.beforedawn.util.CommonUtil;
import top.beforedawn.util.HttpUtil;
import top.beforedawn.util.SingleEvent;

/**
 * 基础功能
 *
 * @author 墨羽翎玖
 */
public class BaseFunction extends BasePlugin {
    public BaseFunction() {
        pluginName = "base_function";
    }

    @Override
    public void handleCommon(SingleEvent singleEvent) {
        if (singleEvent.getMessage().plainEqual("骰子") || singleEvent.getMessage().plainEqual("色子")) {
            singleEvent.sendAt(String.format("你投出的点数是：%d", CommonUtil.randomInteger(1, 6)));
        } else if (singleEvent.getMessage().plainEqual("硬币")) {
            if (CommonUtil.randomInteger() % 2 == 0) {
                singleEvent.sendAt("你抛出的是：正面");
            } else {
                singleEvent.sendAt("你抛出的是：反面");
            }
        } else if (singleEvent.getMessage().plainEqual("运势")) {
            MyUser user = HttpUtil.getLuck(singleEvent);
            int luck = user.getLuck();
            singleEvent.sendAt("你今天的运势是：" + luck);
        }
    }

    @Override
    public void handleFriend(SingleEvent singleEvent) {

    }

    @Override
    public void handleGroup(SingleEvent singleEvent) {

    }
}
