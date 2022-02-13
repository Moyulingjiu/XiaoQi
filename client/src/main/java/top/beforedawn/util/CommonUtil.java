package top.beforedawn.util;

import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.SingleMessage;
import top.beforedawn.models.bo.MyMessage;
import top.beforedawn.models.bo.SimpleBlacklist;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * 通用工具类
 *
 * @author 墨羽翎玖
 */
public class CommonUtil {

    /**
     * 将字符串转为整数
     *
     * @param str 转换的字符串
     * @return 整数
     */
    public static int getInteger(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 将字符串转为整数
     *
     * @param str 转换的字符串
     * @return 整数
     */
    public static long getLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 将字符串转为浮点数
     *
     * @param str 转换的字符串
     * @return 浮点数
     */
    public static double getDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * 将Date转为LocalDate
     *
     * @param date 日期
     * @return LocalDate类型的日期
     */
    public static LocalDate Date2LocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        // atZone()方法返回在指定时区从此Instant生成的ZonedDateTime。
        return instant.atZone(zoneId).toLocalDate();
    }

    /**
     * 将Date转为LocalDate
     *
     * @param date 日期
     * @return LocalDate类型的日期
     */
    public static LocalDateTime Date2LocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        // atZone()方法返回在指定时区从此Instant生成的ZonedDateTime。
        return instant.atZone(zoneId).toLocalDateTime();
    }

    /**
     * 将LocalDate转为Date
     *
     * @param localDate 日期
     * @return Date类型的日期
     */
    public static Date LocalDate2Date(LocalDate localDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
        return Date.from(zdt.toInstant());
    }

    /**
     * 通过格式化获取日期
     *
     * @param str 日期文本
     * @return 日期
     */
    public static LocalDate getLocalDate(String str) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return Date2LocalDate(format.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return LocalDate.now();
    }

    /**
     * 读取时间
     *
     * @param str 日期时间文本
     * @return 日期时间
     */
    public static LocalDateTime getLocalDateTime(String str) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ss");
            return LocalDate.parse(str, formatter).atStartOfDay();
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    /**
     * 是否需要提醒
     *
     * @param key key键值
     * @return 是否需要提醒
     */
    public static boolean needRemind(SimpleBlacklist key) {
        boolean remind = true;
        if (key.getLastRemindTime() != null) {
            remind = false;
            Duration duration = Duration.between(key.getLastRemindTime(), LocalDateTime.now());
            long days = duration.toDays();
            // 间隔天数大于1天，并且最多提醒十次
            if (days >= 1 && key.getRemind() < 10L) {
                remind = true;
            }
        }
        if (remind) {
            key.setRemind(key.getRemind() + 1);
            key.setLastRemindTime(LocalDateTime.now());
        }
        return remind;
    }

    /**
     * 获取消息转为自己的格式
     *
     * @param chain 消息链
     * @return 解析的消息
     */
    public static MyMessage analysisMessage(MessageChain chain, Long botId) {
        MyMessage myMessage = new MyMessage();
        for (SingleMessage message : chain) {
            if (message instanceof PlainText) {
                myMessage.getPlain().add(message.toString());
            } else if (message instanceof At) {
                if (((At) message).getTarget() == botId) {
                    myMessage.setBeAt(true);
                } else {
                    myMessage.getAt().add(((At) message).getTarget());
                }
            }
        }
        return myMessage;
    }

    /**
     * 装饰值
     *
     * @param obj value
     * @return 装饰后的值
     */
    public static Object decorateValue(Object obj) {
        Object ret;
        if (obj instanceof Integer) {
            ret = ((Integer) obj).longValue();
        } else {
            ret = obj;
        }
        return ret;
    }

    /**
     * 装饰数组
     *
     * @param objects 对象
     * @return 装饰后的数组
     */
    public static ArrayList<Long> decorateArrayList(ArrayList<Object> objects) {
        if (objects == null) {
            return new ArrayList<>();
        }
        ArrayList<Long> ans = new ArrayList<>();
        for (Object object : objects) {
            if (object instanceof Integer) {
                ans.add(((Integer) object).longValue());
            } else if (object instanceof Long) {
                ans.add((Long) object);
            }
        }
        return ans;
    }

    /**
     * 生成一个随机数
     *
     * @return 随机数
     */
    public static int RandomInteger() {
        Random random = new Random();
        return random.nextInt();
    }

    /**
     * 生成一个不大于最大值的随机数
     *
     * @param max 最大值
     * @return 随机数
     */
    public static int RandomInteger(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }

    /**
     * 生成一个范围内的随机数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 范围内的随机数
     */
    public static int RandomInteger(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }
}
