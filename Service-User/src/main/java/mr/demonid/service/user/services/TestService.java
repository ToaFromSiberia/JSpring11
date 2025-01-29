package mr.demonid.service.user.services;

import lombok.AllArgsConstructor;
import mr.demonid.service.user.domain.Account;
import mr.demonid.service.user.domain.Role;
import mr.demonid.service.user.domain.User;
import mr.demonid.service.user.dto.RegistrationRequest;
import mr.demonid.service.user.repository.AccountRepository;
import mr.demonid.service.user.repository.RoleRepository;
import mr.demonid.service.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;

@Service
@AllArgsConstructor
public class TestService {

    private static Random random = new Random();


    private UserRepository userRepository;
    private RoleService roleService;
    private RoleRepository roleRepository;
    private AccountRepository accountRepository;


    @Transactional
    public User registerUser(RegistrationRequest reques) throws Exception {
        if (reques.getUsername().isBlank() || reques.getPassword().isBlank() || reques.getEmail().isBlank()) {
            throw new Exception("Данные пользователя неверны!");
        }
        if (userRepository.existsByUsername(reques.getUsername())) {
            throw new Exception("Такой пользователь уже существует!");
        }
        User user = new User();
        user.setUsername(reques.getUsername());
        user.setPassword(reques.getPassword());
        user.setEmail(reques.getEmail());
        user.addRole(roleService.getRole("USER"));
        user.addAccount(createNewAccount());
        return userRepository.save(user);
    }

    @Transactional
    public void createRoles(String ... names) throws Exception {
        Arrays.stream(names).forEach(name -> {
            if (!name.startsWith("ROLE_")) {
                name = "ROLE_" + name;
            }
            if (!roleRepository.existsRoleByName(name)) {
                Role role = new Role();
                role.setName(name);
                role.setDescription("");
                role = roleRepository.save(role);
                System.out.println("-- add role '" + role.getName() + "'");
            }
        });
    }

    /**
     * Создаёт новый счёт, не привязанный ни к кому.
     */
    public Account createNewAccount() {
        Account account = new Account();
        // добавляем имя счета (карты, тут это не важно)
        do {
            Long nameStr = random.nextLong(100000000, 999999999 + 1);
            account.setName(String.format(
                    "%03d-%03d-%03d", nameStr / 1000000L, (nameStr % 1000000L) / 1000, nameStr % 1000));
        } while (accountRepository.existsAccountByName((account.getName())));

        // для примера закидываем денежек
        account.setAmount(BigDecimal.valueOf(1000));
        // и дату создания
        account.setCreation(LocalDate.now());
        return account;
    }


}
