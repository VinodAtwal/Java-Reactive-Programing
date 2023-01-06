package explorer;

import Utility.Util;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicReference;

public class SubscriptionLec {

    public static void main(String[] args) {
        AtomicReference<Subscription> atomicReference = new AtomicReference<>();
        Flux.range(1,20)
                .log()
                .subscribeWith(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("Received subscription: "+s);
                atomicReference.set(s);
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("onNext: "+integer);

            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError: "+t.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");

            }
        });
        Util.sleepSecond(3);
        atomicReference.get().request(3);
        Util.sleepSecond(5);
        atomicReference.get().request(3);
        Util.sleepSecond(3);
        System.out.println("canceling subscription");
        atomicReference.get().cancel();
        Util.sleepSecond(3);
        atomicReference.get().request(5);
        Util.sleepSecond(3);


    }
}
