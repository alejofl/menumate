package ar.edu.itba.paw.exception.base;

/**
 * https://medium.com/@satyendra.jaiswal/demystifying-exception-mappers-in-jax-rs-handling-errors-with-grace-b1a038642829
 * https://www.baeldung.com/java-exception-handling-jersey
 * https://www.mastertheboss.com/jboss-frameworks/resteasy/how-to-handle-exceptions-in-jax-rs-applications/
 */
public interface CustomBaseException {

    int getStatusCode();
    String getExceptionMessage();
}
