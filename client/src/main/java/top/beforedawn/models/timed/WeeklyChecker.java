package top.beforedawn.models.timed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 每周重复的检查器
 *
 * @author 墨羽翎玖
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyChecker extends BaseRepeatChecker {
    int day;
    int hour;
    int minute;

    @Override
    public boolean valid() {
        return day >= 1 && day <= 7 && hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59;
    }

    @Override
    public TaskTime taskTime() {
        return new TaskTime(hour, minute);
    }

    @Override
    public boolean isRepeat(LocalDateTime lastTime, LocalDateTime now) {
        if (lastTime != null) {
            if (now == null || Duration.between(lastTime, now).toDays() <= 6) {
                return false;
            }
        }
        return now.getDayOfWeek().getValue() == day && now.getHour() == hour && now.getMinute() == minute;
    }

    @Override
    public String toString() {
        return "每周" + day + "的" + hour + "点" + minute + "分";
    }
}
