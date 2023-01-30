package run.halo.app.extension.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.model.v3.OAI3Context;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;
import org.openapi4j.schema.validator.v3.SchemaValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import run.halo.app.extension.Extension;
import run.halo.app.extension.ExtensionUtil;
import run.halo.app.extension.Scheme;
import run.halo.app.extension.SchemeManager;
import run.halo.app.extension.exception.ExtensionConvertException;
import run.halo.app.extension.exception.SchemaViolationException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

import static org.openapi4j.core.validation.ValidationSeverity.ERROR;
import static org.springframework.util.StringUtils.arrayToCommaDelimitedString;

@Component
@Slf4j
public class ExtensionStoreCodec {
    private final SchemeManager schemeManager;
    private final ObjectMapper objectMapper;

    public ExtensionStoreCodec(SchemeManager schemeManager, ObjectMapper objectMapper) {
        this.schemeManager = schemeManager;
        this.objectMapper = objectMapper;

    }

    public ExtensionStore cast(ExtensionStoreEntity entity) {
        Scheme scheme = schemeManager.schemes().stream().filter(
            sch -> {
                var prefix = ExtensionUtil.buildStoreNamePrefix(sch);
                return entity.getName().startsWith(prefix);
            }
        ).findFirst().orElseThrow();
        ExtensionStore store = new ExtensionStore();
        store.setName(entity.getName());
        store.setVersion(entity.getVersion());
        try {
            store.setData(objectMapper.readValue(entity.getData(), scheme.type()));
            if (!store.getVersion()
                .equals(store.getData().getMetadata().getVersion())) {
                // log.warn("overwrite version {}->{} : {}", store.getVersion(),
                //     store.getData().getMetadata().getVersion(), store.getName());
                store.getData().getMetadata().setVersion(store.getVersion());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        store.setName(entity.getName());
        store.setVersion(entity.getVersion());
        return store;


    }

    public ExtensionStoreEntity cast(ExtensionStore store) {
        Extension extension = store.getData();
        try {
            var data = objectMapper.writeValueAsBytes(extension);


            return new ExtensionStoreEntity(store.getName(), data, store.getVersion());
        } catch (IOException e) {
            throw new ExtensionConvertException("Failed write Extension as bytes", e);
        }
    }


}
