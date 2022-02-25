package top.beforedawn.plugins;

import top.beforedawn.config.BotConfig;
import top.beforedawn.config.GroupPool;
import top.beforedawn.config.UserPool;
import top.beforedawn.models.bo.*;
import top.beforedawn.util.CommonUtil;
import top.beforedawn.util.SingleEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class BotFunction extends BasePlugin {
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
;                }
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
        }  else if (singleEvent.getMessage().plainStartWith("删除机器人管理员")) {
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

    private void switcher(SingleEvent singleEvent) {
        MyGroup group = GroupPool.get(singleEvent);
        if (singleEvent.getMessage().plainEqual("开启限制模式")) {
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

    @Override
    public void handleCommon(SingleEvent singleEvent) {
        information(singleEvent);
        blacklist(singleEvent);
        admin(singleEvent);
        switcher(singleEvent);
    }

    @Override
    public void handleFriend(SingleEvent singleEvent) {
        if (singleEvent.aboveBotMaster() && singleEvent.getMessage().plainEqual("授权码")) {
            singleEvent.send("授权码：" + singleEvent.getConfig().getKeyValue());
        }
    }

    @Override
    public void handleGroup(SingleEvent singleEvent) {

    }
}
