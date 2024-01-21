package ar.edu.itba.paw.webapp.mapper;

import org.glassfish.jersey.server.validation.ValidationConfig;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.util.Locale;

/***
 * https://download.oracle.com/otn-pub/jcp/bean_validation-1.0-fr-oth-JSpec/bean_validation-1_0-final-spec.pdf?AuthParam=1705788017_fc7d0b2abadbea3d1c028972942036d3
 * https://www.baeldung.com/spring-validation-message-interpolation
 * https://www.cwiki.us/display/JERSEYEN/Configuring+Bean+Validation+Support
 */

@Provider
public class ValidationContextResolverConfig implements ContextResolver<ValidationConfig> {

    @Override
    public ValidationConfig getContext(Class<?> type) {
        final ValidationConfig config = new ValidationConfig();
        config.messageInterpolator(new LocaleContextHolderMessageInterpolator());
        return config;
    }

    private static class LocaleContextHolderMessageInterpolator implements MessageInterpolator {

        private final MessageInterpolator defaultInterpolator;

        public LocaleContextHolderMessageInterpolator() {
            defaultInterpolator = Validation.byDefaultProvider().configure().getDefaultMessageInterpolator();
        }

        @Override
        public String interpolate(String messageTemplate, Context context) {
            return defaultInterpolator.interpolate(messageTemplate, context, LocaleContextHolder.getLocale());
        }

        @Override
        public String interpolate(String messageTemplate, Context context, Locale locale) {
            return defaultInterpolator.interpolate(messageTemplate, context, LocaleContextHolder.getLocale());
        }
    }
}
