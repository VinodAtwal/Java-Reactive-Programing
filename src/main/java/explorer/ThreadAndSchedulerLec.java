package explorer;

import Utility.Util;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import java.util.stream.IntStream;

public class ThreadAndSchedulerLec {

    public static void main(String[] args) {
//        defaultThread();
//        subscribeOn();
//        publishOn();
//        combination();
        parallelExecution();
        Util.sleepSecond(5);
    }

    /*
      Schedulers
    * boundedElastic        Network/Time-consuming calls <thread  = 10* no 0f Cpu>
    * parallel              CPU intensive tasks <thread == no 0f Cpu>
    * single                A single dedicated thread for one-off tasks
    * immediate             current thread

      Operators for Scheduling
    * subscribeOn           for upstream
    * publishOn             for downstream


    * <  Any thread pool but for single subscriber only single thread process data one by one there are not multiple thread pushing the data <no parallel execution>
     Flux<Object> flux = Flux.create(fluxSink -> {
            printThreadName("create");
            IntStream.range(1,6).forEach(i->fluxSink.next(i));
            fluxSink.complete();
        }).subscribeOn(Schedulers.boundedElastic())
                .subscribe(v->printThreadName("sub " +v)); // All item emit by same boundedElastic Thread
    />

    * */


    /* every thing is executed is run by single thread */
    static void defaultThread() {
        Flux<Object> flux = Flux.create(fluxSink -> {
            printThreadName("create");
            fluxSink.next(1);
        }).doOnNext(i -> printThreadName("next " + i));
        Runnable runnable = () -> flux.subscribe(v -> printThreadName("sub " + v));
        IntStream.range(1, 3).forEach(i -> new Thread(runnable).start());


    }

    /* Subscribe On
     * we can use multiple subscribe on
     * */
    static void subscribeOn() {
        Flux<Object> flux = Flux.create(fluxSink -> {
            printThreadName("create");
            fluxSink.next(1);
        }).doOnNext(i -> printThreadName("next " + i)).subscribeOn(Schedulers.newParallel("test1"));
        Runnable runnable = () -> flux.doFirst(() -> printThreadName("first2"))
                .subscribeOn(Schedulers.boundedElastic())
                .doFirst(() -> printThreadName("first1"))
                .subscribe(v -> printThreadName("sub " + v));
        IntStream.range(1, 3).forEach(i -> new Thread(runnable).start());
    }

    /*Publish On */
    static void publishOn() {
        Flux<Object> flux = Flux.create(fluxSink -> {
            printThreadName("create");
            fluxSink.next(1);
        }).doOnNext(i -> printThreadName("next1 " + i));
        flux.publishOn(Schedulers.boundedElastic())
                .doOnNext(i -> printThreadName("next2 " + i))
                .publishOn(Schedulers.parallel())
                .subscribe(v -> printThreadName("sub " + v));


    }

    /*publish on and subscribeOn combination*/
    static void combination() {
        Flux.create(fluxSink -> {
                    printThreadName("create");
                    fluxSink.next(1);
                })
                .publishOn(Schedulers.parallel())
                .doOnNext(i -> printThreadName("next2 " + i))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(v -> printThreadName("sub" + v));

    }

    /*Parallel Execution
    *
    * parallel // can pass param in method arg define number of threads
    * runOn
    *
    * Sequential we can use to convert parallel flux to single thread flux
    * */

    static void parallelExecution(){
        Flux.range(1,10)
                .parallel()
                .runOn(Schedulers.boundedElastic())
//                .sequential()
                .subscribe(v->printThreadName("sub "+v));
    }

    /*Flux.interval() using  parallel thread pool*/



    static void printThreadName(String msg) {
        System.out.println(msg + "\t\t: thread: " + Thread.currentThread().getName());
    }
}
