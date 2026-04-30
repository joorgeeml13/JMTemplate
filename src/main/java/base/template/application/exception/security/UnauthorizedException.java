package base.template.application.exception.security;

import base.template.application.exception.ApplicationException;

public class UnauthorizedException extends ApplicationException{

    public UnauthorizedException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
