package base.template.domain.model.auth;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import base.template.domain.DomainConstants;
import base.template.domain.event.DomainEvent;
import base.template.domain.event.auth.AccountPasswordChangedEvent;
import base.template.domain.event.auth.AccountRoleAddedEvent;
import base.template.domain.event.auth.AccountRoleRemovedEvent;
import base.template.domain.exception.auth.InvalidAccountDataException;
import lombok.Getter;

public class Account {
    @Getter private final UUID id;
    @Getter private final Email email;
    @Getter private HashedPassword password;
    @Getter private AccountStatus status;
    @Getter private final Instant createdAt;

    private List<Role> roles;

    private final transient List<DomainEvent> domainEvents = new ArrayList<>();

    private Account(UUID id, Email email, HashedPassword password, AccountStatus status, Instant createdAt, List<Role> roles) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.status = status;
        this.createdAt = createdAt;
        this.roles = roles;
    }

    //Factory method
    public static Account create(Email email, HashedPassword password) {
       List<Role> roles = new ArrayList<>();
       roles.add(DomainConstants.DEFAULT_ROLE);
        return new Account(UUID.randomUUID(), email, password, AccountStatus.ACTIVE, Instant.now(), roles);
    }

    //Reconstruct entity
    public static Account reconstruct(UUID id, Email email, HashedPassword password, AccountStatus status, Instant createdAt, List<Role> roles){
        return new Account(id, email, password, status, createdAt, roles);
    }

    public List<Role> getRoles() {
        return Collections.unmodifiableList(this.roles);
    }
    
    //ChangePassword
    public void changePassword(HashedPassword newPassword){
        if(newPassword == null) throw new InvalidAccountDataException("error.account.password.null");
        if (this.password.getValue().equals(newPassword.getValue())) throw new InvalidAccountDataException("error.account.password.same_as_old");

        this.password = newPassword;
        
        this.domainEvents.add(new AccountPasswordChangedEvent(id));
    }

    //Gestion roles
    public void addRole(Role role){
        if(role == null) throw new InvalidAccountDataException("error.account.role.null");
        if (this.roles.contains(role)) throw new InvalidAccountDataException("error.account.role.duplicated", role.name());
        this.roles.add(role);

        this.domainEvents.add(new AccountRoleAddedEvent(this.id, role));
    }

    public void removeRole(Role role) {
        if (!this.roles.contains(role)) throw new InvalidAccountDataException("error.account.role.not_found");
        if (this.roles.size() == 1) throw new InvalidAccountDataException("error.account.role.minimum_required");
        
        this.roles.remove(role);

        this.domainEvents.add(new AccountRoleRemovedEvent(this.id, role));
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = List.copyOf(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }

    //Change status
    public void changeStatus(AccountStatus newStatus) {
        if (newStatus == null) throw new InvalidAccountDataException("error.account.status.null");
        if (this.status == newStatus) throw new InvalidAccountDataException("error.account.status.same_as_current");

        this.status = newStatus;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Account account = (Account) obj;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}