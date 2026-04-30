package base.template.infrastructure.config;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import base.template.domain.exception.DomainException;
import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionInterceptor {

    private final MessageSource messageSource;

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex){
        String message = messageSource.getMessage(
            ex.getMessageKey(),
            ex.getArgs(),
            LocaleContextHolder.getLocale());
        
            ErrorResponse response = ErrorResponse.create(ex, HttpStatus.UNPROCESSABLE_ENTITY, message);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusExceptio(ResponseStatusException ex){
        String message = messageSource.getMessage(
            ex.getMessage(),
            null,
            LocaleContextHolder.getLocale());

        ErrorResponse response = ErrorResponse.create(ex, HttpStatus.UNPROCESSABLE_ENTITY, message);

         return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }
}
