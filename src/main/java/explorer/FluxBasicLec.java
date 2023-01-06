package explorer;

import Utility.Util;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class FluxBasicLec {
    public static void main(String[] args) {
//    lec1();
//        lec2();
        fluxFromMono();
    }

    /*flux from Mono
    * */

    static void fluxFromMono(){
        Mono<String > mono = Mono.just("a");
        Flux.from(mono).subscribe(item-> System.out.println(item));
    }

    /*
    * flux from interval using different thread pool */
    static void lec2(){
        Flux.interval(Duration.ofSeconds(1)).subscribe(Util.onNext());
        Util.sleepSecond(5);
    }

    /* flux.just * */
    static void lec1(){

        //Flux from range method
        Flux.range(1,10).log().subscribe(Util.onNext());

        //flux from stream "consumed only once"
        Flux.fromStream(Arrays.asList("a", "b", "c").stream());

        // flux from array/List
        List<String> strings = Arrays.asList("a", "b", "c");
        Flux.fromIterable(strings);

        //flux from just
        Flux<Object> flux = Flux.just(1, 2, 3, 4, "a", Util.faker().name().firstName());
        /*flux.subscribe(
                Util.onNext(),
                Util.onError(),
                Util.onComplete()
        );

        flux.subscribe(  Util.onNext(),
                Util.onError(),
                Util.onComplete());*/
    }
}
