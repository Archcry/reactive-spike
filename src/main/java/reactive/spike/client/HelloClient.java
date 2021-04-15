package reactive.spike.client;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Single;

@Client("http://localhost:3000")
public interface HelloClient {
    default String getHelloFuture() {
        System.out.println(Thread.currentThread().getName());

        return this.getHelloFutureImpl();
    }

    @Get("/hello")
    String getHelloFutureImpl();

    default Single<String> getHelloReactive() {
        System.out.println(Thread.currentThread().getName());

        return this.getHelloReactiveImpl();
    }

    @Get("/hello")
    Single<String> getHelloReactiveImpl();
}
