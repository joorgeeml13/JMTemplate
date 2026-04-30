package base.template.application.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException{
    private final String messageKey;
    private final Object[] args;
    public ApplicationException(String messageKey, Object... args){
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
    }
}
