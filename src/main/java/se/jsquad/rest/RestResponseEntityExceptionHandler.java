package se.jsquad.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import se.jsquad.exception.ClientNotFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Iterator;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    public RestResponseEntityExceptionHandler() {
        super();
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleBadRequest(final RuntimeException runtimeException,
                                                   final WebRequest webRequest) {
        String message;

        if (runtimeException instanceof ConstraintViolationException) {
            StringBuilder stringBuilder = new StringBuilder();
            Iterator<ConstraintViolation<?>> iterator =
                    ((ConstraintViolationException) runtimeException).getConstraintViolations().iterator();

            while (iterator.hasNext()) {
                stringBuilder.append(iterator.next().getMessage()).append(" ");
            }

            message = stringBuilder.toString().trim();
        } else {
            message = runtimeException.getMessage();
        }

        return handleExceptionInternal(runtimeException, message, new HttpHeaders(),
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
    public ResponseEntity<Object> handleGlobalException(final RuntimeException runtimeException,
                                                        final WebRequest webRequest) {
        return getObjectResponseEntity(runtimeException, webRequest);
    }

    private ResponseEntity<Object> getObjectResponseEntity(RuntimeException runtimeException, WebRequest webRequest) {
        logger.error(runtimeException.getMessage(), runtimeException);
        return handleExceptionInternal(runtimeException, runtimeException.getMessage(), new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR, webRequest);
    }
}