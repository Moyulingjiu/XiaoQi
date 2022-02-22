package top.beforedawn.plugins;

import top.beforedawn.models.bo.BotRemoteInformation;
import top.beforedawn.util.CommonUtil;
import top.beforedawn.util.HttpUtil;
import top.beforedawn.util.SingleEvent;

public class BotFunction extends BasePlugin {
    public BotFunction() {
        pluginName = "bot_function";
    }

    @Override
    public void handleCommon(SingleEvent singleEvent) {
        if (singleEvent.getMessage().plainEqual("机器人信息")) {
            StringBuilder builder = new StringBuilder();
            builder.append("机器人id：").append(singleEvent.getConfig().getQq()).append("\n");
            builder.append("名字：").append(singleEvent.getConfig().getName()).append("\n");
            builder.append("主人：").append(singleEvent.getConfig().getMaster()).append("\n");
            if (singleEvent.getConfig().getKeyType().equals("NORMAL")) {
                builder
                        .append("有效期：")
                        .append(CommonUtil.LocalDateTime2String(singleEvent.getConfig().getKeyValidBeginDate()))
                        .append("—")
                        .append(CommonUtil.LocalDateTime2String(singleEvent.getConfig().getKeyValidEndDate()));
            } else {
                builder.append("有效期：无限期");
            }

            singleEvent.send(builder.toString());
        } else if (singleEvent.getMessage().plainEqual("统计信息")) {
            singleEvent.send(singleEvent.getConfig().getStatistics().toString());
        } else if (singleEvent.getMessage().plainEqual("我的权限")) {
            switch (singleEvent.getRight()) {
                case SYSTEM_SUPER_ADMIN:
                    singleEvent.send("你的权限：系统超级管理员");
                    break;
                case SYSTEM_ADMIN:
                    singleEvent.send("你的权限：系统管理员");
                    break;
                case MASTER:
                    singleEvent.send("你的权限：机器人主人");
                    break;
                case ADMIN:
                    singleEvent.send("你的权限：机器人管理员");
                    break;
                case MEMBER:
                    singleEvent.send("你的权限：普通成员");
                    break;
            }
        }
    }

    @Override
    public void handleFriend(SingleEvent singleEvent) {
        if (singleEvent.aboveBotMaster() && singleEvent.getMessage().plainEqual("授权码")) {
            singleEvent.send("授权码：");
        }
    }

    @Override
    public void handleGroup(SingleEvent singleEvent) {

    }
}
