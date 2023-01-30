package run.halo.app.extension.store;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * An implementation of ExtensionStoreClient using JPA.
 *
 * @author johnniang
 */
@Service
public class ExtensionStoreClientJPAImpl implements ExtensionStoreClient {

    private final ReactiveExtensionStoreClient client;

    public ExtensionStoreClientJPAImpl(ReactiveExtensionStoreClient client) {
        this.client = client;
    }

    @Override
    public List<ExtensionStore> listByNamePrefix(String prefix) {
        return client.listByNamePrefix(prefix).collectList().block();
    }

    @Override
    public Optional<ExtensionStore> fetchByName(String name) {
        return client.fetchByName(name).blockOptional();
    }

    @Override
    public ExtensionStore create(ExtensionStore store) {
        return client.create(store).block();
    }

    @Override
    public ExtensionStore update(ExtensionStore store) {

        return client.update(store).block();
    }

    @Override
    public ExtensionStore delete(ExtensionStore store) {
        return  client.delete(store).block();
    }

}
