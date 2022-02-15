package top.beforedawn.service.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 通用工具类
 *
 * @author 墨羽翎玖
 */
public class Common {
    /**
     * 解析文本中的时间
     *
     * @param str 日期时间
     *            eg: 2020-01-01T12:32:00
     * @return LocalDateTime
     */
    public static LocalDateTime getLocalDateTime(String str) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(str, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据class实例化一个对象，并深度克隆bo中对应属性到这个新对象
     * 其中会自动实现modifiedBy和createdBy两字段的类型转换
     * <ul>
     *     <li>默认voClass有无参构造函数</li>
     * </ul>
     *
     * @param bo      business object
     * @param voClass vo对象类型
     * @return 浅克隆的vo对象
     * @author 墨羽翎玖
     */
    public static <T> T cloneVo(Object bo, Class<T> voClass) {
        if (bo == null) {
            return null;
        }
        Class boClass = bo.getClass();
        T newVo = null;
        try {
            //默认voClass有无参构造函数
            newVo = voClass.getDeclaredConstructor().newInstance();
            Field[] voFields = voClass.getDeclaredFields();
            Field[] boFields = boClass.getDeclaredFields();
            for (Field voField : voFields) {
                //静态和Final不能拷贝
                int mod = voField.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                voField.setAccessible(true);
                Field boField = null;
                try {
                    boField = boClass.getDeclaredField(voField.getName());
                    boField.setAccessible(true);
                }
                //bo中查找不到对应的属性，那就有可能为特殊情况xxx，需要由xxxId与xxxName组装
                catch (NoSuchFieldException e) {
                    //提取头部
                    String head = voField.getName();
                    Field boxxxNameField = null;
                    Field boxxxIdField = null;
                    for (Field bof : boFields) {
                        if (bof.getName().matches(head + "Name")) {
                            boxxxNameField = bof;
                        } else if (bof.getName().matches(head + "Id")) {
                            boxxxIdField = bof;
                        }
                    }
                    //找不到xxxName或者找不到xxxId
                    if (boxxxNameField == null || boxxxIdField == null) {
                        voField.set(newVo, null);
                        continue;
                    }

                    Object newSimpleRetVo = voField.getType().getDeclaredConstructor().newInstance();
                    Field newSimpleRetVoIdField = newSimpleRetVo.getClass().getDeclaredField("id");
                    Field newSimpleRetVoNameField = newSimpleRetVo.getClass().getDeclaredField("name");
                    newSimpleRetVoIdField.setAccessible(true);
                    newSimpleRetVoNameField.setAccessible(true);

                    //bo的xxxId和xxxName组装为SimpleRetVo的id,name
                    boxxxIdField.setAccessible(true);
                    boxxxNameField.setAccessible(true);
                    Object boxxxId = boxxxIdField.get(bo);
                    Object boxxxName = boxxxNameField.get(bo);

                    newSimpleRetVoIdField.set(newSimpleRetVo, boxxxId);
                    newSimpleRetVoNameField.set(newSimpleRetVo, boxxxName);

                    voField.set(newVo, newSimpleRetVo);
                    continue;
                }
                Class<?> boFieldType = boField.getType();
                //属性名相同，类型相同，直接克隆
                if (voField.getType().equals(boFieldType)) {
                    boField.setAccessible(true);
                    Object newObject = boField.get(bo);
                    voField.set(newVo, newObject);
                }
                //属性名相同，类型不同
                else {
                    boolean boFieldIsIntegerOrByteAndVoFieldIsEnum = ("Integer".equals(boFieldType.getSimpleName()) || "Byte".equals(boFieldType.getSimpleName())) && voField.getType().isEnum();
                    boolean voFieldIsIntegerOrByteAndBoFieldIsEnum = ("Integer".equals(voField.getType().getSimpleName()) || "Byte".equals(voField.getType().getSimpleName())) && boFieldType.isEnum();
                    boolean voFieldIsLocalDateTimeAndAndBoFieldIsZonedDateTime = ("LocalDateTime".equals(voField.getType().getSimpleName()) && "ZonedDateTime".equals(boField.getType().getSimpleName()));
                    boolean voFieldIsZonedDateTimeAndBoFieldIsLocalDateTime = ("ZonedDateTime".equals(voField.getType().getSimpleName()) && "LocalDateTime".equals(boField.getType().getSimpleName()));

                    try {
                        //整形或Byte转枚举
                        if (boFieldIsIntegerOrByteAndVoFieldIsEnum) {
                            Object newObj = boField.get(bo);
                            if ("Byte".equals(boFieldType.getSimpleName())) {
                                newObj = ((Byte) newObj).intValue();
                            }
                            Object[] enumer = voField.getType().getEnumConstants();
                            voField.set(newVo, enumer[(int) newObj]);
                        }
                        //枚举转整形或Byte
                        else if (voFieldIsIntegerOrByteAndBoFieldIsEnum) {
                            Object value = ((Enum) boField.get(bo)).ordinal();
                            if ("Byte".equals(voField.getType().getSimpleName())) {
                                value = ((Integer) value).byteValue();
                            }
                            voField.set(newVo, value);
                        }
                        //ZonedDateTime转LocalDateTime
                        else if (voFieldIsLocalDateTimeAndAndBoFieldIsZonedDateTime) {
                            ZonedDateTime newObj = (ZonedDateTime) boField.get(bo);
                            LocalDateTime localDateTime = newObj.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
                            voField.set(newVo, localDateTime);
                        }
                        //LocalDateTime转ZonedDateTime
                        else if (voFieldIsZonedDateTimeAndBoFieldIsLocalDateTime) {
                            LocalDateTime newObj = (LocalDateTime) boField.get(bo);
                            ZonedDateTime zdt = newObj.atZone(ZoneId.systemDefault());
                            voField.set(newVo, zdt);
                        } else {
                            voField.set(newVo, null);
                        }
                    }
                    //如果为空字段则不复制
                    catch (Exception e) {
                        voField.set(newVo, null);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newVo;
    }

    public static DecorativeReturnObject decorate(ReturnObject ret) {
        return new DecorativeReturnObject(ret);
    }

    public static DecorativeReturnObject decorate(ReturnNo returnNo) {
        return new DecorativeReturnObject(new ReturnObject(returnNo));
    }

    public static DecorativeReturnObject decorate(Object obj) {
        return new DecorativeReturnObject(new ReturnObject(obj));
    }

    public static DecorativeReturnObject decorate(ReturnNo returnNo, Object obj) {
        return new DecorativeReturnObject(new ReturnObject(returnNo, obj));
    }
}
