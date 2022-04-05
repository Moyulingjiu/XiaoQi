package top.beforedawn.models.timed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 每年重复的检查器
 *
 * @author 墨羽翎玖
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearlyChecker extends BaseRepeatChecker {
    int month;
    int day;
    int hour;
    int minute;

    @Override
    public boolean valid() {
        return month > 0 && month < 13 && day > 0 && day < 32 && hour >= 0 && hour < 24 && minute >= 0 && minute < 60;
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
        if (now == null || Duration.between(lastTime, now).toDays() <= 360) {
            return false;
        }
        return now.getMonthValue() == month && now.getDayOfMonth() == day && now.getHour() == hour && now.getMinute() == minute;
    }

    @Override
    public String toString() {
        return "每年的" + month + "月" + day + "日" + hour + "点" + minute + "分";
    }
}
