package se.jsquad.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import se.jsquad.exception.ClientNotFoundException;
import se.jsquad.exception.IllegalPersonIdentificationNumberException;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    public RestResponseEntityExceptionHandler() {
        super();
    }

    @ExceptionHandler({IllegalPersonIdentificationNumberException.class})
    public ResponseEntity<Object> handleBadRequest(final RuntimeException runtimeException, final WebRequest webRequest) {
        return handleExceptionInternal(runtimeException, runtimeException.getMessage(), new HttpHeaders(),
                HttpStatus.BAD_REQUEST, webRequest);
    }

    @ExceptionHandler({ClientNotFoundException.class})
    public ResponseEntity<Object> handleNotFound(final RuntimeException runtimeException, final WebRequest webRequest) {
        return handleExceptionInternal(runtimeException, runtimeException.getMessage(), new HttpHeaders(),
                HttpStatus.NOT_FOUND, webRequest);
    }

    @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Object> handleInternal(final RuntimeException runtimeException, final WebRequest webRequest) {
        return getObjectResponseEntity(runtimeException, webRequest);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleGlobalException(final RuntimeException runtimeException, final WebRequest webRequest) {
        return getObjectResponseEntity(runtimeException, webRequest);
    }

    private ResponseEntity<Object> getObjectResponseEntity(RuntimeException runtimeException, WebRequest webRequest) {
        logger.error(runtimeException.getMessage(), runtimeException);
        return handleExceptionInternal(runtimeException, runtimeException.getMessage(), new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR, webRequest);
    }
}