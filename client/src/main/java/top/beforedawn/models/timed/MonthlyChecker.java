package top.beforedawn.models.timed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 每月重复的检查器
 *
 * @author 墨羽翎玖
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyChecker extends BaseRepeatChecker {
    int date;
    int hour;
    int minute;

    @Override
    public boolean valid() {
        return date > 0 && date <= 31 && hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59;
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
        if (now == null || Duration.between(lastTime, now).toDays() <= 27) {
            return false;
        }
        return now.getDayOfMonth() == date && now.getHour() == hour && now.getMinute() == minute;
    }

    @Override
    public String toString() {
        return "每月" + date + "日" + hour + "点" + minute + "分";
    }
}
