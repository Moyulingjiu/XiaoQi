package top.beforedawn.util;

import org.junit.Test;

public class TestRequest {
    @Test
    public void test1() {
        HttpResponse response = HttpRequest.sendGet("http://localhost:8080/user/luck/1597867839", "botId=1812322920");
        System.out.println(response);
    }

    @Test
    public void testConvention() {
        String convention = HttpUtil.convention(1812322920L);
        System.out.println(convention);
    }
}
