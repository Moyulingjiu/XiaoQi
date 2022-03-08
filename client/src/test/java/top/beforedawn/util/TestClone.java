package top.beforedawn.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.util.ArrayList;

public class TestClone {

    @Test
    public void test1() {
        ArrayList<Long> a = new ArrayList<>();
        a.add(1L);
        a.add(2L);
        a.add(3L);
        ArrayList<Long> b = a;
        ArrayList<Long> c = new ArrayList<>(a);
        System.out.println(a);
        a.add(1, 210L);
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class TestObject {
    public int a;
    public String text;
}
