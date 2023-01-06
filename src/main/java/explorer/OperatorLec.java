package explorer;

import Utility.OrderService;
import Utility.UserService;
import Utility.Util;
import Utility.Person;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Function;
import java.util.stream.IntStream;

public class OperatorLec {

    public static void main(String[] args) {
//        operatorHandle();
//        operatorDoOn();
//        operatorLimitRate();
//        operatorDelay();
//        operatorOnError();
//        operatorTimeout();
//        operatorEmpty();
//        operatorTransform();
//        operatorSwitchOnFirst();
        operatorFlatMap();
    }

    /*FlatMap */
    static void operatorFlatMap(){
        UserService.getUsers()
                .flatMap(user -> OrderService.getOrders(user.getUserId()))
                .subscribe(Util.subscriber());
        Util.sleepSecond(8);
    }



    /*Switch on first
    * decision is taken only acc to first item if passes then apply transformation to incoming flux
    * */
    static void operatorSwitchOnFirst(){
        getPerson()
                .switchOnFirst((signal, personFlux)->{
                    return signal.isOnNext() && signal.get().getAge() >15 ?
                            personFlux:applyFilterMap().apply(personFlux);
                } )

                .subscribe(Util.subscriber());
    }


    /* Transform operator*/

    static void operatorTransform(){
     getPerson()
             .transform(applyFilterMap())
             .subscribe(Util.subscriber());
    }

    static Flux<Person> getPerson(){
        return Flux.range(1,10).map(i->new Person());
    }

    static Function<Flux<Person>, Flux<Person>> applyFilterMap(){
        return flux -> flux.filter(p->p.getAge()>15)
                .doOnNext(p->p.setName(p.getName().toUpperCase()))
                .doOnDiscard(Person.class,p-> System.out.println("person not allowed age is :" +p.getAge()));
    }

    /*default Empty */
    static void operatorEmpty(){
        Flux.range(1,10)
                .filter(i->i>10)
//                .defaultIfEmpty(-100)
                .switchIfEmpty(fallback2())
                .subscribe(Util.subscriber());
        Util.sleepSecond(7);

    }


    /*timeout
    * for highly resilient streams
    * */
    static void operatorTimeout(){
        orderNumber()
                .timeout(Duration.ofSeconds(2),fallback2()).subscribe(Util.subscriber());
        Util.sleepSecond(5);
    }

    static Flux<Integer> orderNumber(){
        return Flux.range(1,10).delayElements(Duration.ofSeconds(5));
    }

    static Flux<Integer> fallback2(){
        return Flux.range(100,10).delayElements(Duration.ofMillis(200));
    }


    /*On Error resilient Pipeline*/
    static void operatorOnError(){
        Flux.range(1,10).log().map(i-> 10/(i-5))
//                .onErrorReturn(1)
//                .onErrorResume(e->fallback())
                .onErrorContinue((err,obj)->{
                    System.out.println("error object: "+obj);
                })
                .subscribe(Util.subscriber());

    }

    static Mono<Integer> fallback(){
        return Mono.fromSupplier(()-> Util.faker().random().nextInt(100,200));
    }

    /*delay
    * limit of this operator is 32 */
    static void operatorDelay(){
        Flux.range(1,100).log().delayElements(Duration.ofSeconds(1)).subscribe(Util.subscriber());
        Util.sleepSecond(60);
    }

    /*limit rate
    * default limit 75 %
    * */
    static void operatorLimitRate(){
        Flux.range(1,1000)
                .log()
                //when low tide ==0 then all high tide consume first and when low tide == high tide then work as default 75%
                .limitRate(100,0)
                .subscribe(Util.subscriber());
    }



    /*doOn methods
    * multiple same methods run in particular manner
    * first flow from bottom to top
    * onSubscribe run top to bottom
    * */

    static void operatorDoOn(){
        Flux.create(fluxSink -> {
                    System.out.println("inside fluxSink");
            IntStream.range(0,5).forEach(fluxSink::next);
//            fluxSink.error(new RuntimeException("oops"));
            fluxSink.complete();
//            System.out.println("Flux Sink Completed");
        })
                //always run first
                .doFirst(()-> System.out.println("doFirst 1"))
                .doOnNext(item-> System.out.println("doOnNext: "+item))
                .doOnSubscribe(subscription -> System.out.println("doOnSubscribe 1: "+subscription))
                .doOnCancel(()-> System.out.println("doOnCancel"))
                .doFirst(()-> System.out.println("doFirst 2"))
                .doOnComplete(()-> System.out.println("doOnComplete"))
                .doOnSubscribe(subscription -> System.out.println("doOnSubscribe 2: "+subscription))
                //when item discarded <in case of take subs cancel but we pushing value>
                .doOnDiscard(Object.class,o-> System.out.println("doOnDiscard "+o))
                .doOnError(err-> System.out.println("doOnError "+err.getMessage()))
                .doOnRequest(value -> System.out.println("doOnRequest "+value))
                //run on error and when stream is complete but not in cancel state
                .doOnTerminate(()-> System.out.println("doOnTerminate"))
                .doFirst(()-> System.out.println("doFirst 3"))
                //always run at last after downstream receive signal
                .doFinally(signalType -> System.out.println("doFinally "+signalType))
//                .take(2)
                .subscribe(Util.subscriber());
    }

    /*Handle behave like filter+map
    * we can call complete to stop stream on some condition*/
    static void operatorHandle(){
        Flux.range(1,20)
                .handle((integer, synchronousSink) -> {
                    if(integer <5){
                        synchronousSink.next(integer);
                    }else if(integer <15){
                        //filtering
                        System.out.println("filtering :" +integer);

                    }else{
                        //Map
                        synchronousSink.next(integer*2);
                    }
                })
                .subscribe(Util.subscriber());
    }

}

