package run.halo.app.extension.router.selector;

import java.util.function.Predicate;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import run.halo.app.extension.Extension;

public class FieldCriteriaPredicateConverter<E extends Extension>
    implements Converter<SelectorCriteria, Predicate<E>> {

    @Override
    @NonNull
    public Predicate<E> convert(SelectorCriteria criteria) {
        // current we only support name field.
        return ext -> {
            String value = null;
            boolean hint = false;
            if ("name".equals(criteria.key())) {
                value = ext.getMetadata().getName();

                hint = true;
            } else if ("type".equals(criteria.key())) {
                value = ext.getMetadata().getType();
                hint = true;
            }
            if (!hint || value == null) {
                return false;
            }
            switch (criteria.operator()) {
                case Equals, IN -> {
                    return criteria.values().contains(value);
                }
                case NotEquals -> {
                    return !criteria.values().contains(value);
                }
                default -> {
                    return false;
                }
            }
        };
    }
}
