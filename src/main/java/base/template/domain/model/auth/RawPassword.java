package base.template.domain.model.auth;

import base.template.domain.exception.auth.InvalidAccountDataException;
import lombok.Getter;

@Getter
public class RawPassword {
    private final String value;

    public RawPassword(String value) {
        if (value == null || value.length() < 8) 
            throw new InvalidAccountDataException("error.account.invalid.password.length");
        
        this.value = value;
    }
}
