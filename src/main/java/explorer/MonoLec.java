package explorer;

import Utility.Util;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CompletableFuture;

public class MonoLec {
    /*Subscribe takes three method param (consumer)
    *   onNext - Consumer<T>
        onError - Consumer<Throwable>
        onComplete - Runnable
    * */
    public static void main(String[] args) {
//        basic();
//        lec4();
//        lec5();
//        lec6();
//        lec7();
//        lec7_2();
        monoFromFlux();

    }
    /*Mono from flux
        with help of next()
    * */
    private static void monoFromFlux(){
        Flux.range(1,5).next().subscribe(Util.onNext());
    }

    /*
    * Mono from runnable used for some time consuming process like notification and post processing
    * for that we can use MONO from runnable
    * */

    private static void lec7_2(){
        Mono.fromRunnable(()->{
            Util.sleepSecond(3);
            System.out.println("Operation Complete");
        }).subscribe(Util.onNext(), Util.onError(), Util.onComplete());
    }

    //Lec 7 Mono from future
    private static void lec7(){
        Mono.fromFuture(getNameFuture()).subscribe(Util.onNext());
        Util.sleepSecond(1);

    }

    private static CompletableFuture<String> getNameFuture(){
        return CompletableFuture.supplyAsync(()-> Util.FAKER.name().fullName());
    }



    //block() method blocking the main thread until result is received
    /*String name = getName().subscribeOn(Schedulers.boundedElastic()).block();
    * */

    //lec6 Async process
    private static void lec6(){
        getNameAsync();
        getNameAsync().subscribeOn(Schedulers.boundedElastic())
                .subscribe(Util.onNext());
        getNameAsync();
        Util.sleepSecond(4);
    }

    private static Mono<String> getNameAsync(){
        System.out.println("entered get name method");
        return Mono.fromSupplier(()->{
            System.out.println("generating Name");
            Util.sleepSecond(3);
            return Util.FAKER.name().fullName();
        });
    }




    //lec5 Mono from Supplier
    private static void lec5(){

        //user just only when you have data early
        //Mono<String> mono = Mono.just(getName());
        Mono<String> mono = Mono.fromSupplier(() -> getName());
        mono.subscribe(data -> System.out.println(data));


    }

     private static String getName(){
         System.out.println("generating name......");
         return Util.faker().name().fullName();
     }



    //lec 4 mono empty , on error

    private static Mono<String> userRepo(int userId){
        if(userId ==1){
            return Mono.just(Util.faker().name().firstName());
        }else if(userId ==2){
            return Mono.empty();
        }
        return Mono.error(new RuntimeException("Not in the allowed range"));

    }

    static void lec4(){
        userRepo(3).subscribe(
          Util.onNext(), Util.onError(), Util.onComplete()
        );
    }

    //lec1
    static void basic(){
        Mono<Integer> mono = Mono.just(1) ;
//        mono.subscribe(System.out::println);

        mono.subscribe(
                Util.onNext(),
                Util.onError(),
                Util.onComplete()
        );

    }


}
