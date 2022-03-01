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

    @Test
    public void testPost() {
        String url = "http://localhost:8080/drifting_bottle/drifting_bottle";
        String json = "{\n" +
                "    \"botId\": 1812322920,\n" +
                "    \"userId\": 1597867839,\n" +
                "    \"text\": \"测试的漂流瓶2\",\n" +
                "    \"permanent\": 0\n" +
                "}";
        HttpResponse response = HttpRequest.sendPost(url, json);
        System.out.println(response);
    }
}
