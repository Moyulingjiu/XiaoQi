package top.beforedawn.plugins;

import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import top.beforedawn.config.BotConfig;
import top.beforedawn.config.GroupPool;
import top.beforedawn.config.RequestEventPool;
import top.beforedawn.config.UserPool;
import top.beforedawn.models.bo.*;
import top.beforedawn.util.CommonUtil;
import top.beforedawn.util.HttpUtil;
import top.beforedawn.util.SingleEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class BotFunction extends BasePlugin {
    private static final int REQUEST_PAGE_SIZE = 20;

    public BotFunction() {
        pluginName = "bot_function";
    }

    private void information(SingleEvent singleEvent) {
        if (singleEvent.getMessage().plainEqual("机器人信息")) {
            StringBuilder builder = new StringBuilder();
            builder.append("机器人id：").append(singleEvent.getConfig().getQq()).append("\n");
            builder.append("名字：").append(singleEvent.getConfig().getName()).append("\n");
            builder.append("主人：").append(singleEvent.getConfig().getMaster()).append("\n");
            if (singleEvent.getConfig().getKeyType().equals("NORMAL")) {
                builder
                        .append("有效期：")
                        .append(CommonUtil.LocalDateTime2String(singleEvent.getConfig().getKeyValidBeginDate()))
                        .append("—")
                        .append(CommonUtil.LocalDateTime2String(singleEvent.getConfig().getKeyValidEndDate()));
                if (LocalDateTime.now().isAfter(singleEvent.getConfig().getKeyValidEndDate())) {
                    builder.append("（已过期）");
                }
            } else {
                builder.append("有效期：无限期");
            }
            builder.append("\n").append("版本信息：").append(BotConfig.VERSION);
            builder.append("\n").append("激活的附加功能：");
            if (singleEvent.getConfig().isAllowCoc()) {
                builder.append(" coc模块");
            }
            if (singleEvent.getConfig().isAllowRpg()) {
                builder.append(" rpg游戏模块");
            }
            if (singleEvent.getConfig().isAllowPic()) {
                builder.append(" Pixiv搜图模块");
            }
            if (singleEvent.getConfig().isAllowTrpg()) {
                builder.append(" 骰娘模块");
            }
            if (singleEvent.getConfig().isAllowAssistant()) {
                builder.append(" 个人助理模块");
            }

            singleEvent.send(builder.toString());
        } else if (singleEvent.getMessage().plainEqual("统计信息")) {
            StringBuilder builder = new StringBuilder(singleEvent.getConfig().getStatistics().toString());
            builder.append("\n").append("缓存User：").append(UserPool.size()).append("/").append(UserPool.POOL_MAX);
            builder.append("\n").append("缓存Group：").append(GroupPool.size()).append("/").append(GroupPool.POOL_MAX);
            builder.append("\n").append("版本信息：").append(BotConfig.VERSION);
            singleEvent.send(builder.toString());
        } else if (singleEvent.getMessage().plainEqual("我的权限")) {
            switch (singleEvent.getRight()) {
                case SYSTEM_SUPER_ADMIN:
                    singleEvent.send("你的权限：系统超级管理员");
                    break;
                case SYSTEM_ADMIN:
                    singleEvent.send("你的权限：系统管理员");
                    break;
                case MASTER:
                    singleEvent.send("你的权限：机器人主人");
                    break;
                case ADMIN:
                    singleEvent.send("你的权限：机器人管理员");
                    break;
                case MEMBER:
                    singleEvent.send("你的权限：普通成员");
                    break;
            }
        } else if (singleEvent.getMessage().plainEqual("我的积分")) {
            MyUser user = UserPool.getUser(singleEvent);
            singleEvent.send("你的积分：" + user.getPoint());
        } else if (singleEvent.getMessage().plainEqual("版本信息")) {
            singleEvent.send("版本信息：" + BotConfig.VERSION);
        }
    }

    private void blacklist(SingleEvent singleEvent) {
        if (singleEvent.getMessage().plainEqual("查看全局黑名单人")) {
            if (singleEvent.aboveSystemAdmin()) {
                Blacklist blacklist = singleEvent.getConfig().getBlacklist();
                singleEvent.send(blacklist.showGlobalUser());
            } else {
                singleEvent.send("权限不足");
            }
        } else if (singleEvent.getMessage().plainEqual("查看全局黑名单群")) {
            if (singleEvent.aboveSystemAdmin()) {
                Blacklist blacklist = singleEvent.getConfig().getBlacklist();
                singleEvent.send(blacklist.showGlobalGroup());
            } else {
                singleEvent.send("权限不足");
            }
        } else if (singleEvent.getMessage().plainEqual("查看黑名单人")) {
            if (singleEvent.aboveBotAdmin()) {
                Blacklist blacklist = singleEvent.getConfig().getBlacklist();
                singleEvent.send(blacklist.showUser());
            } else {
                singleEvent.send("权限不足");
            }
        } else if (singleEvent.getMessage().plainEqual("查看黑名单群")) {
            if (singleEvent.aboveBotAdmin()) {
                Blacklist blacklist = singleEvent.getConfig().getBlacklist();
                singleEvent.send(blacklist.showGroup());
            } else {
                singleEvent.send("权限不足");
            }
        } else if (singleEvent.getMessage().plainStartWith("添加黑名单人")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("添加黑名单人");
            ArrayList<String> split = analysis.split();
            boolean flag = false;
            if (split.size() == 2) {
                long id = CommonUtil.getLong(split.get(0));
                if (id > 0L) {
                    flag = true;
                    if (singleEvent.aboveBotAdmin()) {
                        if (id != singleEvent.getConfig().getMaster()) {
                            SimpleBlacklist simpleBlacklist = new SimpleBlacklist();
                            simpleBlacklist.setKey(id);
                            simpleBlacklist.setComment(split.get(1));
                            simpleBlacklist.setCreateId(singleEvent.getSenderId());
                            simpleBlacklist.setCreate(LocalDateTime.now());
                            simpleBlacklist.setModifiedId(singleEvent.getSenderId());
                            simpleBlacklist.setModified(LocalDateTime.now());
                            singleEvent.getConfig().getBlacklist().appendUser(simpleBlacklist);
                            singleEvent.getConfig().save();
                            singleEvent.send("添加成功~");
                        } else {
                            singleEvent.send("不可以把主人添加入黑名单");
                        }
                    } else {
                        singleEvent.send("权限不足");
                    }
                }
            }
            if (!flag) {
                singleEvent.send("格式错误！");
            }
        } else if (singleEvent.getMessage().plainStartWith("删除黑名单人")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("删除黑名单人");
            ArrayList<String> split = analysis.split();
            boolean flag = false;
            if (split.size() == 1) {
                long id = CommonUtil.getLong(split.get(0));
                if (id > 0L) {
                    flag = true;
                    if (singleEvent.aboveBotAdmin()) {
                        if (singleEvent.getConfig().getBlacklist().removeUser(id)) {
                            singleEvent.getConfig().save();
                            singleEvent.send("删除成功~");
                        } else {
                            singleEvent.send("该人不在黑名单中~");
                        }
                    } else {
                        singleEvent.send("权限不足");
                    }
                }
            }
            if (!flag) {
                singleEvent.send("格式错误！");
            }
        } else if (singleEvent.getMessage().plainStartWith("添加黑名单群")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("添加黑名单群");
            ArrayList<String> split = analysis.split();
            boolean flag = false;
            if (split.size() == 2) {
                long id = CommonUtil.getLong(split.get(0));
                if (id > 0L) {
                    flag = true;
                    if (singleEvent.aboveBotAdmin()) {
                        if (id != singleEvent.getConfig().getOfficialGroup()) {
                            SimpleBlacklist simpleBlacklist = new SimpleBlacklist();
                            simpleBlacklist.setKey(id);
                            simpleBlacklist.setComment(split.get(1));
                            simpleBlacklist.setCreateId(singleEvent.getSenderId());
                            simpleBlacklist.setCreate(LocalDateTime.now());
                            simpleBlacklist.setModifiedId(singleEvent.getSenderId());
                            simpleBlacklist.setModified(LocalDateTime.now());
                            singleEvent.getConfig().getBlacklist().appendGroup(simpleBlacklist);
                            singleEvent.getConfig().save();
                            singleEvent.send("添加成功~");
                        } else {
                            singleEvent.send("不可以添加官方群");
                        }
                    } else {
                        singleEvent.send("权限不足");
                    }
                }
            }
            if (!flag) {
                singleEvent.send("格式错误！");
            }
        } else if (singleEvent.getMessage().plainStartWith("删除黑名单群")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("删除黑名单群");
            ArrayList<String> split = analysis.split();
            boolean flag = false;
            if (split.size() == 1) {
                long id = CommonUtil.getLong(split.get(0));
                if (id > 0L) {
                    flag = true;
                    if (singleEvent.aboveBotAdmin()) {
                        if (singleEvent.getConfig().getBlacklist().removeGroup(id)) {
                            singleEvent.getConfig().save();
                            singleEvent.send("删除成功~");
                        } else {
                            singleEvent.send("该群不在黑名单中~");
                        }
                    } else {
                        singleEvent.send("权限不足");
                    }
                }
            }
            if (!flag) {
                singleEvent.send("格式错误！");
            }
        }
    }

    private void admin(SingleEvent singleEvent) {
        if (singleEvent.getMessage().plainEqual("查看机器人管理员")) {
            if (singleEvent.aboveBotMaster()) {
                ArrayList<Long> administrator = singleEvent.getConfig().getAdministrator();
                StringBuilder builder = new StringBuilder();
                boolean init = false;
                for (Long id : administrator) {
                    if (!init) {
                        init = true;
                    } else {
                        builder.append("、");
                    }
                    builder.append(id);
                }
                if (administrator.size() == 0) {
                    builder.append("（暂无）");
                    ;
                }
                singleEvent.send(builder.toString());
            } else {
                singleEvent.send("权限不足");
            }
        } else if (singleEvent.getMessage().plainStartWith("添加机器人管理员")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("添加机器人管理员");
            ArrayList<String> split = analysis.split();
            boolean flag = false;
            if (split.size() == 1) {
                long id = CommonUtil.getLong(split.get(0));
                if (id > 0L) {
                    flag = true;
                    if (singleEvent.aboveBotMaster()) {
                        if (!singleEvent.getConfig().getAdministrator().contains(id)) {
                            singleEvent.getConfig().getAdministrator().add(id);
                            singleEvent.getConfig().save();
                            singleEvent.send("添加成功~");
                        } else {
                            singleEvent.send("该成员已经是" + singleEvent.getBotName() + "的管理员");
                        }
                    } else {
                        singleEvent.send("权限不足");
                    }
                }
            }
            if (!flag) {
                singleEvent.send("格式错误！");
            }
        } else if (singleEvent.getMessage().plainStartWith("删除机器人管理员")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("删除机器人管理员");
            ArrayList<String> split = analysis.split();
            boolean flag = false;
            if (split.size() == 1) {
                long id = CommonUtil.getLong(split.get(0));
                if (id > 0L) {
                    flag = true;
                    if (singleEvent.aboveBotMaster()) {
                        if (singleEvent.getConfig().getAdministrator().contains(id)) {
                            singleEvent.getConfig().getAdministrator().remove(id);
                            singleEvent.getConfig().save();
                            singleEvent.send("删除成功~");
                        } else {
                            singleEvent.send("该成员不是" + singleEvent.getBotName() + "的管理员");
                        }
                    } else {
                        singleEvent.send("权限不足");
                    }
                }
            }
            if (!flag) {
                singleEvent.send("格式错误！");
            }
        }
    }

    private boolean check(
            SingleEvent singleEvent,
            boolean right,
            boolean origin,
            boolean target,
            String unableChange
    ) {
        if (right) {
            if (origin == target) {
                singleEvent.send(unableChange);
            } else {
                if (target) {
                    singleEvent.send("开启成功");
                } else {
                    singleEvent.send("关闭成功");
                }
                return true;
            }
        } else {
            singleEvent.send("你无权执行该操作");
        }
        return false;
    }

    private String getSwitcher(boolean switcher) {
        if (switcher)
            return "是";
        return "否";
    }

    private void switcher(SingleEvent singleEvent) {
        MyGroup group = GroupPool.get(singleEvent);
        if (singleEvent.getMessage().plainEqual("全局模块列表")) {
            String message = "版本信息：" + BotConfig.VERSION + "\n" +
                    "是否允许使用coc模块：" + getSwitcher(singleEvent.getConfig().isAllowCoc()) + "\n" +
                    "是否允许使用rpg模块：" + getSwitcher(singleEvent.getConfig().isAllowRpg()) + "\n" +
                    "是否允许使用trpg模块：" + getSwitcher(singleEvent.getConfig().isAllowTrpg()) + "\n" +
                    "是否允许使用pixiv模块：" + getSwitcher(singleEvent.getConfig().isAllowPic()) + "\n" +
                    "是否允许使用个人助理模块：" + getSwitcher(singleEvent.getConfig().isAllowAssistant()) + "\n" +
                    "是否允许添加好友：" + getSwitcher(singleEvent.getConfig().getBotSwitcher().isAllowFriend()) + "\n" +
                    "是否允许添加群：" + getSwitcher(singleEvent.getConfig().getBotSwitcher().isAllowGroup()) + "\n" +
                    "是否订阅新好友提醒：" + getSwitcher(singleEvent.getConfig().getBotSwitcher().isRemindFriend()) + "\n" +
                    "是否订阅群邀请提醒：" + getSwitcher(singleEvent.getConfig().getBotSwitcher().isRemindGroup()) + "\n" +
                    "是否订阅禁言提醒：" + getSwitcher(singleEvent.getConfig().getBotSwitcher().isRemindMute()) + "\n" +
                    "是否订阅退群提醒：" + getSwitcher(singleEvent.getConfig().getBotSwitcher().isRemindQuit()) + "\n" +
                    "是否进行心跳检测：" + getSwitcher(singleEvent.getConfig().getBotSwitcher().isHeart()) + "\n" +
                    "心跳检测间隔：" + singleEvent.getConfig().getBotSwitcher().getHeartInterval();
            singleEvent.send(message);
        } else if (singleEvent.getMessage().plainEqual("模块列表")) {
            String message = "版本信息：" + BotConfig.VERSION + "\n" +
                    "是否处在限制模式：" + getSwitcher(group.isLimit()) + "\n" +
                    "是否开启戳一戳：" + getSwitcher(group.isNudge()) + "\n" +
                    "是否开启解除闪照：" + getSwitcher(group.isUnlockFlashImage()) + "\n" +
                    "是否开启防撤回：" + getSwitcher(group.isRecallGuard()) + "\n" +
                    "是否开启成员监控：" + getSwitcher(group.isMemberWatcher()) + "\n" +
                    "是否开启自定义回复：" + getSwitcher(group.isAutoReply()) + "\n" +
                    "是否开启自动加一：" + getSwitcher(group.isRepeat()) + "\n" +
                    "是否开启自动入群审核：" + getSwitcher(group.isGroupEntry());
            singleEvent.send(message);
        }
        // 单独控制每个开关
        else if (singleEvent.getMessage().plainEqual("开启限制模式")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isLimit(), true, "已经在限制模式了")) {
                group.setLimit(true);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("关闭限制模式")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isLimit(), false, "没有在限制模式哦")) {
                group.setLimit(false);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("开启成员监控")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isMemberWatcher(), true, "已经开了成员监控了")) {
                group.setMemberWatcher(true);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("关闭成员监控")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isMemberWatcher(), false, "本来就没有开启成员监控")) {
                group.setMemberWatcher(false);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("开启解除闪照")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isUnlockFlashImage(), true, "已经开启了解除闪照")) {
                group.setUnlockFlashImage(true);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("关闭解除闪照")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isUnlockFlashImage(), false, "本来就没有开启解除闪照")) {
                group.setUnlockFlashImage(false);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("开启戳一戳")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isNudge(), true, "已经开启了戳一戳")) {
                group.setNudge(true);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("关闭戳一戳")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isNudge(), false, "本来就没有开启戳一戳")) {
                group.setNudge(false);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("开启自定义回复")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isAutoReply(), true, "已经开启了自定义回复")) {
                group.setAutoReply(true);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("关闭自定义回复")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isAutoReply(), false, "本来就没有开启自定义回复")) {
                group.setAutoReply(false);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("开启加一")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isRepeat(), true, "已经开启了加一")) {
                group.setRepeat(true);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("关闭加一")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isRepeat(), false, "本来就没有开启加一")) {
                group.setRepeat(false);
                GroupPool.save(singleEvent);
            }
        }
    }

    public void request(SingleEvent singleEvent) {
        if (!singleEvent.aboveBotMaster()) {
            return;
        }

        if (singleEvent.getMessage().plainStartWith("同意好友请求")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("同意好友请求");
            long id = CommonUtil.getLong(analysis.getText());
            if (RequestEventPool.acceptFriend(id)) {
                singleEvent.send("已同意该请求");
            } else {
                singleEvent.send("请求不存在");
            }
        } else if (singleEvent.getMessage().plainStartWith("拒绝好友请求")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("拒绝好友请求");
            long id = CommonUtil.getLong(analysis.getText());
            if (RequestEventPool.rejectFriend(id)) {
                singleEvent.send("已拒绝该请求");
            } else {
                singleEvent.send("请求不存在");
            }
        } else if (singleEvent.getMessage().plainStartWith("同意群申请")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("同意群申请");
            long id = CommonUtil.getLong(analysis.getText());
            if (RequestEventPool.acceptGroup(id)) {
                singleEvent.send("已同意该请求");
            } else {
                singleEvent.send("请求不存在");
            }
        } else if (singleEvent.getMessage().plainStartWith("拒绝群申请")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("拒绝群申请");
            long id = CommonUtil.getLong(analysis.getText());
            if (RequestEventPool.rejectFriend(id)) {
                singleEvent.send("已拒绝该请求");
            } else {
                singleEvent.send("请求不存在");
            }
        } else if (singleEvent.getMessage().plainStartWith("查看好友请求")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("查看好友请求");
            int page = CommonUtil.getInteger(analysis.getText());
            if (page <= 0) {
                page = 0;
            } else {
                page--;
            }
            int total = RequestEventPool.friendRequestEventMap.keySet().size() / REQUEST_PAGE_SIZE;
            if (RequestEventPool.friendRequestEventMap.keySet().size() % REQUEST_PAGE_SIZE != 0) {
                total++;
            }
            if (page < 0 || page > total) {
                singleEvent.send("页码超限，总计：" + total + "页");
                return;
            }
            StringBuilder builder = new StringBuilder();
            int index = 0;
            for (Long id : RequestEventPool.friendRequestEventMap.keySet()) {
                if (index >= page * REQUEST_PAGE_SIZE) {
                    NewFriendRequestEvent event = RequestEventPool.friendRequestEventMap.get(id);
                    builder.append(String.format("%d.<好友事件%d>申请人：%s（%d）\n", (index + 1), event.getEventId(), event.getFromNick(), event.getFromId()));
                } else if (index > (page + 1) * REQUEST_PAGE_SIZE) {
                    break;
                }
                index++;
            }
            builder.append("--------\n").append("页码：").append(page + 1).append("/").append(total);
            singleEvent.send(builder.toString());
        } else if (singleEvent.getMessage().plainStartWith("查看群申请")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("查看好友请求");
            int page = CommonUtil.getInteger(analysis.getText());
            if (page <= 0) {
                page = 0;
            } else {
                page--;
            }
            int total = RequestEventPool.groupRequestEventMap.keySet().size() / REQUEST_PAGE_SIZE;
            if (RequestEventPool.groupRequestEventMap.keySet().size() % REQUEST_PAGE_SIZE != 0) {
                total++;
            }
            if (page < 0 || page > total) {
                singleEvent.send("页码超限，总计：" + total + "页");
                return;
            }
            StringBuilder builder = new StringBuilder();
            int index = 0;
            for (Long id : RequestEventPool.groupRequestEventMap.keySet()) {
                if (index >= page * REQUEST_PAGE_SIZE) {
                    BotInvitedJoinGroupRequestEvent event = RequestEventPool.groupRequestEventMap.get(id);
                    builder.append(String.format("%d.<群申请%d>申请的群：%s（%d）\n", (index + 1), event.getEventId(), event.getGroupName(), event.getGroupId()));
                } else if (index > (page + 1) * REQUEST_PAGE_SIZE) {
                    break;
                }
                index++;
            }
            builder.append("--------\n").append("页码：").append(page + 1).append("/").append(total);
            singleEvent.send(builder.toString());
        }
    }

    @Override
    public void handleCommon(SingleEvent singleEvent) {
        information(singleEvent);
        blacklist(singleEvent);
        admin(singleEvent);
        request(singleEvent);

        if (singleEvent.getMessage().plainEqual("公约")) {
            singleEvent.send(HttpUtil.convention(singleEvent.getBotId()));
        } else if (singleEvent.getMessage().plainBeAtEqual("关机")) {
            if (singleEvent.aboveBotMaster()) {
                singleEvent.send(singleEvent.getConfig().getName() + "正在执行全局关机，请手动后台重启");
                singleEvent.sendMaster(String.format("%s已关机，操作人%d（权限：%s）", singleEvent.getConfig().getName(), singleEvent.getSenderId(), singleEvent.getRight()));
                singleEvent.getBot().close();
                System.exit(0);
            } else {
                singleEvent.send("你无权执行此操作");
            }
        }
    }

    @Override
    public void handleFriend(SingleEvent singleEvent) {
        if (singleEvent.aboveBotMaster() && singleEvent.getMessage().plainEqual("授权码")) {
            singleEvent.send("授权码：" + singleEvent.getConfig().getKeyValue());
        }
    }

    @Override
    public void handleGroup(SingleEvent singleEvent) {
        switcher(singleEvent);
    }
}
