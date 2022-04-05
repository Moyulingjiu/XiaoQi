package top.beforedawn.models.timed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskTime {
    private int hour;
    private int minute;

    public static TaskTime now() {
        LocalDateTime now = LocalDateTime.now();
        return new TaskTime(now.getHour(), now.getMinute());
    }

    public static TaskTime get(LocalDateTime now) {
        return new TaskTime(now.getHour(), now.getMinute());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskTime taskTime = (TaskTime) o;
        return hour == taskTime.hour && minute == taskTime.minute;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hour, minute);
    }
}
