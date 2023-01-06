package reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class MonoTest {

    @Test
    public void monoSubscriber(){
        Mono<String> mono = Mono.just("Data for MONO").log();
        mono.subscribe();
        log.info("=====");
        StepVerifier.create(mono).expectNext("Data for MONO").verifyComplete();

    }

}
