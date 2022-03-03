package top.beforedawn.plugins;

import top.beforedawn.util.SingleEvent;

/**
 * 帮助列表
 */
public class HelpFunction extends BasePlugin {
    @Override
    public void handleCommon(SingleEvent singleEvent) {
        if (singleEvent.getMessage().plainEqual("帮助")) {
            singleEvent.send("暂无帮助，敬请期待");
        }
    }

    @Override
    public void handleFriend(SingleEvent singleEvent) {

    }

    @Override
    public void handleGroup(SingleEvent singleEvent) {

    }
}
