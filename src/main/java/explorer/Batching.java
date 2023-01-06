package explorer;

import Utility.Util;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Batching{
    /*Options
    *
    * buffer
    * window
    * groups
    * */
    static AtomicInteger atomicInteger = new AtomicInteger(1);
    public static void main(String[] args) {
//        buffer();
//        bufferOverlapDrop();
//        window();
        groupBy();
    }

    /*  Buffer
    * if there is item less than batch size then it emit remain item
    * buffer can take time also or both ,no of item
    * */
    static void buffer(){
        Flux<String> eventStream = Flux.interval(Duration.ofMillis(300)).map(i -> "event " + i);
        eventStream
//                .buffer(5)
//                .buffer(Duration.ofSeconds(2))
                .bufferTimeout(5,Duration.ofSeconds(2))
                .subscribe(Util.subscriber());
        Util.sleepSecond(60);
    }

    /* Buffer overlap or drop
    *  with skip 1 buffer(3,1) -> 123, 234, 345
    * when skip is greater than maxSize it become dropping buffer buffer(3,5)-> 1 2 3,5 4 6, 10 11 12 <can be use for sampling >
    * */

    static void bufferOverlapDrop(){
        Flux<String> eventStream = Flux.interval(Duration.ofMillis(300)).map(i -> "event " + i);
        eventStream.buffer(3,5).subscribe(Util.subscriber());
        Util.sleepSecond(60);

    }

    /* Window
     * it gives you flux
     * window also take optional argument Duration
    **/

    static void window(){
        Flux<String> eventStream = Flux.interval(Duration.ofMillis(500)).map(i -> "event " + i);
        Function<Flux<String> , Mono<Integer>> saveEvents = stringFlux ->
            stringFlux.doOnNext(e-> System.out.println("saving "+e))
                    .doOnComplete(()->{
                        System.out.println("saved this batch");
                        System.out.println("----------------");
                    })
                    .then(Mono.just(atomicInteger.getAndIncrement()));


        eventStream
                .window(5)
                .flatMap(i-> saveEvents.apply(i))
                .subscribe(Util.subscriber());
        Util.sleepSecond(60);
    }

    /*groupBy
    *
    *
    * */

    static void groupBy(){
        BiConsumer<Flux<Integer>,Integer> process = (flux,key)->{
            System.out.println("Called for key: "+key);
            flux.subscribe(i-> System.out.println("Key: "+key+" item: "+i));
        };

        Flux.range(1,30)
                .delayElements(Duration.ofSeconds(1))
                .groupBy(i->i%2)
                .subscribe(gf->process.accept(gf,gf.key()));
        Util.sleepSecond(60);

    }


}
