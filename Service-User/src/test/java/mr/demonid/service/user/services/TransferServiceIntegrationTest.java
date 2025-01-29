package mr.demonid.service.user.services;

import mr.demonid.service.user.domain.Account;
import mr.demonid.service.user.domain.User;
import mr.demonid.service.user.dto.PaymentRequest;
import mr.demonid.service.user.exceptions.BadAccountException;
import mr.demonid.service.user.exceptions.NotEnoughAmountException;
import mr.demonid.service.user.exceptions.NotFoundException;
import mr.demonid.service.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционный тест, с реальной БД.
 * В качестве БД используем H2 в памяти, чтобы не портить рабочую.
 */
@SpringBootTest
@Transactional
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class TransferServiceIntegrationTest {

    @Autowired
    private TransferService transferService;

    @Autowired
    private UserRepository userRepository;

    User userFrom;
    User userTo;

    @BeforeEach
    public void setup() {
        // заносим в БД пару пользователей.
        userRepository.deleteAll();
        userFrom = createUser("userFrom", BigDecimal.valueOf(100));
        assertNotNull(userFrom, "userFrom не был сохранён в базе данных");
        assertNotNull(userFrom.getId(), "ID userFrom должен быть сгенерирован");
        userTo = createUser("userTo", BigDecimal.valueOf(50));
        assertNotNull(userTo, "userTo не был сохранён в базе данных");
        assertNotNull(userTo.getId(), "ID userTo должен быть сгенерирован");
    }

    /**
     * Поведение при корректных входных данных.
     */
    @Test
    void transferSuccess() {
        /*
            Подготовка.
         */
        PaymentRequest request = new PaymentRequest(UUID.randomUUID(), userFrom.getId(), userTo.getId(), BigDecimal.valueOf(20), "TRANSFER");
        /*
            Выполняем.
         */
        transferService.transfer(request);
        /*
            Проверяем. Для чего берем данные из БД.
         */
        User updatedUserFrom = userRepository.findById(userFrom.getId()).orElseThrow();
        User updatedUserTo = userRepository.findById(userTo.getId()).orElseThrow();
        assertEquals(BigDecimal.valueOf(80), updatedUserFrom.getPaymentAccount().getAmount());
        assertEquals(BigDecimal.valueOf(70), updatedUserTo.getPaymentAccount().getAmount());
    }

    /**
     * Проверяем поведение при отсутствии отправителя в БД.
     */
    @Test
    void transfer_NotFoundException() {
        /*
            Подготовка.
         */
        userRepository.delete(userFrom);
        // Создаем запрос с несуществующим ID отправителя
        PaymentRequest request = new PaymentRequest(UUID.randomUUID(), userFrom.getId(), userTo.getId(), BigDecimal.valueOf(20), "TRANSFER");
        /*
            Выполняем и ожидаем исключение NotFoundException()
         */
        assertThrows(NotFoundException.class, () -> transferService.transfer(request), "Ожидалось исключение NotFoundException, но оно не было выброшено");
    }

    /**
     * Проверяем поведение при недостатке средств на счете отправителя
     */
    @Test
    void transfer_NotEnoughAmountException() {
        /*
            Подготовка.
         */
        PaymentRequest request = new PaymentRequest(UUID.randomUUID(), userFrom.getId(), userTo.getId(), userFrom.getPaymentAccount().getAmount().add(BigDecimal.valueOf(20)), "TRANSFER");
        /*
            Выполняем и ожидаем исключение NotEnoughAmountException()
         */
        assertThrows(NotEnoughAmountException.class, () -> transferService.transfer(request), "Ожидалось исключение NotEnoughAmountException, но оно не было выброшено");
    }


    /**
     * Проверяем поведение при некорректном счёте пользователя (его отсутствии).
     */
    @Test
    void transfer_BadAccountException() {
        /*
            Подготовка.
         */
        userFrom.getAccounts().clear();
        userFrom = userRepository.save(userFrom);
        PaymentRequest request = new PaymentRequest(UUID.randomUUID(), userFrom.getId(), userTo.getId(), BigDecimal.valueOf(20), "TRANSFER");
        /*
            Выполняем и ожидаем исключение BadAccountException()
         */
        assertThrows(BadAccountException.class, () -> transferService.transfer(request), "Ожидалось исключение BadAccountException, но оно не было выброшено");
    }


    private User createUser(String name, BigDecimal balance) {
        User user = new User();
        user.setUsername(name);
        user.setPassword("1");
        user.setEmail("test-" + name + "@test.com");
        Account account = new Account();
        account.setName("Account-" + name);
        account.setAmount(balance);
        account.setCreation(LocalDate.now());
        user.addAccount(account);
        return userRepository.save(user);
    }

}
