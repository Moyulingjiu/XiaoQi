package top.beforedawn.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import top.beforedawn.models.bo.*;

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

    public static String convention(long botId) {
        HttpResponse response = HttpRequest.sendGet(serverAddress() + "/bot/convention", "botId=" + botId);
        if (response.getCode() != 0) {
            return "（似乎与主服务器断开了连接，暂时无法获取公约）";
        }
        return response.getData().getString("data");
    }

    public static MyUser getUser(SingleEvent singleEvent) {
        HttpResponse response = HttpRequest.sendGet(serverAddress() + "/user/user/" + singleEvent.getSenderId(), "botId=" + singleEvent.getBotId());
        MyUser user = new MyUser();
        user.setId(singleEvent.getSenderId());
        if (response.getCode()!= 0) {
            singleEvent.send("服务器出现错误，无法连接远程服务器\n代码：" + response.getCode());
            user.setLuck(50);
            return user;
        }
        user.setId(response.getData().getLong("id"));
        user.setQq(response.getData().getLong("qq"));
        user.setLastChangePassword(CommonUtil.getLocalDateTime(response.getData().getString("lastChangePassword")));
        user.setNickname(response.getData().getString("nickname"));
        user.setUseNickname(response.getData().getInteger("useNickname"));
        user.setRight(response.getData().getString("right"));
        user.setPoint(response.getData().getInteger("point"));
        user.setLuck(response.getData().getInteger("luck"));
        user.setLastLuck(CommonUtil.getLocalDateTime(response.getData().getString("lastLuck")));
        user.setModified(CommonUtil.getLocalDateTime(response.getData().getString("modified")));
        user.setCreate(CommonUtil.getLocalDateTime(response.getData().getString("create")));
        return user;
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
        bot.setAllowCoc(response.getData().getByte("allowCoc"));
        bot.setAllowRpg(response.getData().getByte("allowRpg"));
        bot.setAllowPic(response.getData().getByte("allowPic"));
        bot.setAllowAssistant(response.getData().getByte("allowAssistant"));

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
        for (Object user : response.getData().getJSONArray("user")) {
            SimpleBlacklist simpleBlacklist = analysisBlacklist((JSONObject) user);
            blacklist.appendGlobalUser(simpleBlacklist);
        }
        for (Object group : response.getData().getJSONArray("group")) {
            SimpleBlacklist simpleBlacklist = analysisBlacklist((JSONObject) group);
            blacklist.appendGlobalGroup(simpleBlacklist);
        }
        return blacklist;
    }

    public static SimpleBlacklist analysisBlacklist(JSONObject object) {
        SimpleBlacklist simpleBlacklist = new SimpleBlacklist();
        simpleBlacklist.setKey(object.getLong("key"));
        simpleBlacklist.setComment(object.getString("comment"));
        simpleBlacklist.setRemind(object.getLong("remind"));
        if (object.getString("lastRemindTime") != null) {
            simpleBlacklist.setLastRemindTime(CommonUtil.getLocalDateTime(object.getString("lastRemindTime")));
        }
        simpleBlacklist.setModified(CommonUtil.getLocalDateTime(object.getString("modified")));
        simpleBlacklist.setModifiedId(object.getLong("modifiedId"));
        simpleBlacklist.setCreate(CommonUtil.getLocalDateTime(object.getString("create")));
        simpleBlacklist.setCreateId(object.getLong("createId"));
        return simpleBlacklist;
    }

    public static HttpResponse sendDriftingBottle(SingleEvent singleEvent, String text) {
        String url = serverAddress() + "/drifting_bottle/drifting_bottle";
        String json = "{\n" +
                "    \"botId\": " + singleEvent.getBotId() + ",\n" +
                "    \"userId\": " + singleEvent.getSenderId() + ",\n" +
                "    \"text\": \"" + text + "\",\n" +
                "    \"permanent\": 0\n" +
                "}";
        HttpResponse response = HttpRequest.sendPost(url, json);
        if (response.getCode() != 0) {
            singleEvent.send("服务器出现错误，无法连接远程服务器\n代码：" + response.getCode());
            return null;
        }
        return response;
    }

    public static DriftingBottle getDriftingBottle(SingleEvent singleEvent) {
        String url = serverAddress() + "/drifting_bottle/drifting_bottle";
        HttpResponse response = HttpRequest.sendGet(url, "botId=" + singleEvent.getBotId() + "&userId=" + singleEvent.getSenderId());
        if (response.getCode() == 404) {
            singleEvent.send("没有漂流瓶了呢~不妨扔一个");
            return null;
        } else if (response.getCode() != 0) {
            singleEvent.send("服务器出现错误，无法连接远程服务器\n代码：" + response.getCode());
            return null;
        }
        DriftingBottle driftingBottle = new DriftingBottle();
        driftingBottle.setText(response.getData().getString("text"));
        driftingBottle.setCreate(CommonUtil.getLocalDateTime(response.getData().getString("create")));
        return driftingBottle;
    }
}
