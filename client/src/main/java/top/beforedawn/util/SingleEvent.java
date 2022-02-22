package top.beforedawn.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;
import top.beforedawn.config.BotConfig;
import top.beforedawn.models.bo.GroupRight;
import top.beforedawn.models.bo.MyMessage;
import top.beforedawn.models.bo.SystemRight;

/**
 * 事件抽象类
 *
 * @author 墨羽翎玖
 */
@Data
public class SingleEvent {
    public String title;
    private Long botId;
    private Long groupId;
    private Long senderId;
    private MyMessage message;
    private SimpleCombineBot combineBot;
    private GroupMessageEvent groupMessageEvent;
    private FriendMessageEvent friendMessageEvent;

    public SingleEvent() {

    }

    public SingleEvent(GroupMessageEvent event) {
        setGroupMessageEvent(event);
    }

    public SingleEvent(FriendMessageEvent event) {
        setFriendMessageEvent(event);
    }

    public String getBotName() {
        return combineBot.getConfig().getName();
    }

    public BotConfig getConfig() {
        return combineBot.getConfig();
    }

    public Bot getBot() {
        return combineBot.getBot();
    }

    public void setGroupMessageEvent(GroupMessageEvent event) {
        friendMessageEvent = null;
        groupMessageEvent = event;
        botId = groupMessageEvent.getBot().getId();
        combineBot = MyBot.getSimpleCombineBot(botId);
        message = CommonUtil.analysisMessage(event.getMessage(), botId);
        senderId = groupMessageEvent.getSender().getId();
        groupId = groupMessageEvent.getGroup().getId();
    }

    public void setFriendMessageEvent(FriendMessageEvent event) {
        groupMessageEvent = null;
        friendMessageEvent = event;
        botId = friendMessageEvent.getBot().getId();
        combineBot = MyBot.getSimpleCombineBot(botId);
        message = CommonUtil.analysisMessage(event.getMessage(), botId);
        senderId = friendMessageEvent.getSender().getId();
        groupId = 0L;
    }

    public boolean valid() {
        return botId != null &&
                message != null &&
                combineBot != null &&
                (friendMessageEvent != null || groupMessageEvent != null);
    }

    public boolean isGroupMessage() {
        return groupMessageEvent != null;
    }

    public boolean isFriendMessage() {
        return friendMessageEvent != null;
    }

    public SystemRight getRight() {
        if (combineBot == null) {
            return SystemRight.MEMBER;
        }
        return combineBot.getConfig().checkRight(getSenderId());
    }

    public GroupRight getGroupRight() {
        if (isGroupMessage()) {
            switch (groupMessageEvent.getPermission().getLevel()) {
                case 2:
                    return GroupRight.MASTER;
                case 1:
                    return GroupRight.ADMIN;
                default:
                    return GroupRight.MEMBER;
            }
        } else {
            return GroupRight.NONE;
        }
    }

    /**
     * 权限是否是大于群管理的
     *
     * @return boolean
     */
    public boolean aboveGroupAdmin() {
        return !(getRight() == SystemRight.MEMBER && getGroupRight() == GroupRight.MEMBER);
    }

    /**
     * 权限是否是大于机器人主人的
     *
     * @return boolean
     */
    public boolean aboveBotMaster() {
        return getRight() != SystemRight.MEMBER && getRight() != SystemRight.ADMIN;
    }

    /**
     * 退群
     */
    public void quit() {
        if (isGroupMessage()) {
            groupMessageEvent.getSubject().quit();
        }
    }

    public void record() {
        if (title != null && title.length() != 0) {
            getConfig().getStatistics().record(getSenderId(), title);
        } else {
            getConfig().getStatistics().record(getSenderId());
        }
    }

    /**
     * 给主人发送消息
     *
     * @param chain 消息链
     */
    public void sendMaster(MessageChain chain) {
        if (combineBot == null) {
            return;
        }
        Friend friend = combineBot.getBot().getFriend(getConfig().getMaster());
        if (friend != null) {
            record();
            friend.sendMessage(chain);
        }
    }

    /**
     * 给主人发送消息
     *
     * @param plain 文本
     */
    public void sendMaster(String plain) {
        if (combineBot == null) {
            return;
        }
        Friend friend = combineBot.getBot().getFriend(getConfig().getMaster());
        if (friend != null) {
            record();
            friend.sendMessage(plain);
        }
    }

    /**
     * 发送消息
     *
     * @param chain 消息链
     */
    public void send(MessageChain chain) {
        if (isGroupMessage()) {
            record();
            groupMessageEvent.getSubject().sendMessage(chain);
        } else if (isFriendMessage()) {
            record();
            friendMessageEvent.getSubject().sendMessage(chain);
        }
    }

    /**
     * 发送文本信息
     *
     * @param plain 文本信息
     */
    public void send(String plain) {
        if (isGroupMessage()) {
            record();
            groupMessageEvent.getSubject().sendMessage(plain);
        } else if (isFriendMessage()) {
            record();
            friendMessageEvent.getSubject().sendMessage(plain);
        }
    }

    /**
     * 发送带艾特的信息
     *
     * @param chain 消息链
     */
    public void sendAt(MessageChain chain) {
        MessageChainBuilder messages = new MessageChainBuilder();
        if (isGroupMessage()) {
            messages.append(new At(getSenderId()));
        }
        for (SingleMessage singleMessage : chain) {
            messages.append(singleMessage);
        }
        send(messages.asMessageChain());
    }

    /**
     * 发送带艾特的信息
     *
     * @param plain 文本
     */
    public void sendAt(String plain) {
        MessageChainBuilder messages = new MessageChainBuilder();
        if (isGroupMessage()) {
            messages.append(new At(getSenderId()));
        }
        messages.append(new PlainText(plain));
        send(messages.asMessageChain());
    }

    /**
     * 发送临时消息
     *
     * @param chain 消息链
     */
    public void sendTemp(MessageChain chain) {
        if (isGroupMessage()) {
            record();
            groupMessageEvent.getSubject().sendMessage(chain);
        } else if (isFriendMessage()) {
            record();
            friendMessageEvent.getSubject().sendMessage(chain);
        }
    }
}
