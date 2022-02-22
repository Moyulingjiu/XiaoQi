package top.beforedawn.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private Map<String, Long> plugins = new HashMap<>();
    private Map<Long, ArrayList<LocalDateTime>> userRecord = new HashMap<>();

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
        totalMessage++;
        dailyMessage++;
        // 记录当前用户的消息频率与水平
        ArrayList<LocalDateTime> userMessage = userRecord.getOrDefault(userId, new ArrayList<>());
        userMessage.add(LocalDateTime.now());
        userRecord.put(userId, userMessage);
    }

    public void record(Long userId, String title) {
        plugins.put(title, plugins.getOrDefault(title, 1L));
        record(userId);
    }

    public void refresh() {
        dailyMessage = 0;
        plugins = new HashMap<>();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("全部消息：").append(totalMessage).append("\n");
        builder.append("每日消息：").append(dailyMessage).append("\n");
        for (String key : plugins.keySet()) {
            builder.append(key).append("模组：").append(plugins.get(key)).append("\n");
        }
        return builder.toString();
    }
}
