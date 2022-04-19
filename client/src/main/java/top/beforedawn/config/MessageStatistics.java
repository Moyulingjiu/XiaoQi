package top.beforedawn.config;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.beforedawn.util.FileUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息统计类
 *
 * @author 墨羽翎玖
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageStatistics {
    private long dailyMessage = 0;
    private long totalMessage = 0;
    private ConcurrentHashMap<String, Long> plugins = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, ArrayList<LocalDateTime>> userRecord = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Long, LocalDateTime> remindTime = new ConcurrentHashMap<>();

    public static boolean getRemindTime(long qq) {
        clearRemindTime();
        return remindTime.containsKey(qq);
    }

    public static void putRemindTime(long qq) {
        clearRemindTime();
        remindTime.put(qq, LocalDateTime.now());
    }

    public static void clearRemindTime() {
        LocalDateTime now = LocalDateTime.now();
        ConcurrentHashMap<Long, LocalDateTime> newRemindTime = new ConcurrentHashMap<>();
        for (Long key : remindTime.keySet()) {
            if (Duration.between(remindTime.get(key), now).toMinutes() <= 1) {
                newRemindTime.put(key, remindTime.get(key));
            }
        }
        remindTime = newRemindTime;
    }

    public void load(String filename) {
        String content = FileUtil.readFile(filename);
        if (content.equals("")) {
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(content);
        dailyMessage = jsonObject.getLong("dailyMessage");
        totalMessage = jsonObject.getLong("totalMessage");
        JSONObject plugins = jsonObject.getJSONObject("plugins");
        for (String key : plugins.keySet()) {
            this.plugins.put(key, plugins.getLong(key));
        }
    }

    public void save(String filename) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dailyMessage", dailyMessage);
        jsonObject.put("totalMessage", totalMessage);
        jsonObject.put("plugins", plugins);
        FileUtil.writeFile(filename, jsonObject.toJSONString());
    }

    /**
     * 检验1分钟之内的消息数是否有超过阈值
     *
     * @param userId 用户id
     * @param frequency 阈值
     * @return boolean
     */
    public boolean check(Long userId, int frequency) {
        if (!userRecord.containsKey(userId)) {
            return false;
        }
        ArrayList<LocalDateTime> localDateTimes = userRecord.getOrDefault(userId, new ArrayList<>());
        // 记录已经超过一分钟的记录（清理过时的时间）
        ArrayList<LocalDateTime> validDateTimes = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        int total = 0;
        for (LocalDateTime localDateTime : localDateTimes) {
            Duration duration = Duration.between(localDateTime, now);
            if (duration.toMinutes() < 1) {
                total++;
                validDateTimes.add(localDateTime);
            }
        }
        userRecord.put(userId, validDateTimes);
        return total > frequency;
    }

    public void record(Long userId) {
        synchronized (this) {
            totalMessage++;
            dailyMessage++;
            // 记录当前用户的消息频率与水平
            ArrayList<LocalDateTime> userMessage = userRecord.getOrDefault(userId, new ArrayList<>());
            userMessage.add(LocalDateTime.now());
            userRecord.put(userId, userMessage);
        }
    }

    public void record(Long userId, String title) {
        Long number = plugins.getOrDefault(title, 0L);
        plugins.put(title, ++number);
        record(userId);
    }

    public void refresh() {
        dailyMessage = 0;
        plugins = new ConcurrentHashMap<>();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("全部消息：").append(totalMessage).append("\n");
        builder.append("每日消息：").append(dailyMessage);
        for (String key : plugins.keySet()) {
            builder.append("\n").append(key).append("模组消息：").append(plugins.get(key));
        }
        return builder.toString();
    }
}
