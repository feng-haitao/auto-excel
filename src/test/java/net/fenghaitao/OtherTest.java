package net.fenghaitao;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OtherTest {
    @Test
    public void Test1() {
        Pattern pattern = Pattern.compile("c");
        Matcher m = pattern.matcher("abcdecg@126.com");
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, m.group().toUpperCase());
        }
        m.appendTail(sb);
        System.out.println(sb.toString());
    }
    @Test
    public void Test2() {
        Pattern pattern = Pattern.compile("\\Q(abc)\\E!\\$([a-z]+)\\$([0-9]+)");
        Matcher matcher = pattern.matcher("(abc)!$h$3");
        if(matcher.find()) {
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }
    }
    @Test
    public void Test3() throws IllegalAccessException, NoSuchFieldException {
        Object obj = new User("ivan", 15, new Date());
        Class clz = obj.getClass();
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            System.out.println(field.getName());
        }

        Field field = clz.getDeclaredField("name");
        if (field != null) {
            field.setAccessible(true);
            field.set(obj, "fht");
            System.out.println("修改后的User.name： " + field.get(obj));
        }
    }

    public class User {
        private String name;
        private int age;
        private Date birthday;

        public User(String name, int age, Date birthday) {
            this.name = name;
            this.age = age;
            this.birthday = birthday;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Date getBirthday() {
            return birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", birthday=" + birthday +
                    '}';
        }
    }
}
