package ar.edu.itba.paw.webapp.filter;

import org.springframework.context.i18n.LocaleContextHolder;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.Locale;

/**
 * https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Language
 * https://stackoverflow.com/questions/27578134/clientrequestfilter-vs-containerrequestfilter
 * https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/i18n/LocaleContextHolder.html
 */

@Provider
public class AcceptLanguageFilter implements ContainerRequestFilter {

    private static final String ES_LOCALE = "es";
    private static final String EN_LOCALE = "en";
    private static final String ANY_LANGUAGE = "*";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        final List<Locale> acceptableLanguages = requestContext.getAcceptableLanguages();

        final Locale selectedOrDefaultLocale = acceptableLanguages.stream()
                .filter(locale -> {
                    String language = locale.getLanguage().toLowerCase();
                    return language.equals(ES_LOCALE) || language.equals(EN_LOCALE) || language.equals(ANY_LANGUAGE);
                })
                .findFirst()
                .orElseGet(() -> new Locale(EN_LOCALE));

        // Associate the given Locale with the current thread
        LocaleContextHolder.setLocale(selectedOrDefaultLocale);
    }
}

