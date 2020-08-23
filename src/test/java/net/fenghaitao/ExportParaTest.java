package net.fenghaitao;

import net.fenghaitao.parameters.ExportPara;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExportParaTest {
//    @Test
//    public void newExportPara() {
//        ExportPara exportPara = new ExportPara();
//        List<User> users = new ArrayList<>();
//        users.add(new User("coco", 28, new Date()));
//        users.add(new User("ivan", 18, new Date()));
//        users.add(new User("fht", 8, new Date()));
//        exportPara.setDataSource(users);
//    }

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
    }
}
