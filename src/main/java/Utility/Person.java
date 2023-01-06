package Utility;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Person {
    String name;
    Integer age;
    public Person() {
        this.name = Util.faker().name().fullName();
        this.age = Util.faker().random().nextInt(5,30);
    }
}
