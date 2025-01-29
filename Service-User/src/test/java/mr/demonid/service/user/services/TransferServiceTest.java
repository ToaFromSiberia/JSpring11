package mr.demonid.service.user.services;

import mr.demonid.service.user.domain.Account;
import mr.demonid.service.user.domain.Role;
import mr.demonid.service.user.domain.User;
import mr.demonid.service.user.dto.PaymentRequest;
import mr.demonid.service.user.exceptions.BadAccountException;
import mr.demonid.service.user.exceptions.NotEnoughAmountException;
import mr.demonid.service.user.exceptions.NotFoundException;
import mr.demonid.service.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    private static final long userFromId = 1L;
    private static final long userToId = 2L;


    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransferService transferService;


    User userFrom;
    User userTo;
    PaymentRequest request;
    /**
     * Подготавливаем данные для каждого тестового метода.
     */
    @BeforeEach
    public void setup() {
        userFrom = createUser(userFromId, "UserFrom", "12345", "anon@mail.ru", Set.of(createAccount(1L, "222-333-444", BigDecimal.valueOf(100))));
        userTo = createUser(userToId, "UserTo", "12345", "anon@spam.ru", Set.of(createAccount(2L, "555-666-777", BigDecimal.valueOf(50))));
        request = new PaymentRequest(UUID.randomUUID(), userFromId, userToId, BigDecimal.valueOf(30), "DEBIT");
    }

    /**
     * Проверяем при корректных входных данных.
     */
    @Test
    void transferSuccess() {
        /*
            Подготовка
         */
        when(userRepository.findById(userFromId)).thenReturn(Optional.of(userFrom));
        when(userRepository.findById(userToId)).thenReturn(Optional.of(userTo));
        /*
            Выполняем.
         */
        assertDoesNotThrow(() -> transferService.transfer(request));
        /*
            Проверяем.
         */
        assertEquals(BigDecimal.valueOf(70), userFrom.getAccounts().stream().findFirst().orElse(new Account()).getAmount());
        assertEquals(BigDecimal.valueOf(80), userTo.getAccounts().stream().findFirst().orElse(new Account()).getAmount());
        verify(userRepository).save(userFrom);
        verify(userRepository).save(userTo);
    }

    /**
     * Проверяем поведение при отсутствии одного из пользователей в БД.
     */
    @Test
    void transfer_NotFountException() {
        /*
            Подготовка.
         */
        when(userRepository.findById(userFromId)).thenReturn(Optional.empty()); // user not found :)

        /*
            Выполняем и ожидаем исключение NotFoundException()
         */
        assertThrows(NotFoundException.class, () -> transferService.transfer(request));
    }


    /**
     * Проверяем поведение при отсутствии платежного счёта у пользователя.
     */
    @Test
    void transfer_BadAccountException() {
        /*
            Подготовка.
         */
        userFrom.setAccounts(new HashSet<>());
        when(userRepository.findById(userFromId)).thenReturn(Optional.of(userFrom));
        when(userRepository.findById(userToId)).thenReturn(Optional.of(userTo));
        /*
            Выполняем и ожидаем исключение BadAccountException()
         */
        assertThrows(BadAccountException.class, () -> transferService.transfer(request));
    }


    /**
     * Проверяем поведение при недостатке средств у источника.
     */
    @Test
    void  transfer_NotEnoughAmountException() {
        /*
            Подготовка.
         */
        Account account = userFrom.getPaymentAccount();
        account.setAmount(BigDecimal.valueOf(20));
        when(userRepository.findById(1L)).thenReturn(Optional.of(userFrom));
        when(userRepository.findById(2L)).thenReturn(Optional.of(userTo));
        /*
            Выполняем и ожидаем исключение NotEnoughAmountException().
         */
        assertThrows(NotEnoughAmountException.class, () -> transferService.transfer(request));
    }



    private Account createAccount(long id, String name, BigDecimal amount) {
        Account account = new Account();
        account.setId(id);
        account.setAmount(amount);
        account.setName(name);
        account.setCreation(LocalDate.now());
        return account;
    }

    private User createUser(long id, String username, String password, String email, Set<Account> accounts) {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        accounts.forEach(user::addAccount);
        user.addRole(role);
        return user;
    }

}
