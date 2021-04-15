package reactive.spike.endpoint;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.reactivex.Single;
import reactive.spike.service.HelloService;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Controller("/hello")
public class HelloEndpoint {
    private final HelloService helloService;

    @Inject
    public HelloEndpoint(final HelloService helloService) {
        this.helloService = helloService;
    }

    @Get("future")
    public CompletableFuture<Stream<String>> getHelloFuture() {
        return helloService.getHelloFuture();
    }

    @Get("reactive")
    public Single<List<String>> getHelloReactive() {
        return helloService.getHelloReactive()
            .toList();
    }
}
