package top.beforedawn.plugins;

import top.beforedawn.config.GroupPool;
import top.beforedawn.models.bo.DriftingBottle;
import top.beforedawn.models.bo.MessageLinearAnalysis;
import top.beforedawn.models.bo.MyGroup;
import top.beforedawn.util.CommonUtil;
import top.beforedawn.util.HttpResponse;
import top.beforedawn.util.HttpUtil;
import top.beforedawn.util.SingleEvent;

/**
 * 漂流瓶
 *
 * @author 墨羽翎玖
 */
public class DriftingBottleFunction extends BasePlugin {
    public DriftingBottleFunction() {
        pluginName = "drifting_bottle";
    }

    @Override
    public boolean before(SingleEvent singleEvent) {
        if (singleEvent.isFriendMessage())
            return true;
        MyGroup group = GroupPool.get(singleEvent);
        return group.isDriftingBottle();
    }

    @Override
    public void handleCommon(SingleEvent singleEvent) {
        if (singleEvent.getMessage().plainEqual("捡漂流瓶") || singleEvent.getMessage().plainEqual("拾漂流瓶")) {
            DriftingBottle driftingBottle = HttpUtil.getDriftingBottle(singleEvent);
            if (driftingBottle != null) {
                singleEvent.send(driftingBottle.getText() + "——" + CommonUtil.LocalDateTime2String(driftingBottle.getCreate()));
            }
        } else if (singleEvent.getMessage().plainStartWith("扔漂流瓶")) {
            MessageLinearAnalysis analysis = new MessageLinearAnalysis(singleEvent.getMessage());
            analysis.pop("扔漂流瓶");
            HttpResponse response = HttpUtil.sendDriftingBottle(singleEvent, analysis.getText());
            if (response != null) {
                singleEvent.send("你的漂流瓶已经被扔向了大海~");
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
