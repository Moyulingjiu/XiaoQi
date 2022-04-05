package top.beforedawn.models.timed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 每天重复的检查器
 *
 * @author 墨羽翎玖
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyChecker extends BaseRepeatChecker {
    int hour;
    int minute;

    @Override
    public boolean valid() {
        return hour >= 0 && hour < 24 && minute >= 0 && minute < 60;
    }

    @Override
    public TaskTime taskTime() {
        return new TaskTime(hour, minute);
    }

    @Override
    public boolean isRepeat(LocalDateTime lastTime, LocalDateTime now) {
        if (lastTime == null) {
            return true;
        }
        if (now == null || Duration.between(lastTime, now).toHours() <= 23) {
            return false;
        }
        return now.getHour() == hour && now.getMinute() == minute;
    }

    @Override
    public String toString() {
        return "每天" + hour + "点" + minute + "分";
    }
}
