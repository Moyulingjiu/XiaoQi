package top.beforedawn.config;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import top.beforedawn.models.bo.BaseTimedTask;
import top.beforedawn.models.timed.TaskTime;
import top.beforedawn.util.CommonUtil;
import top.beforedawn.util.MyBot;
import top.beforedawn.util.SingleEvent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 单例的背景任务管理器
 *
 * @author 墨羽翎玖
 */
public class BackgroundTask implements Runnable {
    static class BackgroundTaskHandler {
        public static BackgroundTask instance = new BackgroundTask();
    }

    public static BackgroundTask getInstance() {
        return BackgroundTaskHandler.instance;
    }

    private BackgroundTask() {

    }

    public Bot bot;
    public BotConfig config;
    private final ConcurrentHashMap<TaskTime, LinkedList<BaseTimedTask>> tasks = new ConcurrentHashMap<>();

    public int size() {
        int ans = 0;
        for (TaskTime time : tasks.keySet()) {
            LinkedList<BaseTimedTask> list = tasks.get(time);
            if (list != null) {
                ans += list.size();
            }
        }
        return ans;
    }

    public void clear() {
        tasks.clear();
    }

    public void remove(BaseTimedTask task) {
        LinkedList<BaseTimedTask> tasks = this.tasks.get(task.taskTime());
        if (tasks != null) {
            if (tasks.remove(task)) {
                return;
            }
        }
        for (TaskTime taskTime : this.tasks.keySet()) {
            tasks = this.tasks.get(taskTime);
            for (BaseTimedTask timedTask : tasks) {
                if (timedTask.equals(task)) {
                    tasks.remove(timedTask);
                    break;
                }
            }
        }
    }

    public void put(BaseTimedTask task) {
        if (tasks.containsKey(task.taskTime())) {
            LinkedList<BaseTimedTask> baseTimedTasks = tasks.get(task.taskTime());
            if (!baseTimedTasks.contains(task)) {
                baseTimedTasks.add(task);
            }
        } else {
            LinkedList<BaseTimedTask> list = new LinkedList<>();
            list.add(task);
            tasks.put(task.taskTime(), list);
        }
    }

    @Override
    public void run() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        SingleEvent singleEvent = new SingleEvent(bot.getId());
        singleEvent.setTitle("watcher");
        // 这里设置时间为当前时间之前的一分钟，可以保证程序运行的一瞬间，就可以执行。
        LocalDateTime last = LocalDateTime.now().minusMinutes(1);
        LocalDateTime lastHeart = LocalDateTime.now();

        while (true) {
            // 保证时间一致性十分重要！！！这个now将会作为这一个tick的时间。
            LocalDateTime now = LocalDateTime.now();
            if (last.getMinute() != now.getMinute() || Duration.between(last, now).toMinutes() >= 1) {
                last = now;

                // 检查定时任务
                TaskTime taskTime = TaskTime.get(now);
                LinkedList<BaseTimedTask> tasks = this.tasks.get(taskTime);
                if (tasks != null) {
                    for (BaseTimedTask task : tasks) {
                        executorService.submit(() -> task.execute(bot, now));
                    }
                }

                // 心跳报时
                if (config.getBotSwitcher().isHeart()) {
                    if (Duration.between(lastHeart, now).toHours() >= config.getBotSwitcher().getHeartInterval()) {
                        lastHeart = now;
                        Friend friend = bot.getFriend(config.getMaster());
                        if (friend != null) {
                            friend.sendMessage("心跳报告，当前时间" + CommonUtil.LocalDateTime2String(now) + "\n" +
                                    "当前版本：" + BotConfig.VERSION);
                        }
                    }
                }

                // 每日零点清空数据
                if (now.getHour() == 0 && now.getMinute() == 0) {
                    String msg = "当前版本：" + BotConfig.VERSION + "\n" + config.getStatistics().toString();
                    config.getStatistics().refresh();
                    Group group = bot.getGroup(config.getOfficialGroup());
                    if (group != null) {
                        singleEvent.record();
                        group.sendMessage(msg);
                    } else {
                        Friend friend = bot.getFriend(config.getMaster());
                        if (friend != null) {
                            friend.sendMessage(msg);
                        }
                    }
                }
            }
            try {
                // 如果当前时间与上一次时间没有相差超过一秒，则休眠900ms。
                if (LocalDateTime.now().getSecond() == now.getSecond()) {
                    Thread.sleep(900);
                }
                config = MyBot.getSimpleCombineBot(bot.getId(), singleEvent).getConfig();
//                System.out.println("背景任务tick");
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }

        executorService.shutdown();
    }
}
