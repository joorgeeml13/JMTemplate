package base.template.infrastructure.config;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import base.template.application.exceptions.InvalidCredentialsException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        String messageKey = ex.getMessage();
        String localizedMessage = messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("status", 400);
        errorBody.put("error", "Bad Request");
        errorBody.put("message", localizedMessage);

        return ResponseEntity.badRequest().body(errorBody);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        String messageKey = ex.getMessage();
        String localizedMessage = messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("status", 401);
        errorBody.put("error", "Unauthorized");
        errorBody.put("message", localizedMessage);

        return ResponseEntity.status(401).body(errorBody);
    }
}
