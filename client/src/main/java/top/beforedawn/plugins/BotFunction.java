package top.beforedawn.plugins;

import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import top.beforedawn.config.*;
import top.beforedawn.models.bo.*;
import top.beforedawn.models.context.BroadcastContext;
import top.beforedawn.models.context.Context;
import top.beforedawn.models.context.GroupConfigClearContext;
import top.beforedawn.models.context.WelcomeContext;
import top.beforedawn.util.CommonUtil;
import top.beforedawn.util.HttpUtil;
import top.beforedawn.util.SingleEvent;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class BotFunction extends BasePlugin {
    private static final int BLACKLIST_PAGE_SIZE = 20;
    private static final int REQUEST_PAGE_SIZE = 20;
    private static final int MUTE_WORDS_PAGE_SIZE = 20;
    private static final int MAX_TUNNEL = 30;

    public BotFunction() {
        pluginName = "bot_function";
    }

    private void information(SingleEvent singleEvent) {
        if (singleEvent.getMessage().plainEqual("帮助") ||
                singleEvent.getMessage().plainEqual("指令") ||
                singleEvent.getMessage().plainEqual("菜单") ||
                singleEvent.getMessage().plainEqual(".help") ||
                singleEvent.getMessage().plainEqual("*help") ||
                singleEvent.getMessage().plainEqual("你能做什么")) {
            singleEvent.send("帮助文档地址：https://beforedawn.top/");
        }
        // 机器人信息
        else if (singleEvent.getMessage().plainEqual("机器人信息")) {
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
        }
        // 统计信息
        else if (singleEvent.getMessage().plainEqual("统计信息")) {
            String builder = singleEvent.getConfig().getStatistics().toString() +
                    "\n" + "缓存User：" + UserPool.size() + "/" + UserPool.POOL_MAX +
                    "\n" + "缓存Group：" + GroupPool.size() + "/" + GroupPool.POOL_MAX +
                    "\n" + "版本信息：" + BotConfig.VERSION;
            singleEvent.send(builder);
        }
        // 获取权限
        else if (singleEvent.getMessage().plainEqual("我的权限")) {
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
        }
        // 获取积分
        else if (singleEvent.getMessage().plainEqual("我的积分")) {
            MyUser user = UserPool.getUser(singleEvent);
            singleEvent.send(singleEvent.getSenderName() + "你的系统积分：" + user.getPoint());
        }
        // 获取版本信息
        else if (singleEvent.getMessage().plainEqual("版本信息")) {
            singleEvent.send("版本信息：" + BotConfig.VERSION);
        }
    }

    private void blacklist(SingleEvent singleEvent) {
        // 查看全局黑名单人
        if (singleEvent.getMessage().plainStartWith("查看全局黑名单人")) {
            if (singleEvent.aboveSystemAdmin()) {
                Blacklist blacklist = singleEvent.getConfig().getBlacklist();
                MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
                analysis.pop("查看全局黑名单人");
                int page = CommonUtil.getInteger(analysis.getText());
                if (page > 0) page--;
                singleEvent.send(blacklist.showGlobalUser(page, BLACKLIST_PAGE_SIZE));
            } else {
                singleEvent.send("权限不足");
            }
        }
        // 查看全局黑名单群
        else if (singleEvent.getMessage().plainStartWith("查看全局黑名单群")) {
            if (singleEvent.aboveSystemAdmin()) {
                Blacklist blacklist = singleEvent.getConfig().getBlacklist();
                MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
                analysis.pop("查看全局黑名单群");
                int page = CommonUtil.getInteger(analysis.getText());
                if (page > 0) page--;
                singleEvent.send(blacklist.showGlobalGroup(page, BLACKLIST_PAGE_SIZE));
            } else {
                singleEvent.send("权限不足");
            }
        }
        // 查看黑名单人
        else if (singleEvent.getMessage().plainStartWith("查看黑名单人")) {
            if (singleEvent.aboveBotAdmin()) {
                Blacklist blacklist = singleEvent.getConfig().getBlacklist();
                MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
                analysis.pop("查看黑名单人");
                int page = CommonUtil.getInteger(analysis.getText());
                if (page > 0) page--;
                singleEvent.send(blacklist.showUser(page, BLACKLIST_PAGE_SIZE));
            } else {
                singleEvent.send("权限不足");
            }
        }
        // 查看黑名单群
        else if (singleEvent.getMessage().plainStartWith("查看黑名单群")) {
            if (singleEvent.aboveBotAdmin()) {
                Blacklist blacklist = singleEvent.getConfig().getBlacklist();
                MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
                analysis.pop("查看黑名单群");
                int page = CommonUtil.getInteger(analysis.getText());
                if (page > 0) page--;
                singleEvent.send(blacklist.showGroup(page, BLACKLIST_PAGE_SIZE));
            } else {
                singleEvent.send("权限不足");
            }
        }
        // 添加黑名单人
        else if (singleEvent.getMessage().plainStartWith("添加黑名单人")) {
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
        }
        // 删除黑名单人
        else if (singleEvent.getMessage().plainStartWith("删除黑名单人")) {
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
        }
        // 添加黑名单群
        else if (singleEvent.getMessage().plainStartWith("添加黑名单群")) {
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
        }
        // 删除黑名单群
        else if (singleEvent.getMessage().plainStartWith("删除黑名单群")) {
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

    /**
     * 机器人的开关
     *
     * @param singleEvent 事件
     */
    private void botSwitcher(SingleEvent singleEvent) {
        // 查看全局模块列表
        if (singleEvent.getMessage().plainEqual("全局模块列表")) {
            String message = "版本信息：" + BotConfig.VERSION + "\n" +
                    "是否允许使用coc模块：" + getSwitcher(singleEvent.getConfig().isAllowCoc()) + "\n" +
                    "是否允许使用rpg模块：" + getSwitcher(singleEvent.getConfig().isAllowRpg()) + "\n" +
                    "是否允许使用trpg模块：" + getSwitcher(singleEvent.getConfig().isAllowTrpg()) + "\n" +
                    "是否允许使用pixiv模块：" + getSwitcher(singleEvent.getConfig().isAllowPic()) + "\n" +
                    "是否允许使用个人助理模块：" + getSwitcher(singleEvent.getConfig().isAllowAssistant()) + "\n" +
                    "是否允许自动添加好友：" + getSwitcher(singleEvent.getConfig().getBotSwitcher().isAllowFriend()) + "\n" +
                    "是否允许自动添加群：" + getSwitcher(singleEvent.getConfig().getBotSwitcher().isAllowGroup()) + "\n" +
                    "是否订阅新好友提醒：" + getSwitcher(singleEvent.getConfig().getBotSwitcher().isRemindFriend()) + "\n" +
                    "是否订阅群邀请提醒：" + getSwitcher(singleEvent.getConfig().getBotSwitcher().isRemindGroup()) + "\n" +
                    "是否订阅禁言提醒：" + getSwitcher(singleEvent.getConfig().getBotSwitcher().isRemindMute()) + "\n" +
                    "是否订阅退群提醒：" + getSwitcher(singleEvent.getConfig().getBotSwitcher().isRemindQuit()) + "\n" +
                    "是否进行心跳检测：" + getSwitcher(singleEvent.getConfig().getBotSwitcher().isHeart()) + "\n" +
                    "心跳检测间隔：" + singleEvent.getConfig().getBotSwitcher().getHeartInterval();
            singleEvent.send(message);
        }
        // 广播消息
        else if (singleEvent.getMessage().plainEqual("广播消息")) {
            if (ContextPool.contains(singleEvent.getSenderId())) {
                return;
            }
            if (!singleEvent.aboveBotMaster()) {
                singleEvent.send("你无权执行该操作");
                return;
            }
            ContextPool.put(singleEvent.getSenderId(), new BroadcastContext());
            singleEvent.send("你可以输入“取消广播”，来取消广播。否则你的下一条消息将会被广播出去。\n" +
                    "请注意不要频繁使用广播骚扰别人，注意量度");
        }
        // 开启机器人自动加好友
        else if (singleEvent.getMessage().plainEqual("开启自动添加好友")) {
            if (!singleEvent.aboveBotMaster()) {
                singleEvent.send("你无权执行该操作");
                return;
            }
            if (singleEvent.getConfig().getBotSwitcher().isAllowFriend()) {
                singleEvent.send("当前策略就是允许自动添加好友");
                return;
            }
            singleEvent.getConfig().getBotSwitcher().setAllowFriend(true);
            if (HttpUtil.updateBot(singleEvent, singleEvent.getConfig())) {
                singleEvent.send("修改成功！");
            } else {
                singleEvent.send("修改失败，无法上传到云端。");
            }
        }
        // 关闭机器人自动加好友
        else if (singleEvent.getMessage().plainEqual("关闭自动添加好友")) {
            if (!singleEvent.aboveBotMaster()) {
                singleEvent.send("你无权执行该操作");
                return;
            }
            if (!singleEvent.getConfig().getBotSwitcher().isAllowFriend()) {
                singleEvent.send("当前策略就是不允许自动添加好友");
                return;
            }
            singleEvent.getConfig().getBotSwitcher().setAllowFriend(false);
            if (HttpUtil.updateBot(singleEvent, singleEvent.getConfig())) {
                singleEvent.send("修改成功！");
            } else {
                singleEvent.send("修改失败，无法上传到云端。");
            }
        }
        // 开启机器人自动加群
        else if (singleEvent.getMessage().plainEqual("开启自动添加群")) {
            if (!singleEvent.aboveBotMaster()) {
                singleEvent.send("你无权执行该操作");
                return;
            }
            if (singleEvent.getConfig().getBotSwitcher().isAllowGroup()) {
                singleEvent.send("当前策略就是允许自动添加群");
                return;
            }
            singleEvent.getConfig().getBotSwitcher().setAllowGroup(true);
            if (HttpUtil.updateBot(singleEvent, singleEvent.getConfig())) {
                singleEvent.send("修改成功！");
            } else {
                singleEvent.send("修改失败，无法上传到云端。");
            }
        }
        // 关闭机器人自动加群
        else if (singleEvent.getMessage().plainEqual("关闭自动添加群")) {
            if (!singleEvent.aboveBotMaster()) {
                singleEvent.send("你无权执行该操作");
                return;
            }
            if (!singleEvent.getConfig().getBotSwitcher().isAllowGroup()) {
                singleEvent.send("当前策略就是不允许自动添加群");
                return;
            }
            singleEvent.getConfig().getBotSwitcher().setAllowGroup(false);
            if (HttpUtil.updateBot(singleEvent, singleEvent.getConfig())) {
                singleEvent.send("修改成功！");
            } else {
                singleEvent.send("修改失败，无法上传到云端。");
            }
        }
        // 开启机器人新好友提醒
        else if (singleEvent.getMessage().plainEqual("开启新好友提醒")) {
            if (!singleEvent.aboveBotMaster()) {
                singleEvent.send("你无权执行该操作");
                return;
            }
            if (singleEvent.getConfig().getBotSwitcher().isRemindFriend()) {
                singleEvent.send("当前策略就是开启好友提醒的");
                return;
            }
            singleEvent.getConfig().getBotSwitcher().setRemindFriend(true);
            if (HttpUtil.updateBot(singleEvent, singleEvent.getConfig())) {
                singleEvent.send("修改成功！");
            } else {
                singleEvent.send("修改失败，无法上传到云端。");
            }
        }
        // 关闭机器人新好友提醒
        else if (singleEvent.getMessage().plainEqual("关闭新好友提醒")) {
            if (!singleEvent.aboveBotMaster()) {
                singleEvent.send("你无权执行该操作");
                return;
            }
            if (!singleEvent.getConfig().getBotSwitcher().isRemindFriend()) {
                singleEvent.send("当前策略就是关闭好友提醒的");
                return;
            }
            singleEvent.getConfig().getBotSwitcher().setRemindFriend(false);
            if (HttpUtil.updateBot(singleEvent, singleEvent.getConfig())) {
                singleEvent.send("修改成功！");
            } else {
                singleEvent.send("修改失败，无法上传到云端。");
            }
        }
        // 开启机器人新群提醒
        else if (singleEvent.getMessage().plainEqual("开启新群提醒")) {
            if (!singleEvent.aboveBotMaster()) {
                singleEvent.send("你无权执行该操作");
                return;
            }
            if (singleEvent.getConfig().getBotSwitcher().isRemindGroup()) {
                singleEvent.send("当前策略就是开启新群提醒的");
                return;
            }
            singleEvent.getConfig().getBotSwitcher().setRemindGroup(true);
            if (HttpUtil.updateBot(singleEvent, singleEvent.getConfig())) {
                singleEvent.send("修改成功！");
            } else {
                singleEvent.send("修改失败，无法上传到云端。");
            }
        }
        // 关闭机器人新群提醒
        else if (singleEvent.getMessage().plainEqual("关闭新群提醒")) {
            if (!singleEvent.aboveBotMaster()) {
                singleEvent.send("你无权执行该操作");
                return;
            }
            if (!singleEvent.getConfig().getBotSwitcher().isRemindGroup()) {
                singleEvent.send("当前策略就是关闭新群提醒的");
                return;
            }
            singleEvent.getConfig().getBotSwitcher().setRemindGroup(false);
            if (HttpUtil.updateBot(singleEvent, singleEvent.getConfig())) {
                singleEvent.send("修改成功！");
            } else {
                singleEvent.send("修改失败，无法上传到云端。");
            }
        }
        // 开启机器人退群提醒
        else if (singleEvent.getMessage().plainEqual("开启退群提醒")) {
            if (!singleEvent.aboveBotMaster()) {
                singleEvent.send("你无权执行该操作");
                return;
            }
            if (singleEvent.getConfig().getBotSwitcher().isRemindQuit()) {
                singleEvent.send("当前策略就是开启退群提醒的");
                return;
            }
            singleEvent.getConfig().getBotSwitcher().setRemindQuit(true);
            if (HttpUtil.updateBot(singleEvent, singleEvent.getConfig())) {
                singleEvent.send("修改成功！");
            } else {
                singleEvent.send("修改失败，无法上传到云端。");
            }
        }
        // 关闭机器人退群提醒
        else if (singleEvent.getMessage().plainEqual("关闭退群提醒")) {
            if (!singleEvent.aboveBotMaster()) {
                singleEvent.send("你无权执行该操作");
                return;
            }
            if (!singleEvent.getConfig().getBotSwitcher().isRemindQuit()) {
                singleEvent.send("当前策略就是关闭退群提醒的");
                return;
            }
            singleEvent.getConfig().getBotSwitcher().setRemindQuit(false);
            if (HttpUtil.updateBot(singleEvent, singleEvent.getConfig())) {
                singleEvent.send("修改成功！");
            } else {
                singleEvent.send("修改失败，无法上传到云端。");
            }
        }
        // 开启机器人禁言提醒
        else if (singleEvent.getMessage().plainEqual("开启禁言提醒")) {
            if (!singleEvent.aboveBotMaster()) {
                singleEvent.send("你无权执行该操作");
                return;
            }
            if (singleEvent.getConfig().getBotSwitcher().isRemindMute()) {
                singleEvent.send("当前策略就是开启禁言提醒的");
                return;
            }
            singleEvent.getConfig().getBotSwitcher().setRemindMute(true);
            if (HttpUtil.updateBot(singleEvent, singleEvent.getConfig())) {
                singleEvent.send("修改成功！");
            } else {
                singleEvent.send("修改失败，无法上传到云端。");
            }
        }
        // 关闭机器人禁言提醒
        else if (singleEvent.getMessage().plainEqual("关闭禁言提醒")) {
            if (!singleEvent.aboveBotMaster()) {
                singleEvent.send("你无权执行该操作");
                return;
            }
            if (!singleEvent.getConfig().getBotSwitcher().isRemindMute()) {
                singleEvent.send("当前策略就是关闭禁言提醒的");
                return;
            }
            singleEvent.getConfig().getBotSwitcher().setRemindMute(false);
            if (HttpUtil.updateBot(singleEvent, singleEvent.getConfig())) {
                singleEvent.send("修改成功！");
            } else {
                singleEvent.send("修改失败，无法上传到云端。");
            }
        }
        // 开启机器人心跳
        else if (singleEvent.getMessage().plainEqual("开启心跳")) {
            if (!singleEvent.aboveBotMaster()) {
                singleEvent.send("你无权执行该操作");
                return;
            }
            if (singleEvent.getConfig().getBotSwitcher().isHeart()) {
                singleEvent.send("当前策略就是开启心跳的");
                return;
            }
            singleEvent.getConfig().getBotSwitcher().setHeart(true);
            if (HttpUtil.updateBot(singleEvent, singleEvent.getConfig())) {
                singleEvent.send("修改成功！");
            } else {
                singleEvent.send("修改失败，无法上传到云端。");
            }
        }
        // 关闭机器人心跳
        else if (singleEvent.getMessage().plainEqual("关闭心跳")) {
            if (!singleEvent.aboveBotMaster()) {
                singleEvent.send("你无权执行该操作");
                return;
            }
            if (!singleEvent.getConfig().getBotSwitcher().isHeart()) {
                singleEvent.send("当前策略就是关闭心跳的");
                return;
            }
            singleEvent.getConfig().getBotSwitcher().setHeart(false);
            if (HttpUtil.updateBot(singleEvent, singleEvent.getConfig())) {
                singleEvent.send("修改成功！");
            } else {
                singleEvent.send("修改失败，无法上传到云端。");
            }
        }
        // 设置机器人心跳
        else if (singleEvent.getMessage().plainStartWith("设置心跳")) {
            if (!singleEvent.aboveBotMaster()) {
                singleEvent.send("你无权执行该操作");
                return;
            }
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("设置心跳");
            int interval = CommonUtil.getInteger(analysis.getText());
            if (interval > 0 && interval <= 24) {
                singleEvent.getConfig().getBotSwitcher().setHeartInterval(interval);
                if (HttpUtil.updateBot(singleEvent, singleEvent.getConfig())) {
                    singleEvent.send("成功将心跳间隔设置为：" + interval);
                    if (!singleEvent.getConfig().getBotSwitcher().isHeart()) {
                        singleEvent.send("注意：当前并未开启心跳");
                    }
                } else {
                    singleEvent.send("修改失败，无法上传到云端。");
                }
            } else {
                singleEvent.send("心跳时间应该大于0小时，小于等于24小时");
            }
        }
        // 设置机器人名字
        else if (singleEvent.getMessage().plainStartWith("设置机器人昵称")) {
            if (!singleEvent.aboveBotMaster()) {
                singleEvent.send("你无权执行该操作");
                return;
            }
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("设置机器人昵称");
            String name = analysis.getText();
            if (name.equals(singleEvent.getBotName())) {
                singleEvent.send("新名字与老名字相同");
            } else {
                singleEvent.getConfig().setName(name);
                if (HttpUtil.updateBot(singleEvent, singleEvent.getConfig())) {
                    singleEvent.send("成功修改名字为：" + name);
                } else {
                    singleEvent.send("修改失败，无法上传到云端。");
                }
            }
        }
        // 查看背景任务
        else if (singleEvent.getMessage().plainEqual("查看背景任务")) {
            if (!singleEvent.aboveBotMaster()) {
                singleEvent.send("你无权执行该操作");
                return;
            }
            singleEvent.send("当前背景任务个数为：" + BackgroundTask.getInstance().size());
        }
        // 查看运行状态
        else if (singleEvent.getMessage().plainEqual("查看进程号")) {
            if (!singleEvent.aboveSystemAdmin()) {
                singleEvent.send("权限不足！");
                return;
            }
            singleEvent.send(ManagementFactory.getRuntimeMXBean().getName());
        }
    }

    /**
     * 单个群的开关控制
     *
     * @param singleEvent 事件
     */
    private void groupSwitcher(SingleEvent singleEvent) {
        MyGroup group = GroupPool.get(singleEvent);
        if (singleEvent.getMessage().plainEqual("模块列表")) {
            String message = "版本信息：" + BotConfig.VERSION + "\n" +
                    "是否处在限制模式：" + getSwitcher(group.isLimit()) + "\n" +
                    "是否开启基础功能：" + getSwitcher(group.isBaseFunction()) + "\n" +
                    "是否开启戳一戳：" + getSwitcher(group.isNudge()) + "\n" +
                    "是否开启解除闪照：" + getSwitcher(group.isUnlockFlashImage()) + "\n" +
                    "是否开启防撤回：" + getSwitcher(group.isRecallGuard()) + "\n" +
                    "是否开启成员监控：" + getSwitcher(group.isMemberWatcher()) + "\n" +
                    "是否开启自定义回复：" + getSwitcher(group.isAutoReply()) + "\n" +
                    "是否开启群回复共享：" + getSwitcher(group.isAllowCopyAutoReply()) + "\n" +
                    "是否开启自动加一：" + getSwitcher(group.isRepeat()) + "\n" +
                    "是否开启部落冲突查询：" + getSwitcher(group.isCoc()) + "\n" +
                    "是否开启漂流瓶：" + getSwitcher(group.isDriftingBottle()) + "\n" +
                    "是否开启RPG游戏：" + getSwitcher(group.isRpg()) + "\n" +
                    "是否开启RPG游戏限制模式：" + getSwitcher(group.isRpgLimit()) + "\n" +
                    "是否开启智能回复：" + getSwitcher(group.isSelfReply()) + "\n" +
                    "是否开启文摘：" + getSwitcher(group.isTalk()) + "\n" +
                    "是否开启脏话：" + getSwitcher(group.isSwear()) + "\n" +
                    "是否开启入群欢迎：" + getSwitcher(group.isWelcome()) + "\n" +
                    "是否开启自动入群审核：" + getSwitcher(group.isGroupEntry());
            singleEvent.send(message);
        }
        // 单独控制每个开关

        // 限制模式
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
        }
        // 成员监控
        else if (singleEvent.getMessage().plainEqual("开启成员监控")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isMemberWatcher(), true, "已经开了成员监控了")) {
                group.setMemberWatcher(true);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("关闭成员监控")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isMemberWatcher(), false, "本来就没有开启成员监控")) {
                group.setMemberWatcher(false);
                GroupPool.save(singleEvent);
            }
        }
        // 闪照
        else if (singleEvent.getMessage().plainEqual("开启解除闪照")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isUnlockFlashImage(), true, "已经开启了解除闪照")) {
                group.setUnlockFlashImage(true);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("关闭解除闪照")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isUnlockFlashImage(), false, "本来就没有开启解除闪照")) {
                group.setUnlockFlashImage(false);
                GroupPool.save(singleEvent);
            }
        }
        // 防撤回
        else if (singleEvent.getMessage().plainEqual("开启防撤回")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isRecallGuard(), true, "已经开启了防撤回")) {
                group.setRecallGuard(true);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("关闭防撤回")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isRecallGuard(), false, "本来就没有防撤回")) {
                group.setRecallGuard(false);
                GroupPool.save(singleEvent);
            }
        }
        // 戳一戳
        else if (singleEvent.getMessage().plainEqual("开启戳一戳")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isNudge(), true, "已经开启了戳一戳")) {
                group.setNudge(true);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("关闭戳一戳")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isNudge(), false, "本来就没有开启戳一戳")) {
                group.setNudge(false);
                GroupPool.save(singleEvent);
            }
        }
        // 自定义回复
        else if (singleEvent.getMessage().plainEqual("开启自定义回复")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isAutoReply(), true, "已经开启了自定义回复")) {
                group.setAutoReply(true);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("关闭自定义回复")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isAutoReply(), false, "本来就没有开启自定义回复")) {
                group.setAutoReply(false);
                GroupPool.save(singleEvent);
            }
        }
        // 自动加一
        else if (singleEvent.getMessage().plainEqual("开启自动加一")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isRepeat(), true, "已经开启了加一")) {
                group.setRepeat(true);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("关闭自动加一")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isRepeat(), false, "本来就没有开启加一")) {
                group.setRepeat(false);
                GroupPool.save(singleEvent);
            }
        }
        // 部落冲突查询
        else if (singleEvent.getMessage().plainEqual("开启部落冲突查询")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isCoc(), true, "已经开启了部落冲突查询")) {
                group.setCoc(true);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("关闭部落冲突查询")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isCoc(), false, "本来就没有开启部落冲突查询")) {
                group.setCoc(false);
                GroupPool.save(singleEvent);
            }
        }
        // 漂流瓶
        else if (singleEvent.getMessage().plainEqual("开启漂流瓶")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isDriftingBottle(), true, "已经开启了漂流瓶")) {
                group.setDriftingBottle(true);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("关闭漂流瓶")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isDriftingBottle(), false, "本来就没有开启漂流瓶")) {
                group.setDriftingBottle(false);
                GroupPool.save(singleEvent);
            }
        }
        // 游戏
        else if (singleEvent.getMessage().plainEqual("开启游戏")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isRpg(), true, "已经开启了RPG游戏")) {
                group.setRpg(true);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("关闭游戏")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isRpg(), false, "本来就没有开启RPG游戏")) {
                group.setRpg(false);
                GroupPool.save(singleEvent);
            }
        }
        // 游戏限制模式
        else if (singleEvent.getMessage().plainEqual("开启游戏限制模式")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isRpgLimit(), true, "已经开启了RPG游戏限制模式")) {
                group.setRpgLimit(true);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("关闭游戏限制模式")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isRpgLimit(), false, "本来就没有开启RPG游戏限制模式")) {
                group.setRpgLimit(false);
                GroupPool.save(singleEvent);
            }
        }
        // 智能回复
        else if (singleEvent.getMessage().plainEqual("开启智能回复")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isSelfReply(), true, "已经开启了智能回复")) {
                group.setSelfReply(true);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("关闭智能回复")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isSelfReply(), false, "本来就没有开启智能回复")) {
                group.setSelfReply(false);
                GroupPool.save(singleEvent);
            }
        }
        // 文摘
        else if (singleEvent.getMessage().plainEqual("开启文摘")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isTalk(), true, "已经开启了文摘")) {
                group.setTalk(true);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("关闭文摘")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isTalk(), false, "本来就没有开启文摘")) {
                group.setTalk(false);
                GroupPool.save(singleEvent);
            }
        }
        // 脏话
        else if (singleEvent.getMessage().plainEqual("开启脏话")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isSwear(), true, "已经开启了脏话")) {
                group.setSwear(true);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("关闭脏话")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isSwear(), false, "本来就没有开启脏话")) {
                group.setSwear(false);
                GroupPool.save(singleEvent);
            }
        }
        // 群回复共享
        else if (singleEvent.getMessage().plainEqual("开启群回复共享")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isAllowCopyAutoReply(), true, "已经开启了群回复共享")) {
                group.setAllowCopyAutoReply(true);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("关闭群回复共享")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isAllowCopyAutoReply(), false, "本来就没有开启群回复共享")) {
                group.setAllowCopyAutoReply(false);
                GroupPool.save(singleEvent);
            }
        }
        // 屏蔽词
        else if (singleEvent.getMessage().plainStartWith("添加屏蔽词")) {
            if (!singleEvent.aboveGroupAdmin()) {
                singleEvent.send("权限不足");
                return;
            }
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("添加屏蔽词");
            if (group.getMuteWords().contains(analysis.getText())) {
                singleEvent.send("屏蔽词“" + analysis.getText() + "”已存在");
                return;
            } else if (analysis.getText().startsWith("删除屏蔽词")) {
                singleEvent.send("不可以把“删除屏蔽词”开头的句子添加为屏蔽词");
                return;
            }
            group.getMuteWords().add(analysis.getText());
            GroupPool.save(singleEvent);
            singleEvent.send("添加成功");
        } else if (singleEvent.getMessage().plainStartWith("删除屏蔽词")) {
            if (!singleEvent.aboveGroupAdmin()) {
                singleEvent.send("权限不足");
                return;
            }
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("删除屏蔽词");
            if (!group.getMuteWords().contains(analysis.getText())) {
                singleEvent.send("屏蔽词“" + analysis.getText() + "”不存在");
                return;
            }
            group.getMuteWords().remove(analysis.getText());
            GroupPool.save(singleEvent);
            singleEvent.send("删除成功");
        } else if (singleEvent.getMessage().plainStartWith("查看屏蔽词")) {
            if (!singleEvent.aboveGroupAdmin()) {
                singleEvent.send("权限不足");
                return;
            }
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("查看屏蔽词");
            int page = CommonUtil.getInteger(analysis.getText());
            if (page > 0) page--;
            if (group.getMuteWords().size() == 0) {
                singleEvent.send("（暂无）");
                return;
            }
            int total = group.getMuteWords().size() / MUTE_WORDS_PAGE_SIZE;
            if (group.getMuteWords().size() % MUTE_WORDS_PAGE_SIZE != 0) total++;
            if (page > total) {
                singleEvent.send("页码超限，总计：" + total + "页");
                return;
            }
            StringBuilder builder = new StringBuilder();
            for (int i = page * MUTE_WORDS_PAGE_SIZE; i < group.getMuteWords().size() && i < (page + 1) * MUTE_WORDS_PAGE_SIZE; i++) {
                builder.append(i + 1).append(".").append(group.getMuteWords().get(i)).append("\n");
            }
            builder.append("--------\n页码：").append(page + 1).append("/").append(total);
            singleEvent.send(builder.toString());
        }
        // 入群欢迎
        else if (singleEvent.getMessage().plainEqual("开启入群欢迎")) {
            if (!singleEvent.aboveGroupAdmin()) {
                singleEvent.send("你无权操作");
                return;
            }
            if (ContextPool.contains(singleEvent.getSenderId())) return;
            singleEvent.send(singleEvent.getBotName() + "将为您开启/修改入群欢迎，请问你的入群欢迎是什么，下一条消息将会被记录作为欢迎语（可包含图片，可在其他群或私聊告诉我）");
            WelcomeContext welcomeContext = new WelcomeContext();
            welcomeContext.setGroupId(singleEvent.getGroupId());
            ContextPool.put(singleEvent.getSenderId(), welcomeContext);
        } else if (singleEvent.getMessage().plainEqual("关闭入群欢迎")) {
            if (!singleEvent.aboveGroupAdmin()) {
                singleEvent.send("你无权操作");
                return;
            }
            if (group.isWelcome()) {
                group.setWelcome(false);
                GroupPool.save(singleEvent);
                singleEvent.send("关闭成功~");
            } else {
                singleEvent.send("本群本来就没有开启入群欢迎哦~");
            }
        }
        // 指令隧穿
        else if (singleEvent.getMessage().plainStartWith("添加指令隧穿")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("添加指令隧穿");
            ArrayList<String> split = analysis.split();
            if (!singleEvent.aboveGroupAdmin()) {
                singleEvent.send("你无权操作");
                return;
            }
            if (split.size() != 2) {
                singleEvent.send("格式错误！");
                return;
            }
            String origin = split.get(0);
            String target = split.get(1);
            if (group.getTunnel().containsKey(origin)) {
                singleEvent.send("已经存在指令隧穿：" + origin + " - " + group.getTunnel().get(origin));
                return;
            }
            if (group.getTunnel().keySet().size() > MAX_TUNNEL) {
                singleEvent.send("指令隧穿已达到上限：" + MAX_TUNNEL);
                return;
            }
            group.getTunnel().put(origin, target);
            GroupPool.save(singleEvent);
            singleEvent.send("添加成功~");
        } else if (singleEvent.getMessage().plainStartWith("删除指令隧穿")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("删除指令隧穿");
            String origin = analysis.getText();
            if (!singleEvent.aboveGroupAdmin()) {
                singleEvent.send("你无权操作");
                return;
            }
            if (!group.getTunnel().containsKey(origin)) {
                singleEvent.send("不存在词组配对：" + origin);
                return;
            }
            singleEvent.send("成功删除指令隧穿：" + origin + " - " + group.getTunnel().get(origin));
            group.getTunnel().remove(origin);
            GroupPool.save(singleEvent);
        } else if (singleEvent.getMessage().plainEqual("查看指令隧穿")) {
            if (group.getTunnel().keySet().size() == 0) {
                singleEvent.send("暂无指令隧穿");
                return;
            }
            StringBuilder builder = new StringBuilder();
            int index = 0;
            for (String key : group.getTunnel().keySet()) {
                if (index++ != 0) builder.append("\n");
                builder.append(index).append(".").append(key).append(" - ").append(group.getTunnel().get(key));
            }
            singleEvent.send(builder.toString());
        }
        // 禁言一个人
        else if (singleEvent.getMessage().plainStartWithoutAt("禁言") && singleEvent.getMessage().getAt().size() != 0) {
            if (!singleEvent.aboveGroupAdmin()) {
                singleEvent.send("你无权执行禁言其他人的操作");
                return;
            }
            GroupMessageEvent event = singleEvent.getGroupMessageEvent();
            if (event.getGroup().getBotPermission() == MemberPermission.MEMBER) {
                singleEvent.send(singleEvent.getBotName() + "无权禁言其他人");
            }
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("禁言");
            int seconds = analysis.getSeconds();
            if (seconds < 60) seconds = 60;
            else if (seconds > 3600 * 24 * 30) seconds = 3600 * 24 * 30;
            StringBuilder builder = new StringBuilder();
            boolean init = false;
            for (NormalMember member : event.getGroup().getMembers()) {
                if (singleEvent.getMessage().getAt().contains(member.getId())) {
                    if (member.getPermission() != MemberPermission.MEMBER) {
                        builder.append("无权禁言").append(member.getNameCard()).append("（").append(member.getId()).append("）\n");
                    } else {
                        builder.append("禁言").append(member.getNameCard()).append("（").append(member.getId()).append("）\n");
                        member.mute(seconds);
                        init = true;
                    }
                }
            }
            if (init) {
                singleEvent.send(builder.toString());
            } else {
                singleEvent.send(singleEvent.getBotName() + "无权禁言其中任何人");
            }
        }
        // 解除禁言
        else if (singleEvent.getMessage().plainEqualWithoutAt("解除禁言") && singleEvent.getMessage().getAt().size() != 0) {
            if (!singleEvent.aboveGroupAdmin()) {
                singleEvent.send("你无权执行禁言其他人的操作");
                return;
            }
            GroupMessageEvent event = singleEvent.getGroupMessageEvent();
            if (event.getGroup().getBotPermission() == MemberPermission.MEMBER) {
                singleEvent.send(singleEvent.getBotName() + "无权解除其他人的禁言");
            }
            for (NormalMember member : event.getGroup().getMembers()) {
                if (singleEvent.getMessage().getAt().contains(member.getId())) {
                    member.unmute();
                }
            }
            singleEvent.send("操作成功");
        }
        // 开启基础功能
        else if (singleEvent.getMessage().plainEqual("开启基础功能")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isBaseFunction(), true, "已经开启了基础功能")) {
                group.setBaseFunction(true);
                GroupPool.save(singleEvent);
            }
        } else if (singleEvent.getMessage().plainEqual("关闭基础功能")) {
            if (check(singleEvent, singleEvent.aboveGroupAdmin(), group.isBaseFunction(), false, "本来就没有开启基础功能")) {
                group.setBaseFunction(false);
                GroupPool.save(singleEvent);
            }
        }
        // 清除缓存
        else if (singleEvent.getMessage().plainEqual("清除缓存")) {
            if (singleEvent.aboveBotMaster()) {
                GroupPool.clear();
                UserPool.clear();
                singleEvent.send("清空成功");
            } else {
                singleEvent.send("权限不足");
            }
        }
        // 清空群设置
        else if (singleEvent.getMessage().plainEqual("清空群设置")) {
            if (!singleEvent.aboveGroupAdmin()) {
                singleEvent.send("你无权执行清空群设置的操作");
            } else if (!ContextPool.contains(singleEvent.getSenderId())) {
                GroupConfigClearContext context = new GroupConfigClearContext();
                context.setGroupId(singleEvent.getGroupId());
                ContextPool.put(singleEvent.getSenderId(), context);
                singleEvent.send(CommonUtil.confirmMessage());
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
            if (RequestEventPool.rejectGroup(id)) {
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
            if (page > total) {
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
            if (page > total) {
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
    public boolean handContextCommon(SingleEvent singleEvent) {
        Context context = ContextPool.get(singleEvent.getSenderId());
        // 入群欢迎
        if (context instanceof WelcomeContext) {
            ((WelcomeContext) context).setWelcome(CommonUtil.getSerializeMessage(singleEvent.getConfig().getWorkdir() + "image/", singleEvent.getMessage().getOrigin()));
            Long groupId = singleEvent.getGroupId();
            singleEvent.setGroupId(((WelcomeContext) context).getGroupId());
            MyGroup group = GroupPool.get(singleEvent);
            group.setWelcome(true);
            // 删除原来的图片文件
            if (!CommonUtil.removeImageFile(group.getWelcomeMessage())) {
                singleEvent.sendMaster("在删除复杂入群欢迎的时候，遇见了一个未知错误，无法删除文件");
            }
            group.setWelcomeMessage(((WelcomeContext) context).getWelcome());
            GroupPool.save(singleEvent);
            ContextPool.remove(singleEvent.getSenderId());
            singleEvent.setGroupId(groupId);
            singleEvent.send("添加成功~");
        }
        // 发送广播消息
        else if (context instanceof BroadcastContext) {
            ContextPool.remove(singleEvent.getSenderId());
            if (singleEvent.getMessage().plainEqual("取消广播")) {
                singleEvent.send("已为您取消创建");
                return true;
            }
            new Thread(() -> {
                MessageChain chain = singleEvent.getMessage().getOrigin();
                MessageChainBuilder builder = new MessageChainBuilder();
                builder.addAll(chain);
                builder.append(new PlainText("——来自管理员（" + singleEvent.getSenderId() + "）的全局广播，无需回复。"));
                ContactList<Group> groups = singleEvent.getBot().getGroups();
                singleEvent.sendMaster(singleEvent.getBotName() + "已开始广播，总计群：" + groups.size() + "个\n" +
                        "执行人：" + singleEvent.getSenderName() + "（权限：" + singleEvent.getRight() + "）\n" +
                        "预估时间：" + groups.size() * 3 / 4 + "秒");
                for (Group group : groups) {
                    singleEvent.record();
                    group.sendMessage(builder.asMessageChain());
                    try {
                        Thread.sleep(CommonUtil.randomInteger(500, 1000));
                    } catch (InterruptedException e) {
                        singleEvent.sendMaster("广播消息受到外部影响已提前终止");
                        break;
                    }
                }
                singleEvent.sendMaster("广播完成！");
            }).start();
        }
        // 清空群设置
        else if (context instanceof GroupConfigClearContext) {
            if (CommonUtil.isConfirmMessage(singleEvent.getMessage().getPlainString())) {
                MyGroup group = new MyGroup();
                group.setId(((GroupConfigClearContext) context).getGroupId());
                GroupPool.remove(group.getId());
                GroupPool.put(group);
                GroupPool.save(singleEvent, group.getId());
                singleEvent.send("清空成功！");
            } else {
                singleEvent.send("已取消清空！");
            }
            ContextPool.remove(singleEvent.getSenderId());
        }
        return false;
    }

    @Override
    public void handleCommon(SingleEvent singleEvent) {
        information(singleEvent);
        blacklist(singleEvent);
        admin(singleEvent);
        request(singleEvent);
        botSwitcher(singleEvent);

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
        groupSwitcher(singleEvent);
    }
}
