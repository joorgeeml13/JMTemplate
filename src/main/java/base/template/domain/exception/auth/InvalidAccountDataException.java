package base.template.domain.exception.auth;

import base.template.domain.exception.DomainException;

public class InvalidAccountDataException extends DomainException{

    public InvalidAccountDataException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
