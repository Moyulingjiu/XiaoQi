package top.beforedawn.util;

import com.alibaba.fastjson.JSONArray;
import top.beforedawn.models.bo.Blacklist;
import top.beforedawn.models.bo.BotRemoteInformation;
import top.beforedawn.models.bo.MyUser;

public class HttpUtil {
    private static final String serviceIp = "localhost";
    private static final int servicePort = 8080;

    private static String serverAddress() {
        StringBuilder builder = new StringBuilder();
        builder.append("http:").append("//").append(serviceIp);
        if (servicePort != 0) {
            builder.append(":").append(servicePort);
        }
        return builder.toString();
    }

    public static MyUser getUser(SingleEvent singleEvent) {
        return new MyUser();
    }

    public static MyUser getLuck(SingleEvent singleEvent) {
        HttpResponse response = HttpRequest.sendGet(serverAddress() + "/user/luck/" + singleEvent.getSenderId(), "botId=" + singleEvent.getBotId());
        MyUser user = new MyUser();
        user.setId(singleEvent.getSenderId());
        if (response.getCode()!= 0) {
            singleEvent.send("服务器出现错误，无法连接远程服务器\n代码：" + response.getCode());
            user.setLuck(50);
            return user;
        }
        user.setLuck(response.getData().getInteger("luck"));
        return user;
    }

    public static BotRemoteInformation getBot(SingleEvent singleEvent) {
        HttpResponse response = HttpRequest.sendGet(serverAddress() + "/bot/bot/" + singleEvent.getBotId(), "");
        BotRemoteInformation bot = new BotRemoteInformation();
        if (response.getCode()!= 0) {
            singleEvent.send("服务器出现错误，无法连接远程服务器\n代码：" + response.getCode());
            return null;
        }
        bot.setId(response.getData().getLong("id"));
        bot.setQq(singleEvent.getBotId());
        bot.setPassword(response.getData().getString("password"));
        bot.setName(response.getData().getString("name"));
        bot.setMasterQq(response.getData().getLong("masterQq"));
        bot.setKeyId(response.getData().getLong("keyId"));
        bot.setValid(response.getData().getBoolean("valid"));
        bot.setAllowFriend(response.getData().getInteger("allowFriend"));
        bot.setAllowGroup(response.getData().getInteger("allowGroup"));
        bot.setHeart(response.getData().getInteger("heart"));
        bot.setHeartInterval(response.getData().getInteger("heartInterval"));
        bot.setRemindFriend(response.getData().getInteger("remindFriend"));
        bot.setRemindGroup(response.getData().getInteger("remindGroup"));
        bot.setRemindMute(response.getData().getInteger("remindMute"));
        bot.setRemindQuit(response.getData().getInteger("remindQuit"));
        bot.setClearBlacklist(response.getData().getInteger("clearBlacklist"));
        bot.setKeyValue(response.getData().getString("keyValue"));
        bot.setKeyUserId(response.getData().getLong("keyUserId"));
        bot.setKeyValidBeginDate(CommonUtil.getLocalDateTime(response.getData().getString("keyValidBeginDate")));
        bot.setKeyValidEndDate(CommonUtil.getLocalDateTime(response.getData().getString("keyValidEndDate")));

        bot.setKeyType(response.getData().getString("keyType"));

        bot.setModified(CommonUtil.getLocalDateTime(response.getData().getString("modified")));
        bot.setModifiedId(response.getData().getLong("modifiedId"));
        bot.setCreate(CommonUtil.getLocalDateTime(response.getData().getString("create")));
        bot.setCreateId(response.getData().getLong("createId"));
        return bot;
    }

    public static Blacklist getBlacklist(SingleEvent singleEvent) {
        HttpResponse response = HttpRequest.sendGet(serverAddress() + "/blacklist/blacklists" , "botId=" + singleEvent.getBotId());
        Blacklist blacklist = new Blacklist();
        if (response.getCode()!= 0) {
            singleEvent.send("服务器出现错误，无法连接远程服务器\n代码：" + response.getCode());
            return null;
        }
        JSONArray user = response.getData().getJSONArray("user");
        return blacklist;
    }
}
