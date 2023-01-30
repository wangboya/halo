package run.halo.app.extension.store;

import jakarta.persistence.Lob;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;
import run.halo.app.extension.Extension;

/**
 * ExtensionStore is an entity for storing Extension data into database.
 *
 * @author johnniang
 */
@Data
public class ExtensionStore {

    /**
     * Extension store name, which is globally unique.
     * We will use it to query Extensions by using left-like query clause.
     */

    private String name;

    /**
     * Exactly Extension body, which might be base64 format.
     */

    private Extension data;

    /**
     * This field only for serving optimistic lock value.
     */

    private Long version;

    public ExtensionStore() {
    }

    public ExtensionStore(String name, Extension data) {
        this.name = name;
        this.data = data;
    }

    public ExtensionStore(String name, Long version) {
        this.name = name;
        this.version = version;
    }

    public ExtensionStore(String name, Extension data, Long version) {
        this.name = name;
        this.data = data;
        this.version = version;
    }
}
