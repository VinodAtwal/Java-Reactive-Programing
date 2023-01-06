package Utility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DefaultSubscriber implements Subscriber<Object> {
    private String name="";


    @Override
    public void onSubscribe(Subscription s) {
        s.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(Object o) {
        System.out.println(name +" received: "+o);

    }

    @Override
    public void onError(Throwable t) {
        System.out.println(name+" error: "+t.getMessage());

    }

    @Override
    public void onComplete() {
        System.out.println(name+" completed");

    }
}
