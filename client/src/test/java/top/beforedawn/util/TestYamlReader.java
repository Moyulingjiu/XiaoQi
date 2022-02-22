package top.beforedawn.util;

import org.junit.Test;
import top.beforedawn.models.bo.Blacklist;

import java.util.ArrayList;

public class TestYamlReader {
    @Test
    public void testRead1() {
        YamlReader yamlReader = new YamlReader("C:/mirai/config.yml");
        String name = (String) yamlReader.getValueByKey("name");
        System.out.println(name);
        ArrayList<Object> a = (ArrayList<Object>) yamlReader.getValueByKey("blacklist_member");
        System.out.println(a);
        System.out.println(a.getClass().getName());
        System.out.println(a.get(0).getClass().getName());

        ArrayList<Long> admin = (ArrayList<Long>) yamlReader.getValueByKey("admin");
        System.out.println(admin);
        ArrayList<Long> systemSuperAdministrator = (ArrayList<Long>) yamlReader.getValueByKey("system_super_administrator");
        System.out.println(systemSuperAdministrator);
    }

    @Test
    public void testWrite1() {
        ArrayList<Long> arrayList = new ArrayList<>();
        arrayList.add(1234567890L);
        System.out.println(arrayList.contains(1234567890L));
    }
}
