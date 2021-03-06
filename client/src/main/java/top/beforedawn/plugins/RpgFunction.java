package top.beforedawn.plugins;

import top.beforedawn.config.BotConfig;
import top.beforedawn.config.GroupPool;
import top.beforedawn.models.bo.MyGroup;
import top.beforedawn.util.HttpRequest;
import top.beforedawn.util.HttpResponse;
import top.beforedawn.util.SingleEvent;

public class RpgFunction extends BasePlugin {
    public RpgFunction() {
        pluginName = "RPG";
    }

    @Override
    public boolean before(SingleEvent singleEvent) {
        if (!singleEvent.isFriendMessage() && !singleEvent.isGroupMessage()) {
            return false;
        }
        if (!singleEvent.getConfig().isAllowRpg()) {
            return false;
        }
        if (singleEvent.isGroupMessage()) {
            MyGroup group = GroupPool.get(singleEvent);
            return group.isRpg();
        }
        return true;
    }

    @Override
    public void handleCommon(SingleEvent singleEvent) {
        String message = singleEvent.getMessage().getPlainString();
        if (!message.contains("@") && !message.contains("\n")) {
            StringBuilder atMessage = new StringBuilder();
            for (Long aLong : singleEvent.getMessage().getAt()) {
                atMessage.append("@").append(aLong);
            }
            atMessage.append(" ");
            String senderName = singleEvent.getSenderName();
            boolean limit = false;
            if (singleEvent.isGroupMessage()) {
                MyGroup group = GroupPool.get(singleEvent);
                limit = group.isRpgLimit();
            }

            String url = BotConfig.REMOTE_RPG_SERVER_IP + "/rpg";
            String json = "{\n" +
                    "    \"text\": \"" + (atMessage + message).strip() + "\",\n" +
                    "    \"qq\": " + singleEvent.getSenderId() + ",\n" +
                    "    \"member_name\": \"" + senderName + "\",\n" +
                    "    \"bot_name\": \"" + singleEvent.getBotName() + "\",\n" +
                    "    \"be_at\": " + singleEvent.getMessage().isBeAt() + ",\n" +
                    "    \"limit\": " + limit + "\n" +
                    "}";
            HttpResponse response = HttpRequest.sendPost(url, json);
            if (response.getCode() == 500) return;
            Boolean reply = response.getData().getBoolean("need_reply");
            if (reply != null && reply) {
                String replyMessage = response.getData().getString("reply_text");
                if (replyMessage.length() != 0) {
                    singleEvent.send(replyMessage);
                }
            }
        }
    }

    @Override
    public void handleFriend(SingleEvent singleEvent) {

    }

    @Override
    public void handleGroup(SingleEvent singleEvent) {

    }
}
