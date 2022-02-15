package top.beforedawn.util;

import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestDate {
    @Test
    public void test1() {
        String str = "2022-02-16 17:32:04";
        System.out.println(CommonUtil.getLocalDateTime(str));

        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println(CommonUtil.Date2LocalDateTime(format.parse(str)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
