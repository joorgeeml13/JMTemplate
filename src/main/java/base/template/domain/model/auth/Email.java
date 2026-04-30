package base.template.domain.model.auth;

import base.template.domain.exception.auth.InvalidAccountDataException;
import lombok.Getter;

@Getter
public class Email {
    private final String value;

    public Email(String value){
        if(value == null || !value.contains("@")) throw new InvalidAccountDataException("error.account.invalid.email");

        this.value = value;
    }
}