package Utility;

import com.github.javafaker.Faker;
import org.reactivestreams.Subscriber;

import java.util.function.Consumer;

public class Util {

    public static final Faker FAKER = Faker.instance();
    public static Consumer<Object> onNext(){
        return (item) -> System.out.println("Received: "+ item);
    }

    public static Consumer<Throwable> onError(){
        return (err) -> System.out.println("Error: "+ err.getMessage()) ;
    }

    public static Runnable onComplete(){
        return ()-> System.out.println("Completed");
    }

    public static Faker faker(){
        return FAKER;
    }

    public static void sleepMillis(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleepSecond(int seconds){
        sleepMillis(seconds*1000);
    }

    public static Subscriber<Object> subscriber(){
        return new DefaultSubscriber();
    }

    public static Subscriber<Object> subscriber(String name){
        return new DefaultSubscriber(name);
    }

}
