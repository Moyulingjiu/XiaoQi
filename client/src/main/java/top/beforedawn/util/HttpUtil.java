package top.beforedawn.util;

import com.alibaba.fastjson.JSONObject;
import top.beforedawn.config.BotConfig;
import top.beforedawn.models.bo.*;

public class HttpUtil {
    private static final String serviceIp = "175.178.4.128";
//        private static final String serviceIp = "127.0.0.1";
    private static final int servicePort = 9001;

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
        if (response.getCode() != 0) {
            singleEvent.send("服务器出现错误，无法连接远程服务器\n代码：" + response.getCode());
            user.setLuck(50);
            return user;
        }
        System.out.println(response);
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
        if (response.getCode() != 0) {
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
        if (response.getCode() != 0) {
            singleEvent.send("服务器出现错误，无法连接远程服务器\n代码：" + response.getCode());
            return null;
        }
        bot.setId(response.getData().getLong("id"));
        bot.setQq(singleEvent.getBotId());
        bot.setPassword(response.getData().getString("password"));
        bot.setName(response.getData().getString("name"));
        bot.setOfficialGroup(response.getData().getLong("officialGroup"));
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

    public static boolean updateBot(SingleEvent singleEvent, BotConfig config) {
        String url = serverAddress() + "/bot/bot/" + singleEvent.getBotId();
        String json = "{\n" +
                "    \"operator\": " + singleEvent.getSenderId() + ",\n" +
                "    \"botId\": " + singleEvent.getBotId() + ",\n" +
                "    \"name\": \"" + config.getName() + "\",\n" +
                "    \"allowFriend\": " + (config.getBotSwitcher().isAllowFriend() ? 1 : 0) + ",\n" +
                "    \"allowGroup\": " + (config.getBotSwitcher().isAllowGroup() ? 1 : 0) + ",\n" +
                "    \"heartInterval\": " + config.getBotSwitcher().getHeartInterval() + ",\n" +
                "    \"heart\": " + (config.getBotSwitcher().isHeart() ? 1 : 0) + ",\n" +
                "    \"remindFriend\": " + (config.getBotSwitcher().isRemindFriend() ? 1 : 0) + ",\n" +
                "    \"remindGroup\": " + (config.getBotSwitcher().isRemindGroup() ? 1 : 0) + ",\n" +
                "    \"remindMute\": " + (config.getBotSwitcher().isRemindMute() ? 1 : 0) + ",\n" +
                "    \"remindQuit\": " + (config.getBotSwitcher().isRemindQuit() ? 1 : 0) + ",\n" +
                "    \"clearBlacklist\": " + (config.getBotSwitcher().isClearBlacklist() ? 1 : 0) + "\n" +
                "}";
        HttpResponse response = HttpRequest.sendPost(url, json);
        if (response.getCode() != 0) {
            singleEvent.send("服务器出现错误，无法连接远程服务器\n代码：" + response.getCode());
            return false;
        }
        return true;
    }

    public static Blacklist getBlacklist(SingleEvent singleEvent) {
        HttpResponse response = HttpRequest.sendGet(serverAddress() + "/blacklist/blacklists", "botId=" + singleEvent.getBotId());
        Blacklist blacklist = new Blacklist();
        if (response.getCode() != 0) {
            singleEvent.send("服务器出现错误，无法连接远程服务器\n代码：" + response.getCode());
            return null;
        }
        if (response.getData().getJSONArray("user") != null) {
            for (Object user : response.getData().getJSONArray("user")) {
                SimpleBlacklist simpleBlacklist = analysisBlacklist((JSONObject) user);
                blacklist.appendGlobalUser(simpleBlacklist);
            }
        }
        if (response.getData().getJSONArray("group") != null) {
            for (Object group : response.getData().getJSONArray("group")) {
                SimpleBlacklist simpleBlacklist = analysisBlacklist((JSONObject) group);
                blacklist.appendGlobalGroup(simpleBlacklist);
            }
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

    public static String getTalk(SingleEvent singleEvent, byte type) {
        HttpResponse response = HttpRequest.sendGet(serverAddress() + "/abstract/abstract", "botId=" + singleEvent.getBotId() + "&type=" + type);
        if (response.getCode() == 404) return "暂无";
        else if (response.getCode() != 0) {
            singleEvent.send("服务器出现错误，无法连接远程服务器\n代码：" + response.getCode());
            return null;
        }
        return response.getData().getString("text");
    }

    public static boolean sendTalk(SingleEvent singleEvent, String text, int type) {
        String url = serverAddress() + "/abstract/abstract";
        String json = "{\n" +
                "    \"text\": \"" + text + "\",\n" +
                "    \"type\": " + type + ",\n" +
                "    \"userId\": " + singleEvent.getSenderId() + ",\n" +
                "    \"botId\": " + singleEvent.getBotId() + "\n" +
                "}";
        HttpResponse response = HttpRequest.sendPost(url, json);
        if (response.getCode() != 0) {
            singleEvent.send("服务器出现错误，无法连接远程服务器\n代码：" + response.getCode());
            return false;
        }
        return true;
    }

    public static String getQingYunKe(String message) {
        HttpResponse response = HttpRequest.sendGet("http://api.qingyunke.com/api.php", "key=free&appid=0&msg=" + message);
        return response.getData().getString("content");
    }
}
