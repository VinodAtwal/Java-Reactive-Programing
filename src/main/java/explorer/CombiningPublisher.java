package explorer;

import Utility.NameGenerator;
import Utility.Util;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class CombiningPublisher {

    /*Options
    *
    * startWith
    * concat
    * merge
    * zip
    * combineLatest
    * */
    public static void main(String[] args) {
//        startWith();
//        concatWith();
//        merge();
//        zip();
        combineLatest();
    }

    /* startWith
    * */
    static void startWith(){
        NameGenerator generator = new NameGenerator();
        generator.generateName()
                .take(2)
                .subscribe(Util.subscriber("Sam"));
        generator.generateName()
                .take(2)
                .subscribe(Util.subscriber("Mike"));
        generator.generateName()
                .take(3)
                .subscribe(Util.subscriber("Jazz"));
    }

    /*concatWith */
    static void concatWith(){
        Flux<String> flux1 = Flux.just("a", "b");
        Flux<String> flux2 = Flux.just("c", "d");
        Flux<String> errorFlux = Flux.error(new RuntimeException("oops"));
        Flux<String> flux = flux1.concatWith(flux2);
//        flux.subscribe(Util.subscriber());
//        Flux.concat(flux1,errorFlux,flux2).subscribe(Util.subscriber("test 2"));
        Flux.concatDelayError(flux1,errorFlux,flux2).subscribe(Util.subscriber("test 3"));
    }

    /*Merge */
    static void merge(){
        Flux.merge(getQatarFlights(),getAmericanFlights(),getEmiratesFlights()).subscribe(Util.subscriber());
        Util.sleepSecond(15);

    }

    /*Zip */
    static void zip(){
        Flux<String> bodyFlux = Flux.range(1, 5).map(i -> "body "+i).delayElements(Duration.ofSeconds(1));
        Flux<String> engineFlux = Flux.range(1, 5).map(i -> "engine "+i);
        Flux<String> tyreFlux = Flux.range(1, 5).map(i -> "tyre "+i);
        Flux.zip(bodyFlux,engineFlux,tyreFlux).subscribe(Util.subscriber());
        Util.sleepSecond(10);


    }

    /*Combine latest <combine the latest item from publishers>*/

    static void combineLatest() {
        Flux<String> fastFlux1 = Flux.just("A", "B", "C", "D").delayElements(Duration.ofSeconds(1));
        Flux<Integer> slowFlux1 = Flux.just(1, 2, 3).delayElements(Duration.ofSeconds(3));
        Flux.combineLatest(fastFlux1,slowFlux1, (s,i)->s+i).subscribe(Util.subscriber());
        Util.sleepSecond(15);


    }

    /*Util Methods*/
    static Flux<String> getQatarFlights(){
        return Flux.range(1,Util.faker().random().nextInt(1,5)).delayElements(Duration.ofSeconds(1))
                .map(i-> "QATAR" + Util.faker().random().nextInt(100,999))
                .filter(i-> Util.faker().random().nextBoolean());
    }

    static Flux<String> getAmericanFlights(){
        return Flux.range(1,Util.faker().random().nextInt(1,10)).delayElements(Duration.ofSeconds(1))
                .map(i-> "AA" + Util.faker().random().nextInt(100,999))
                .filter(i-> Util.faker().random().nextBoolean());
    }
    static Flux<String> getEmiratesFlights(){
        return Flux.range(1,Util.faker().random().nextInt(1,10 )).delayElements(Duration.ofSeconds(1))
                .map(i-> "EMIRATES " + Util.faker().random().nextInt(100,999))
                .filter(i-> Util.faker().random().nextBoolean());
    }

}
