import org.junit.Test;
import top.beforedawn.util.HttpRequest;
import top.beforedawn.util.HttpResponse;

import java.util.Locale;

public class TestRpg {
    @Test
    public void test1() {
        String url = "http://127.0.0.1:8000/rpg";
        String json = "{\n" +
                "    \"text\": \"签到\",\n" +
                "    \"qq\": 1597867839,\n" +
                "    \"member_name\": \"墨羽翎玖\",\n" +
                "    \"bot_name\": \"小玖\",\n" +
                "    \"be_at\": false,\n" +
                "    \"limit\": false\n" +
                "}";
        HttpResponse response = HttpRequest.sendPost(url, json);
        System.out.println(response);
    }

    @Test
    public void test2() {
        System.out.println(System.getProperty("os.name").toLowerCase(Locale.US));
    }
}
