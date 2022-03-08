package top.beforedawn.plugins;

import top.beforedawn.config.GroupPool;
import top.beforedawn.models.bo.MyGroup;
import top.beforedawn.util.CommonUtil;
import top.beforedawn.util.HttpUtil;
import top.beforedawn.util.SingleEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class SelfReplyFunction extends BasePlugin {
    private static final String[] screenWords = new String[]{
            "老公", "草", "未获取到相关信息", "上课", "{", "暂时没这功能", "佢應", "這樣",
            "不要啊皇上", "有錢會好D囉", "太监", "聽", "我妈没教我", "杨幂", "没什么话题聊了是吗",
            "切糕", "男人", "辉煌", "灰灰", "杨秀", "撸", "寶貝", "上床", "操", "春袋",
            "下面", "领导", "无法理解您的话", "帮助", "身體", "當", "難", "扒著腿", "~",
            "腿", "JJ", "鸡", "*", "噶", "歲", "兽", "便便", "大便", "香港腳", "MM",
            "咪亂叫", "啪", "伦家", "二级域名", "元", "me", "UC", "爹", "吹啊", "饥渴", "B",
            "冇你勁", "輕點", "搖搖", "性感", "骚", "喜欢跟妈妈一起穿着丝袜被Terrance推车",
            "他正在和小狗狗亚麻地。。。", "快来，抠玩我给你玩，随便你搞，劳资说话算数！", "傻逼",
            "谈谈情，跳跳舞，跟我搞暧昧啊", "嗯嗯啊", "恶心什么呀恶心，你才恶心", "求草", "吴杰峰",
            "草泥马", "群主", "tianyu", "你可以教", "宇哥", "你使劲曰吧", "梦魇是我爹", "欢迎来日",
            "是母猪", "周超辉", "把裤子脱了", "性"
    };

    private String getMessage(SingleEvent singleEvent, String message, boolean forceRely) {
        String reply = HttpUtil.getQingYunKe(message);
        reply = reply.replace("{br}", "\n").replace("菲菲", singleEvent.getBotName());
        boolean allowReply = true;
        for (String screenWord : screenWords) {
            if (reply.contains(screenWord)) {
                allowReply = false;
                break;
            }
        }
        if ((reply.contains("℃") || message.startsWith("计算")) && forceRely) return reply;
        if (!allowReply) {
            if (forceRely)
                reply = CommonUtil.randomString(new ArrayList<>(Arrays.asList("不知道哦~", "这是什么意思呢", "嘤嘤嘤，不知道你在说什么", "无聊的话")));
            else
                reply = null;
        }
        return reply;
    }

    @Override
    public boolean before(SingleEvent singleEvent) {
        if (singleEvent.getMessage().plainStartWith("天气")) return true;
        if (singleEvent.getMessage().plainStartWith("计算")) return true;
        if (singleEvent.isFriendMessage()) return false;
        MyGroup group = GroupPool.get(singleEvent);
        return group.isSelfReply();
    }

    @Override
    public void handleCommon(SingleEvent singleEvent) {
        if (singleEvent.getMessage().plainStartWith("天气"))
            singleEvent.send(getMessage(singleEvent, singleEvent.getMessage().getPlainString(), true));
        else if (singleEvent.getMessage().plainStartWith("计算"))
            singleEvent.send(getMessage(singleEvent, singleEvent.getMessage().getPlainString().replace("+", "加"), true));
        else {
            if (CommonUtil.randomInteger(1000) < 10 || singleEvent.getMessage().isBeAt()) {
                singleEvent.send(getMessage(singleEvent, singleEvent.getMessage().getPlainString(), false));
            }
        }
    }

    @Override
    public void handleFriend(SingleEvent singleEvent) {

    }

    @Override
    public void handleGroup(SingleEvent singleEvent) {

    }
}
