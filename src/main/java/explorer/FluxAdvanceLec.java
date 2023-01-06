package explorer;

import Utility.Util;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;
import java.util.stream.IntStream;

public class FluxAdvanceLec {
    public static void main(String[] args) {
//         fluxCreate();
//        fluxSinkRefactor();
//        multiThreadFluxSink();
//        fluxTake();
//        fluxGenerate();
//        fluxGenCounter();
        fluxPush();

    }

    /*flux create use for generating value programmatically*/
    /*fixing cancel request*/
    static void fluxCreate(){
        Flux.create(fluxSink -> {
            String country;
            do {
                country = Util.faker().country().name();
                System.out.println("emitting: "+ country);
                fluxSink.next(country);

            }while (!country.equalsIgnoreCase("canada") && !fluxSink.isCancelled());
            fluxSink.complete();
        }).take(3).subscribe(Util.subscriber("country"));
    }

    /*FluxSink  refactored way*/
    static void fluxSinkRefactor(){
        NameProducer nameProducer = new NameProducer();
        Flux.create(nameProducer).subscribe(Util.subscriber());
        nameProducer.produce();
    }
    /*flux sink in muti threaded context*/
    static void multiThreadFluxSink(){
        NameProducer nameProducer = new NameProducer();
        Flux.create(nameProducer).subscribe(Util.subscriber());
        Runnable runnable = nameProducer::produce;
        IntStream.range(1,10).forEach(i->new Thread(runnable).start());
    }

    /*take() operator  limit the item by cancel the subscription and send complete signal to downstream*/

    static void fluxTake(){
        Flux.range(1,10).log().take(3).log().subscribe(Util.subscriber());
    }

    /*Flux generate  emit item continuously OR call defined on Next method again and again
    * for stopping you can call onComplete or on Error*/
    static void fluxGenerate(){
        Flux.generate(synchronousSink -> {
            synchronousSink.next(Util.faker().country().name());
        }
        ).take(2)
                .subscribe(Util.subscriber());

    }

    /*flux generate with
    * downstream subs also working fine like take() */
    static void fluxGenCounter(){
        Flux.generate(
                ()->1,
                (state,sink)->{
                    String country = Util.faker().country().name();
                    sink.next(country);
                    if(state>=10 || country.toLowerCase().equals("canada")){
                        sink.complete();
                    }
                    return state+1;
                }
                ).take(5).subscribe(Util.subscriber());
    }
    /* flux push
    * not thread safe
    * create is thread safe using serialized sink
    * */

    static void fluxPush(){
        NameProducer nameProducer = new NameProducer();
        Flux.push(nameProducer).subscribe(Util.subscriber());
        Runnable runnable = nameProducer::produce;
        IntStream.range(1,10).forEach(i->new Thread(runnable).start());
    }

    static class NameProducer implements Consumer<FluxSink<String>> {
        private FluxSink<String> fluxSink;
        @Override
        public void accept(FluxSink<String> stringFluxSink) {
            this.fluxSink =stringFluxSink;
        }
        public void produce(){
            this.fluxSink.next( Thread.currentThread().getName()+" : "+ Util.faker().name().fullName());
        }
    }
}
