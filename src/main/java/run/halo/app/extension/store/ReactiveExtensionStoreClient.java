package run.halo.app.extension.store;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveExtensionStoreClient {

    Flux<ExtensionStore> listByNamePrefix(String prefix);

    Mono<ExtensionStore> fetchByName(String name);

    Mono<ExtensionStore> create(ExtensionStore store);

    Mono<ExtensionStore> update(ExtensionStore store);

    Mono<ExtensionStore> delete(ExtensionStore store);

}
