package top.beforedawn.plugins;

import net.mamoe.mirai.contact.MemberPermission;
import top.beforedawn.models.bo.MessageLinearAnalysis;
import top.beforedawn.models.bo.MyUser;
import top.beforedawn.util.*;

import java.nio.charset.StandardCharsets;

/**
 * 基础功能
 *
 * @author 墨羽翎玖
 */
public class BaseFunction extends BasePlugin {
    public BaseFunction() {
        pluginName = "base_function";
    }

    @Override
    public void handleCommon(SingleEvent singleEvent) {
        if (singleEvent.getMessage().plainEqual("骰子") || singleEvent.getMessage().plainEqual("色子")) {
            singleEvent.sendAt(String.format("你投出的点数是：%d", CommonUtil.randomInteger(1, 6)));
        } else if (singleEvent.getMessage().plainEqual("硬币")) {
            if (CommonUtil.randomInteger() % 2 == 0) {
                singleEvent.sendAt("你抛出的是：正面");
            } else {
                singleEvent.sendAt("你抛出的是：反面");
            }
        } else if (singleEvent.getMessage().plainEqual("运势")) {
            MyUser user = HttpUtil.getLuck(singleEvent);
            int luck = user.getLuck();
            singleEvent.sendAt("你今天的运势是：" + luck);
        }
        // 塔罗牌
        else if (singleEvent.getMessage().plainStartWith("tarot")) {
            String url = "http://175.178.4.128:9000/tarot";
//            String url = "http://localhost:8000/tarot";
            HttpResponse response = HttpRequest.sendGet(url, "code=" + java.net.URLEncoder.encode(singleEvent.getMessage().getPlainString().replaceAll(" +", ""), StandardCharsets.UTF_8));
            if (!response.getData().getString("data").equals("指令错误")) {
                singleEvent.send(response.getData().getString("data"));
            }
        }
        // 随机字符串
        else if (singleEvent.getMessage().plainStartWith("随机字符串")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("随机字符串");
            int length = CommonUtil.getInteger(analysis.getText());
            if (length == 0) {
                length = 8;
            } else if (length > 128) {
                length = 128;
            }
            singleEvent.send(CommonUtil.randomString(length));
        }
    }

    @Override
    public void handleFriend(SingleEvent singleEvent) {

    }

    @Override
    public void handleGroup(SingleEvent singleEvent) {
        if (singleEvent.getGroupMessageEvent().getGroup().getBotPermission() != MemberPermission.MEMBER) {
            if (singleEvent.getMessage().plainEqual("抽卡")) {
                int minute = CommonUtil.randomInteger(1, 15);
                singleEvent.send("恭喜抽中禁言：" + minute + "min");
                singleEvent.getGroupMessageEvent().getSender().mute(minute * 60);
            }
        }
    }
}
