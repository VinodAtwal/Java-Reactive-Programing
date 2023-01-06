package explorer;

import Utility.Util;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;
import reactor.util.concurrent.Queues;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class BackPressureOverflowStrategy {

    /*
    * OverFlow Strategy
    *
    * buffer    keep in memory
    * drop      Once the queue is full, new items will be dropped
    * latest    Once the queue is full, keep 1 latest item as and when it arrives. drop old
    * error     throw error to downstream
    *
    *Note: reactor.util.concurrent.Queues is the class using for store buffer data and some threshold define there and
    * we can change these threshold value;
    * System.setProperty("reactor.bufferSize.small", "256")
    * */

    public static void main(String[] args) {
//        buffer();
//        drop();
//        latest();
//        error();
    }

    /* Buffer
    * Default behaviour <no need to provide this strategy>
    * Can cause of Out Of Memory
    * You can define size of buffer by passing arguments <buffer(20)>
    * */
    static void buffer(){
        Flux.create(fluxSink -> {
            IntStream.range(0,500).takeWhile(i-> !fluxSink.isCancelled()).forEach(i-> {
                fluxSink.next(i);
                System.out.println("Pushed: "+ i);
                Util.sleepMillis(1);
            });
            fluxSink.complete();
        }).onBackpressureBuffer(50, o-> System.out.println("Dropped item: "+o))
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(i->{
                    Util.sleepMillis(10);}).subscribe(Util.subscriber());

        Util.sleepSecond(10);
    }

    /* DROP
    * when 75% of queue is empty then it will take further request
    * */
    static void drop(){
        System.setProperty("reactor.bufferSize.small", "20");

        List<Object> list = new ArrayList<>();

        Flux.create(fluxSink -> {
                    IntStream.range(1,201).forEach(i-> {
                        fluxSink.next(i);
                        System.out.println("Pushed: "+ i);
                        Util.sleepMillis(1);
                    });
                    fluxSink.complete();
                    // we can capture dropped value or send it to some other publisher also
                }).onBackpressureDrop(list::add)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(i->{
                    Util.sleepMillis(10);}).subscribe(Util.subscriber());

        Util.sleepSecond(10);
        System.out.println(list);
    }


    /*Latest */
    static void latest(){
        System.setProperty("reactor.bufferSize.small", "20");
        Flux.create(fluxSink -> {
                    IntStream.range(1,201).forEach(i-> {
                        fluxSink.next(i);
                        System.out.println("Pushed: "+ i);
                        Util.sleepMillis(1);
                    });
                    fluxSink.complete();
                    // we can capture dropped value or send it to some other publisher also
                }).onBackpressureLatest()
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(i->{
                    Util.sleepMillis(10);}).subscribe(Util.subscriber());

        Util.sleepSecond(10);
    }

    /*Error */

    static void error(){
        System.setProperty("reactor.bufferSize.small", "20");
        Flux.create(fluxSink -> {
                    IntStream.range(1,201).takeWhile(i->!fluxSink.isCancelled()).forEach(i-> {
                        fluxSink.next(i);
                        System.out.println("Pushed: "+ i);
                        Util.sleepMillis(1);
                    });
                    fluxSink.complete();
                    // we can capture dropped value or send it to some other publisher also
                }).onBackpressureError()
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(i->{
                    Util.sleepMillis(10);}).subscribe(Util.subscriber());

        Util.sleepSecond(10);
    }

    /* while creating flux we can directly provider overflow strategy
    * Flux.create(fluxSink->{ .... },FluxSink.OverflowStrategy.BUFFER )....
    * */


}
