package top.beforedawn.util;

import top.beforedawn.models.bo.Blacklist;
import top.beforedawn.models.bo.SimpleBlacklist;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * 配置类的工具类
 * BotConfig类其所有静态函数在此class中
 *
 * @author 墨羽翎玖
 */
public class ConfigUtil {

    /**
     * 分析黑名单
     *
     * @param data 数据
     * @return 黑名单列表
     */
    public static Blacklist analysisBlacklist(ArrayList<Object> data) {
        Blacklist blacklist = new Blacklist();
        if (data != null) {
            for (Object datum : data) {
                if (datum instanceof LinkedHashMap) {
                    LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) datum;
                    SimpleBlacklist simpleBlacklist = new SimpleBlacklist();
                    if (map.containsKey("key")) {
                        simpleBlacklist.setKey((Long) CommonUtil.decorateValue(map.get("key")));
                    }
                    if (map.containsKey("comment")) {
                        simpleBlacklist.setComment((String) map.get("comment"));
                    }
                    if (map.containsKey("remind")) {
                        simpleBlacklist.setRemind(((Integer) map.get("remind")).longValue());
                    }
                    if (map.containsKey("last_remind_time")) {
                        simpleBlacklist.setLastRemindTime(CommonUtil.Date2LocalDateTime((Date) map.get("last_remind_time")));
                    }
                    if (map.containsKey("create_id")) {
                        simpleBlacklist.setCreateId(((Integer) map.get("create_id")).longValue());
                    }
                    if (simpleBlacklist.getKey() != null) {
                        blacklist.append(simpleBlacklist);
                    }
                }
            }
        }
        return blacklist;
    }

}
