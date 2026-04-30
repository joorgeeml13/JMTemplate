package base.template.domain.service;

import base.template.domain.model.auth.HashedPassword;
import base.template.domain.model.auth.RawPassword;

public interface PasswordHasher {
    HashedPassword hash(RawPassword rawPassword);
    boolean matches(RawPassword rawPassword, HashedPassword hashedPassword);
}
