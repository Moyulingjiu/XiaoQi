package top.beforedawn.models.timed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import top.beforedawn.config.GroupPool;
import top.beforedawn.models.bo.BaseTimedTask;
import top.beforedawn.models.context.SerializeMessage;
import top.beforedawn.util.CommonUtil;
import top.beforedawn.util.SingleEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

/**
 * 群里定时任务
 *
 * @author 墨羽翎玖
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupTimedMessage implements BaseTimedTask {
    // 群号和名字就可以唯一确定一个任务
    private Long groupId;
    private String name;
    // 以下参数为发送的消息，并不用于唯一确定一个任务
    private ArrayList<SerializeMessage> reply;
    private BaseRepeatChecker checker;
    private LocalDateTime lastTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupTimedMessage that = (GroupTimedMessage) o;
        return Objects.equals(groupId, that.groupId) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, name);
    }

    @Override
    public TaskTime taskTime() {
        return checker.taskTime();
    }

    @Override
    public void execute(Bot bot, LocalDateTime now) {
        Group group = bot.getGroup(groupId);
        if (group == null) {
            return;
        }

        if (checker.isRepeat(lastTime, now)) {
            lastTime = now;
            SingleEvent singleEvent = new SingleEvent(bot.getId());
            singleEvent.setTitle("timed_message");
            singleEvent.setGroup(group);
            group.sendMessage(CommonUtil.getMessageChain(singleEvent, reply).asMessageChain());
            singleEvent.record();
            // 保存当前的变化，堆上的值已经发生改变了，所以此处可以直接调用保存
            GroupPool.save(singleEvent, groupId);
        }
    }

    @Override
    public String toString() {
        return checker.toString() + "发送“" + name + "”" + "\n" +
                "——上次发送时间：" + CommonUtil.LocalDateTime2String(lastTime) + "\n";
    }
}
