package explorer;

import Utility.Util;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/*Repeat And Retry
*
* repeat        Resubscribe after complete signal
* retry         Resubscribe after error signal
* */
public class RepeatAndRetry {

    static AtomicInteger integer = new AtomicInteger(1);
    public static void main(String[] args) {
//    repeat();
//        retry();
        retryWhenAdvance();
    }

    static Supplier<Flux<Integer>> getIntegers = ()-> Flux.range(1,3)
            .doOnSubscribe(s-> System.out.println("Subscribed"))
            .doOnComplete(()-> System.out.println("--complete"))
            .map(i-> integer.getAndIncrement());
    //            .map(i->i/0);


    /*Repeat
    * if not passing param it will try to call indefinitely
    * but stop in error signal
    * can also have boolean supplier for repeat till some condition
    * */
    static void repeat(){

        getIntegers.get()
//                .repeat(2)
                .repeat(()->integer.get()<14)
                .subscribe(Util.subscriber());
    }

    static Supplier<Flux<Integer>> getIntegersWithError = ()-> Flux.range(1,3)
            .doOnSubscribe(s-> System.out.println("Subscribed"))
            .doOnComplete(()-> System.out.println("--complete"))
            .map(i->i/0)
            .doOnError(err-> System.out.println("--error"));

    /* Retry
    * retry only on error
    * retry comes with some variation like retry when
    *
    * */

    static void retry(){
        getIntegersWithError.get()
//                .retry(2)
                .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2)))
                .subscribe(Util.subscriber());

        Util.sleepSecond(15);
    }

    static void retryWhenAdvance(){
        Consumer<String> processPayment = ccNumber->{
            int random = Util.faker().random().nextInt(1,10);
            if(random<8) throw new RuntimeException("500");
            else if (random<10) throw new RuntimeException("404");
        };

        Function<String, Mono<String>> orderService = ccNumber->Mono.fromSupplier(
                ()->{
                    processPayment.accept(ccNumber);
                    return Util.faker().idNumber().valid();
                }
        );
        orderService.apply(Util.faker().business().creditCardNumber())
                .doOnError(err-> System.out.println("error Message: "+err.getMessage()))
                .retryWhen(Retry.from(flux->flux.doOnNext(retrySignal -> {
                    System.out.println("total retry "+ retrySignal.totalRetries());
                    System.out.println("retry signal failure Message"+retrySignal.failure());
                }).handle(((retrySignal, synchronousSink) -> {
                    if(retrySignal.failure().getMessage().equals("500"))
                        synchronousSink.next("1");
                    else synchronousSink.error(retrySignal.failure());
                        })).delayElements(Duration.ofSeconds(1))
                ))
                .subscribe(Util.subscriber());
        Util.sleepSecond(30);

    }






}
