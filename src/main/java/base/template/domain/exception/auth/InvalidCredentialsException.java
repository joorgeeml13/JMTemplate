package base.template.domain.exception.auth;

import base.template.domain.exception.DomainException;

public class InvalidCredentialsException extends DomainException{
    public InvalidCredentialsException() {
        super("error.auth.invalid_credentials"); 
    }
}
