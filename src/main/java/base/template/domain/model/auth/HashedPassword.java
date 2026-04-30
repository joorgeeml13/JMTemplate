package base.template.domain.model.auth;

import lombok.Getter;

@Getter
public class HashedPassword {
    private final String value;

    public HashedPassword(String value) {
        if (value == null || value.isBlank()) 
            throw new IllegalArgumentException("error.account.empty.hash"); 
        this.value = value;
    }
}
