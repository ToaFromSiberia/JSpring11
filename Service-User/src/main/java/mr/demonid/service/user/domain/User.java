package mr.demonid.service.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
//public class User implements UserDetails {
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;

    @JsonManagedReference
//    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "userParen", cascade = CascadeType.ALL)
    private Set<Account> accounts = new HashSet<>();     // счета


    @JsonIgnore
    public Collection<String> getAuthorities() {
        return roles.stream()
                .map(Role::toString)
                .toList();
    }

    /**
     * Возвращает платежный аккаунт.
     */
    public Account getPaymentAccount() {
        if (accounts == null) {
            accounts = new HashSet<>();
        }
        if (accounts.isEmpty()) {
            return null;
        }
        return accounts.stream().findFirst().orElse(new Account()); // пока это будет первый в списке (множестве)
    }

    /**
     * Добавления счёта пользователю, с учётом связей в БД.
     * @param account Новый счет.
     */
    public void addAccount(Account account) {
        account.setUserParen(this);
        accounts.add(account);
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
        account.setUserParen(this);
    }

    public void addRole(Role role) {
        roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(Role role) {
        roles.remove(role);
        role.getUsers().remove(this);
    }


    @JsonIgnore
    public String getRolesList() {
        return roles.stream().map(Role::getName).toList().toString();
    }

    @JsonIgnore
    public String getAccountsList() {
        return accounts.stream().map(e -> "(" + e.getName() + ": " + e.getAmount() + " уе)").toList().toString();
    }

    @JsonIgnore
    public String getAccountsNameList() {
        return accounts.stream().map(Account::getName).toList().toString();
    }

    @JsonIgnore
    public String getAccountsAmountList() {
        return accounts.stream().map(Account::getAmount).toList().toString();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", accounts=" + getAccountsList() +
                ", roles=" + getRolesList() +
                '}';
    }
}
