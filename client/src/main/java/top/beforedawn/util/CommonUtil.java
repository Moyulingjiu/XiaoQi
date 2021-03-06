package top.beforedawn.util;

import net.mamoe.mirai.message.data.*;
import top.beforedawn.models.bo.MyMessage;
import top.beforedawn.models.context.SerializeMessage;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

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

    public static String LocalDateTime2String(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return f.format(LocalDateTime2Date(dateTime));
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
     * 将LocalDate转为Date
     *
     * @param localDate 日期
     * @return Date类型的日期
     */
    public static Date LocalDateTime2Date(LocalDateTime localDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atZone(zoneId);
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
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (str.contains(".")) {
                str = str.substring(0, str.indexOf("."));
            }
            return Date2LocalDateTime(format.parse(str.replace("T", " ")));
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    /**
     * 获取消息转为自己的格式
     *
     * @param chain 消息链
     * @return 解析的消息
     */
    public static MyMessage analysisMessage(MessageChain chain, Long botId) {
        MyMessage myMessage = new MyMessage();
        myMessage.setOrigin(chain);
        for (SingleMessage message : chain) {
            if (message instanceof PlainText) {
                myMessage.getPlain().add(message.toString());
            } else if (message instanceof At) {
                if (((At) message).getTarget() == botId) {
                    myMessage.setBeAt(true);
                } else {
                    myMessage.getAt().add(((At) message).getTarget());
                }
            } else if (message instanceof FlashImage) {
                myMessage.getFlashImages().add(((FlashImage) message).getImage());
            } else if (message instanceof Image) {
                myMessage.getImages().add((Image) message);
            } else if (message instanceof Face) {
                myMessage.getFaces().add((Face) message);
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
    public static int randomInteger() {
        Random random = new Random();
        return random.nextInt();
    }

    /**
     * 生成一个不大于最大值的随机数
     *
     * @param max 最大值
     * @return 随机数
     */
    public static int randomInteger(int max) {
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
    public static int randomInteger(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    /**
     * 生成一个随机的字符串
     *
     * @param strings 字符串数组
     * @return 数组中随机的一个字符串
     */
    public static String randomString(ArrayList<String> strings) {
        if (strings == null || strings.size() == 0) {
            return "";
        }
        int index = randomInteger(strings.size());
        if (strings.get(index) == null) {
            return "";
        }
        return strings.get(index);
    }

    /**
     * 将消息序列化
     *
     * @param basePath 工作路径
     * @param chain    消息链
     * @return 序列化之后的消息
     */
    public static ArrayList<SerializeMessage> getSerializeMessage(String basePath, MessageChain chain) {
        ArrayList<SerializeMessage> serializeMessages = new ArrayList<>();
        for (SingleMessage singleMessage : chain) {
            if (singleMessage instanceof PlainText) {
                serializeMessages.add(new SerializeMessage(SerializeMessage.MessageType.PLAIN, ((PlainText) singleMessage).getContent()));
            } else if (singleMessage instanceof Face) {
                serializeMessages.add(new SerializeMessage(SerializeMessage.MessageType.EMOJI, ((Face) singleMessage).getId() + ""));
            } else if (singleMessage instanceof At) {
                serializeMessages.add(new SerializeMessage(SerializeMessage.MessageType.AT, ((At) singleMessage).getTarget() + ""));
            } else if (singleMessage instanceof Image) {
                String url = Image.queryUrl((Image) singleMessage);
                String path = UUID.randomUUID() + ".png";
                if (HttpRequest.downloadPicture(url, basePath + path)) {
                    serializeMessages.add(new SerializeMessage(SerializeMessage.MessageType.IMAGE, basePath + path));
                }
            }
        }
        return serializeMessages;
    }

    /**
     * 反序列化消息
     *
     * @param singleEvent       事件
     * @param serializeMessages 序列化的消息
     * @return 消息链
     */
    public static MessageChainBuilder getMessageChain(SingleEvent singleEvent, ArrayList<SerializeMessage> serializeMessages) {
        MessageChainBuilder builder = new MessageChainBuilder();
        for (SerializeMessage serializeMessage : serializeMessages) {
            switch (serializeMessage.getType()) {
                case PLAIN:
                    builder.append(new PlainText(serializeMessage.getContext()));
                    break;
                case IMAGE:
                    if (exists(serializeMessage.getContext()))
                        builder.append(singleEvent.uploadImage(serializeMessage.getContext()));
                    break;
                case AT:
                    builder.append(new At(getLong(serializeMessage.getContext())));
                    break;
                case EMOJI:
                    builder.append(new Face(getInteger(serializeMessage.getContext())));
                    break;
            }
        }
        return builder;
    }

    /**
     * 删除序列中保存在本地的图片文件
     *
     * @param serializeMessages 消息序列
     * @return 是否删除成功
     */
    public static boolean removeImageFile(ArrayList<SerializeMessage> serializeMessages) {
        return true;
        // todo: 对于复制回复之后，图片的占用可能不止是一个回复在占用，所以需要重新评估如何实现图片资源的回收
//        boolean ans = true;
//        for (SerializeMessage serializeMessage : serializeMessages) {
//            if (serializeMessage.getType() == SerializeMessage.MessageType.IMAGE) {
//                ans = ans && deleteFile(serializeMessage.getContext());
//            }
//        }
//        return ans;
    }

    /**
     * 删除文件
     *
     * @param path 路径
     * @return 删除成功与否
     */
    public static boolean deleteFile(String path) {
        File file = new File(path);
        return file.delete();
    }

    /**
     * 文件是否存在
     *
     * @param path 路径
     * @return 是否存在
     */
    public static boolean exists(String path) {
        if (path == null) return false;
        File file = new File(path);
        return file.exists();
    }

    /**
     * 确认信息
     *
     * @return 确认信息
     */
    public static String confirmMessage() {
        return "请输入“确认”来确认当前操作。";
    }

    /**
     * 是否是确定消息
     *
     * @param message 消息
     * @return boolean
     */
    public static boolean isConfirmMessage(String message) {
        if (message.equals("确认")) return true;
        if (message.equals("是")) return true;
        if (message.equals("我确定")) return true;
        if (message.equals("确定")) return true;
        return false;
    }

    /**
     * 随机字符串
     *
     * @param len 长度
     * @return 随机字符串
     */
    public static String randomString(int len) {
        char[] chars = new char[]{
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
        };
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            builder.append(chars[(int) (Math.random() * chars.length)]);
        }
        return builder.toString();
    }
}
