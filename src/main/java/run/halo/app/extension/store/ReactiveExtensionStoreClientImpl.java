package run.halo.app.extension.store;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.Extension;
import run.halo.app.extension.SchemeManager;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReactiveExtensionStoreClientImpl implements ReactiveExtensionStoreClient ,
    ApplicationRunner {

    private final ExtensionStoreEntityRepository repository;
    private final ExtensionStoreCodec codec;

    public ReactiveExtensionStoreClientImpl(ExtensionStoreEntityRepository repository,
        ExtensionStoreCodec codec) {
        this.repository = repository;
        this.codec = codec;


    }

    private boolean needInit = true;

    public void init() {
        if (needInit) {
            synchronized (ReactiveExtensionStoreClientImpl.class) {
                if (needInit) {

                    long nano = System.nanoTime();
                    AtomicInteger idx = new AtomicInteger();
                    repository.findAll().map(entity -> codec.cast(entity))
                        .doOnNext(

                            entity -> {
                                idx.incrementAndGet();
                                cache.put(entity.getName(), entity);

                            }
                        ).blockLast();
                    nano = System.nanoTime() - nano;
                    log.info("加载所有资源完成,共{}条，耗时{}毫秒", idx.get(), nano / 1000_000.0);
                    needInit = false;
                }
            }
        }
    }

    Map<String, ExtensionStore> cache = new ConcurrentHashMap<>();

    @Override
    public Flux<ExtensionStore> listByNamePrefix(String prefix) {

        long nano = System.nanoTime();
        log.trace("listByNamePrefix={}", prefix);
        return Flux.fromArray(cache.values().toArray(new ExtensionStore[cache.values().size()]))
            .filter(
                store ->
                    store.getName().startsWith(prefix)
            ).doOnComplete(
                () -> {
                    log.trace("listByNamePrefix={},cost={}ns", prefix, System.nanoTime() - nano);
                }
            );
        // return repository.findAllByNameStartingWith(prefix);
    }

    @Override
    public Mono<ExtensionStore> fetchByName(String name) {

        long nano = System.nanoTime();
        log.trace("fetch={}", name);
        ExtensionStore store = cache.get(name);
        if (store == null) {
            log.trace("fetch.miss={}", name);
            return repository.findById(name)
                .map(
                    entity -> {
                        if (entity == null) {
                            return null;
                        } else {
                            ExtensionStore extensionStore = codec.cast(entity);
                            cache.put(extensionStore.getName(), extensionStore);
                            log.trace("fetch={},{},cost={}ns", extensionStore.getName(),
                                extensionStore.getVersion(), System.nanoTime() - nano);
                            return extensionStore;
                        }
                    }
                );
        } else {
            log.trace("fetch={},{},cost={}ns", store.getName(), store.getVersion(),
                System.nanoTime() - nano);
            return Mono.just(store);
        }
    }

    @Override
    public Mono<ExtensionStore> create(ExtensionStore store) {

        long nano = System.nanoTime();
        log.trace("create={}", store.getName());
        ExtensionStore fStore = new ExtensionStore(store.getName(), store.getData());
        ExtensionStoreEntity entity = codec.cast(fStore);
        return repository.save(entity)
            .map(
                e -> {
                    if (e != null) {
                        fStore.setVersion(e.getVersion());
                        fStore.getData().getMetadata().setVersion(e.getVersion());
                        cache.put(e.getName(), fStore);
                        log.trace("create={},cost={}ns", fStore.getName(),
                            System.nanoTime() - nano);
                        return fStore;
                    } else {
                        return null;
                    }
                }
            );

    }

    @Override
    public Mono<ExtensionStore> update(ExtensionStore store) {

        long nano = System.nanoTime();
        ExtensionStore fStore = new ExtensionStore(store.getName(), store.getData(),
            store.getVersion());

        ExtensionStoreEntity entity = codec.cast(fStore);

        log.trace("update={},{}", fStore.getName(), fStore.getVersion());
        return repository.save(entity)
            .map(
                e -> {
                    if (e != null) {
                        log.trace("update={},{}->{},cost={}ns", fStore.getName(),
                            fStore.getVersion(), e.getVersion(), System.nanoTime() - nano);
                        fStore.setVersion(e.getVersion());
                        fStore.getData().getMetadata().setVersion(e.getVersion());
                        cache.put(e.getName(), fStore);
                        return fStore;
                    } else {
                        return null;
                    }
                }
            );
    }

    @Override
    @Transactional
    public Mono<ExtensionStore> delete(ExtensionStore store) {
        long nano = System.nanoTime();
        log.trace("delete={},{}", store.getName(), store.getVersion());
        return repository.findById(store.getName())
            .flatMap((ExtensionStoreEntity entity) -> {
                // reset the version
                entity.setVersion(store.getVersion());
                return repository.delete(entity).map(
                    v -> {
                        log.trace("delete={},{},cost={}ns", store.getName(),
                            store.getVersion(), System.nanoTime() - nano);

                        return cache.remove(store.getName());
                    }
                );
            });
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        init();
    }
}
