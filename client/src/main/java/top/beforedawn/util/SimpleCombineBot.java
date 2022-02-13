package top.beforedawn.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mamoe.mirai.Bot;
import top.beforedawn.config.BotConfig;

/**
 * 组合机器人与其配置类
 *
 * @author 墨羽翎玖
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleCombineBot {
    private BotConfig config;
    private Bot bot;
}