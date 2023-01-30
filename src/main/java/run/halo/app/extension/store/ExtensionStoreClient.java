package run.halo.app.extension.store;

import java.util.List;
import java.util.Optional;

/**
 * An interface to query and operate ExtensionStore.
 *
 * @author johnniang
 */
public interface ExtensionStoreClient {

    /**
     * Lists all ExtensionStores by name prefix.
     *
     * @param prefix is the prefix of ExtensionStore name.
     * @return all ExtensionStores which names start with the prefix.
     */
    List<ExtensionStore> listByNamePrefix(String prefix);

    /**
     * Fetches an ExtensionStore by unique name.
     *
     * @param name is the full name of an ExtensionStore.
     * @return an optional ExtensionStore.
     */
    Optional<ExtensionStore> fetchByName(String name);

    /**
     * Creates an ExtensionStore.
     *
     * @param name is the full name of an ExtensionStore.
     * @param data is Extension body to be persisted.
     * @return a fresh ExtensionStore created just now.
     */
    ExtensionStore create(ExtensionStore store);

    /**
     * Updates an ExtensionStore with version to prevent concurrent update.
     *
     * @param name is the full name of an ExtensionStore.
     * @param version is the expected version of ExtensionStore.
     * @param data is Extension body to be updated.
     * @return updated ExtensionStore with a fresh version.
     */
    ExtensionStore update(ExtensionStore store);

    /**
     * Deletes an ExtensionStore by name and current version.
     *
     * @param name is the full name of an ExtensionStore.
     * @param version is the expected version of ExtensionStore.
     * @return previous ExtensionStore.
     */
    ExtensionStore delete(ExtensionStore store);

    //TODO add watch method here.
}
