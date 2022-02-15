package top.beforedawn.util;

import org.junit.Test;

public class TestRequest {
    @Test
    public void test1() {
        HttpResponse response = HttpRequest.sendGet("http://localhost:8080/user/luck/1597867839", "botId=1812322920");
        System.out.println(response);
    }
}
