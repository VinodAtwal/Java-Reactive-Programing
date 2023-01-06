package Utility;

import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

public class NameGenerator {

    List<String> list = new ArrayList<>();

    public  Flux<String> generateName(){
        return Flux.generate(synchronousSink -> {
            Util.sleepSecond(1);
            String name = Util.faker().name().fullName();
            System.out.println("generated Name..");
            list.add(name);
            synchronousSink.next(name);
        })
                .cast(String.class)
                .startWith(getFromCache());
    }

     public Flux<String> getFromCache(){
        return Flux.fromIterable(list);
    }
}
