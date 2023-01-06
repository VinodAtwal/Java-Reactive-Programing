package explorer;

import Utility.Util;
import reactor.core.publisher.Flux;
import java.time.Duration;
import java.util.stream.Stream;

public class HotColdPublisher {
    public static void main(String[] args) {
//        coldPublish();
//        hotPublisher();
//        hotPublisherRefCount();
//        hotPublisherAutoConnect();
//        hotPublisherCache();

    }


    /*Hot Publish cache
    * cache = publish().replay()
    * cache(param) param is not of history it will store
    * max history = Integer.MaxValue
    * */
    static void hotPublisherCache(){
        Flux<String> movieStream = Flux.fromStream(()->getMovie()).delayElements(Duration.ofSeconds(1)).cache(3);
        movieStream.subscribe(Util.subscriber("Sam"));
        Util.sleepSecond(5);
        System.out.println("mike is about to join");
        movieStream.subscribe(Util.subscriber("mike"));

        Util.sleepSecond(15);
    }


    /*Auto Connect
    * behave as same refcount but not resubscribe for different subs after emitting all data <Pure hot publisher>
    * if min subs zero it start emitting data even if subs not there
    * */

    static void hotPublisherAutoConnect(){
        Flux<String> movieStream = Flux.fromStream(()->getMovie()).delayElements(Duration.ofSeconds(1)).publish().autoConnect(1);
        movieStream.subscribe(Util.subscriber("Sam"));
        Util.sleepSecond(10);
        movieStream.subscribe(Util.subscriber("mike"));

        Util.sleepSecond(60);
    }




    /* publish for hot publisher

    *  share  = publish90.refcount(1)
    * min count means requirement of min subs to emit data
    * if next subs join after finishing of all data then publisher start with new subs from start
    * */

    static void hotPublisherRefCount(){
        Flux<String> movieStream = Flux.fromStream(()->getMovie()).delayElements(Duration.ofSeconds(2))
                .publish().refCount(2);
        movieStream.subscribe(Util.subscriber("Sam"));
        Util.sleepSecond(5);
        movieStream.subscribe(Util.subscriber("mike"));

        Util.sleepSecond(60);
    }

    /*Hot publisher*/
    static void hotPublisher(){
        Flux<String> movieStream = Flux.fromStream(()->getMovie()).delayElements(Duration.ofSeconds(2)).share();
        movieStream.subscribe(Util.subscriber("Sam"));
        Util.sleepSecond(5);
        movieStream.subscribe(Util.subscriber("mike"));

        Util.sleepSecond(60);
    }

    /*Cold Publisher*/
    static void coldPublish(){
        Flux<String> movieStream = Flux.fromStream(()->getMovie()).delayElements(Duration.ofSeconds(2));
        movieStream.subscribe(Util.subscriber("Sam"));
        Util.sleepSecond(5);
        movieStream.subscribe(Util.subscriber("mike"));

        Util.sleepSecond(60);

    }


    private static Stream<String> getMovie(){
        System.out.println("Got movie streaming req");
        return Stream.of(
                "Scene 1", "Scene 2", "Scene 3", "Scene 4", "Scene 5", "Scene 6"
        );
    }
}
