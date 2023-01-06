package explorer;

import Utility.Util;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;


public class ContextDemo {
    public static void main(String[] args) {
//        context();
        ctxRateLimiting();
    }
    static Supplier<Mono<String>> getWelcomeMessage= ()-> Mono.deferContextual(contextView -> {
        if(contextView.hasKey("user")) return Mono.just("Welcome "+contextView.get("user"));
        else return Mono.error(new RuntimeException("unauthenticated user"));
    }) ;

    static void context(){
        getWelcomeMessage.get()
                .contextWrite(ctx->ctx.put("user","changed".toUpperCase()))
//                .contextWrite(Context.of("user","jake"))
                .contextWrite(Context.of("user","sam"))
                .subscribe(Util.subscriber());
    }

    static void ctxRateLimiting(){
        final Map<String, String> USERMAP = Map.of("sam", "std",
            "mike", "prime");
        Map<String, Integer> BOOKMAP = new HashMap<>();
        BOOKMAP.put("std",2);
        BOOKMAP.put("prime", 3);

        Function<Context,Context> userCategoryContext = ctx ->{
            String category = USERMAP.get(ctx.get("user").toString());
            return ctx.put("category",category);
        };

        Function<Context,Context> rateLimitingContext = ctx ->{
            if(ctx.hasKey("category")){
                Integer attempts = BOOKMAP.get(ctx.get("category").toString());
                String category = ctx.get("category").toString();

                if(attempts>0){
                    BOOKMAP.put(category,attempts-1);
                     return ctx.put("allow",true);
                }
            }
            return ctx.put("allow",false);

        };

        Mono<String> mono = Mono.deferContextual(ctx -> {
            if (ctx.get("allow")) {
                return Mono.just(Util.faker().book().title());
            } else {
                return Mono.error(new RuntimeException("Not Allowed"));
            }
        }).contextWrite(rateLimitingContext);

        mono.contextWrite(userCategoryContext)
                .contextWrite(Context.of("user","sam"))
                .repeat(2)
                .subscribe(Util.subscriber());


    }
}
