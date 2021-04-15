package reactive.spike.service;

import com.google.common.collect.Iterables;
import io.micronaut.scheduling.TaskExecutors;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import reactive.spike.client.HelloClient;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Singleton
public class HelloService {
    private final HelloClient helloClient;
    private final ExecutorService executorService;

    @Inject
    public HelloService(final HelloClient helloClient, @Named(TaskExecutors.IO) final ExecutorService executorService) {
        this.helloClient = helloClient;
        this.executorService = executorService;
    }

    /**
     * Futures
     */
    public CompletableFuture<Stream<String>> getHelloFuture() {
        return getHelloFuture2(getIds())
            .thenApply(helloList -> helloList.stream().map(string -> String.format("Hello %s", string)));
    }

    private CompletableFuture<List<String>> getHelloFuture2(final Set<Integer> ids) {
        final Set<CompletableFuture<String>> futures = StreamSupport.stream(Iterables.partition(ids, 10).spliterator(), false)
            .map(id -> CompletableFuture.supplyAsync(this.helloClient::getHelloFuture, executorService))
            .collect(Collectors.toSet());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(future -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList())
            );
    }


    /**
     * Reactive
     */
    public Observable<String> getHelloReactive() {
        return getHelloReactive2(getIds())
            .map(string -> String.format("Hello %s", string));
    }

    private Observable<String> getHelloReactive2(final Set<Integer> ids) {
        return Observable.fromIterable(ids)
            .subscribeOn(Schedulers.from(executorService))
            .buffer(10)
            .flatMap(a -> this.helloClient.getHelloReactive().toObservable());
    }

    private Set<Integer> getIds() {
        return IntStream.rangeClosed(0, 99)
            .boxed()
            .collect(Collectors.toSet());
    }
}
