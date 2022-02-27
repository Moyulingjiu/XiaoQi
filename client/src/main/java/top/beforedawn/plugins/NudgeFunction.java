package top.beforedawn.plugins;

import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import top.beforedawn.config.GroupPool;
import top.beforedawn.models.bo.MyGroup;
import top.beforedawn.util.CommonUtil;
import top.beforedawn.util.SingleEvent;

/**
 * 戳一戳事件
 */
public class NudgeFunction extends BasePlugin {
    public NudgeFunction() {
        pluginName = "nudge";
    }

    @Override
    public boolean before(SingleEvent singleEvent) {
        if (!singleEvent.isNudge()) {
            return false;
        }
        if (singleEvent.getGroupId() == 0L) {
            return singleEvent.getMessage().isBeNudge() && singleEvent.getMessage().isBeAt();
        } else {
            MyGroup group = GroupPool.get(singleEvent);
            return singleEvent.getMessage().isBeNudge() && singleEvent.getMessage().isBeAt() && group.isNudge();
        }
    }

    @Override
    public void handleCommon(SingleEvent singleEvent) {
        String filepath = singleEvent.getConfig().getGlobalWorkdir() + "nudge/";
        MessageChainBuilder builder = new MessageChainBuilder();
        switch (CommonUtil.randomInteger(35)) {
            case 0:
                builder.append(new PlainText("你再戳？你再戳？"));
                builder.append(singleEvent.uploadImage(filepath + "打.gif"));
                break;
            case 1:
                builder.append(singleEvent.uploadImage(filepath + "质疑.jpg"));
                break;
            case 2:
                builder.append(new PlainText("别戳了"));
                builder.append(singleEvent.uploadImage(filepath + "过分.jpg"));
                break;
            case 3:
                builder.append(new PlainText("放过我吧"));
                builder.append(singleEvent.uploadImage(filepath + "乖巧.jpg"));
                break;
            case 4:
                builder.append(singleEvent.uploadImage(filepath + "无语.jpg"));
                break;
            case 5:
                builder.append(new PlainText("你再戳我就哭给你看，嘤嘤嘤~"));
                break;
            case 6:
                builder.append(new PlainText("别戳了呜呜"));
                builder.append(singleEvent.uploadImage(filepath + "委屈2.jpg"));
                break;
            case 7:
                builder.append(new PlainText("你是不是戳上头了"));
                builder.append(singleEvent.uploadImage(filepath + "上头.png"));
                break;
            case 8:
                builder.append(new PlainText("为什么戳我"));
                builder.append(singleEvent.uploadImage(filepath + "质疑2.gif"));
                break;
            case 9:
                builder.append(new PlainText("别戳了呜呜"));
                builder.append(singleEvent.uploadImage(filepath + "委屈.jpg"));
                break;
            case 10:
                builder.append(new PlainText("不许戳"));
                builder.append(singleEvent.uploadImage(filepath + "不许戳.jpg"));
                break;
            case 11:
                builder.append(singleEvent.uploadImage(filepath + "委屈3.jpg"));
                break;
            case 12:
                builder.append(singleEvent.uploadImage(filepath + "不开心.jpg"));
                break;
            case 13:
                builder.append(new PlainText("不可以再戳了"));
                builder.append(singleEvent.uploadImage(filepath + "不开心2.jpg"));
                break;
            case 14:
                builder.append(singleEvent.uploadImage(filepath + "无语2.jpg"));
                break;
            case 15:
                builder.append(singleEvent.uploadImage(filepath + "无语3.bmp"));
                break;
            case 16:
                builder.append(new PlainText("不可以做这种事情哦~"));
                builder.append(singleEvent.uploadImage(filepath + "哭.bmp"));
                break;
            case 17:
                builder.append(new PlainText("不可以再戳了~"));
                builder.append(singleEvent.uploadImage(filepath + "别戳了.bmp"));
                break;
            case 18:
                builder.append(new PlainText("你再戳你是笨蛋"));
                builder.append(singleEvent.uploadImage(filepath + "质疑3.bmp"));
                break;
            case 19:
                builder.append(singleEvent.uploadImage(filepath + "骂骂咧咧.png"));
                break;
            case 20:
                builder.append(new PlainText("真够无聊的呢"));
                builder.append(singleEvent.uploadImage(filepath + "质疑4.bmp"));
                break;
            case 21:
                builder.append(new PlainText("突死你"));
                builder.append(singleEvent.uploadImage(filepath + "打2.jpg"));
                break;
            case 22:
                builder.append(singleEvent.uploadImage(filepath + "无语4.gif"));
                break;
            case 23:
                builder.append(singleEvent.uploadImage(filepath + "乖巧2.jpg"));
                break;
            case 24:
                builder.append(singleEvent.uploadImage(filepath + "哭2.jpg"));
                break;
            case 25:
                builder.append(new PlainText("呜呜呜呜呜为什么呜呜为什么戳呜呜我"));
                builder.append(singleEvent.uploadImage(filepath + "哭3.gif"));
                break;
            case 26:
                builder.append(new PlainText("别戳！"));
                builder.append(singleEvent.uploadImage(filepath + "凶狠.gif"));
                break;
            case 27:
                builder.append(new PlainText("QAQ"));
                builder.append(singleEvent.uploadImage(filepath + "哭4.gif"));
                break;
            case 28:
                builder.append(singleEvent.uploadImage(filepath + "摇摆.gif"));
                break;
            case 29:
                builder.append(singleEvent.uploadImage(filepath + "哭5.jpg"));
                break;
            case 30:
                builder.append(new PlainText("戳吧戳吧"));
                builder.append(singleEvent.uploadImage(filepath + "摆烂.jpg"));
                break;
            case 31:
                builder.append(new PlainText("？为什么戳我？"));
                builder.append(singleEvent.uploadImage(filepath + "质疑5.jpg"));
                break;
            case 32:
                builder.append(singleEvent.uploadImage(filepath + "质疑6.png"));
                break;
            case 33:
                builder.append(singleEvent.uploadImage(filepath + "摆烂2.jpg"));
                break;
            default:
                builder.append(new PlainText("别戳啦~"));
        }
        singleEvent.send(builder.asMessageChain());
    }

    @Override
    public void handleFriend(SingleEvent singleEvent) {

    }

    @Override
    public void handleGroup(SingleEvent singleEvent) {

    }
}
