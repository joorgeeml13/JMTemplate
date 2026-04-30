package base.template.domain.exception;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException{
    private final String messageKey;
    private final Object[] args;
    public DomainException(String messageKey, Object... args){
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
    }
}
