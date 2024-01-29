package ar.edu.itba.paw.webapp.filter;

import org.springframework.context.i18n.LocaleContextHolder;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * https://www.javaguides.net/2018/06/jax-rs-filters-and-interceptors.html
 * https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/filters-and-interceptors.html
 */

@Provider
public class ContentLanguageFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().add(HttpHeaders.CONTENT_LANGUAGE, LocaleContextHolder.getLocale().toLanguageTag());
    }
}
