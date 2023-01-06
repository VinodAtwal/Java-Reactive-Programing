package explorer;

import Utility.Util;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public class SinksLec {
    /*Sinks
    * |--------------|--------------|----------|
    * |Type             Behaviour      Pub:Sub |
    * |--------------|--------------|----------|
    * one               Mono            1:N
    * many - unicast    Flux            1:1
    * many - multicast  Flux            1:N
    * many - replay     Flux            1:N (with replay of all values to late subscriber)
    * */
    public static void main(String[] args) {
//        sinkOne();
//        sinkUnicast();
//        sinkThreadSafety();
//        sinkMulticast();
//        sinkMulticastDirectAllOrBestEfforts();
        sinkReplay();
    }

    static void sinkOne(){
        Sinks.One<Object> sink = Sinks.one();
        Mono<Object> mono = sink.asMono();
        mono.subscribe(Util.subscriber("sam"));
        mono.subscribe(Util.subscriber("tom"));
//        sink.tryEmitValue("hi");
        sink.emitValue("hello",(signalType, emitResult) -> {
            System.out.println(signalType.name());
            System.out.println(emitResult.name());
            return false; // should retry or not
        });

    }
    /*unicast only single subs */
    static void sinkUnicast(){
        //handle through which we would push items
        Sinks.Many<Object> sink = Sinks.many().unicast().onBackpressureBuffer();
        // handle through which subs as listening
        Flux<Object> flux = sink.asFlux();
        flux.subscribe(Util.subscriber("sam"));
//        flux.subscribe(Util.subscriber("mike"));
        sink.tryEmitNext("hi");
        sink.tryEmitNext("hi 1");
    }
    static void sinkThreadSafety(){
        Sinks.Many<Object> sink = Sinks.many().unicast().onBackpressureBuffer();
        Flux<Object> flux = sink.asFlux();
        ArrayList<Object> objects = new ArrayList<>();
        flux.subscribe(objects::add);
       /* IntStream.range(0,1000).forEach(i->{
            final int j=i;
            CompletableFuture.runAsync(()->{
                sink.tryEmitNext(j);
            });
        });*/
        // with emit failure handle
        IntStream.range(0,1000).forEach(i->{
            final int j=i;
            CompletableFuture.runAsync(()->{
                sink.emitNext(j,(s,e)->true);
            });
        });
        Util.sleepSecond(3);
        System.out.println(objects.size());
    }

    /*
    * when there is no subscriber emitted item store in buffer after that late join subscriber got latest emitted item*/
    static void sinkMulticast(){
        Sinks.Many<Object> sink = Sinks.many().multicast()
                .onBackpressureBuffer();
        Flux<Object> flux = sink.asFlux();
//        flux.subscribe(Util.subscriber("sam"));
//        flux.subscribe(Util.subscriber("mike"));
        sink.tryEmitNext("hi");
        sink.tryEmitNext("hiii 2");
        flux.subscribe(Util.subscriber("sam"));
        flux.subscribe(Util.subscriber("mike"));
        sink.tryEmitNext("hiiii 3");
    }
    /*Direct All noting OR DirectBestEfforts*/

    static void sinkMulticastDirectAllOrBestEfforts(){
        System.setProperty("reactor.bufferSize.small", "16");
        Sinks.Many<Object> sink = Sinks.many().multicast()
                .directBestEffort();
//                .directAllOrNothing();
        Flux<Object> flux = sink.asFlux();
        flux.subscribe(Util.subscriber("sam"));
        flux.delayElements(Duration.ofMillis(200)).subscribe(Util.subscriber("mike"));
        IntStream.range(0,100).forEach(sink::tryEmitNext);
        Util.sleepSecond(29);
    }


    /*Replay */
    static void sinkReplay(){
        Sinks.Many<Object> sink = Sinks.many().replay().all();
        Flux<Object> flux = sink.asFlux();
        sink.tryEmitNext("hi");
        sink.tryEmitNext("hiii 2");
        flux.subscribe(Util.subscriber("sam"));
        flux.subscribe(Util.subscriber("mike"));
        sink.tryEmitNext("hiiii 3");
        flux.subscribe(Util.subscriber("jake"));


    }



}
