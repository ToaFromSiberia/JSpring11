package mr.demonid.service.payment.services;

import mr.demonid.service.payment.domain.Payment;
import mr.demonid.service.payment.dto.PaymentRequest;
import mr.demonid.service.payment.dto.UserPayInfo;
import mr.demonid.service.payment.exceptions.NotEnoughAmountException;
import mr.demonid.service.payment.exceptions.NotFoundException;
import mr.demonid.service.payment.links.UserServiceClient;
import mr.demonid.service.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckTransferTest {

    private static final long fromUserId = 1L;      // от кого
    private static final long recipientId = 2L;     // кому
    private static final long accountId = 1L;       // откуда

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService; // Сервис, где находятся checkTransfer() и transfer()

    private PaymentRequest paymentRequest;
    private UserPayInfo userPayInfo;

    /**
     * Подготавливаем данные для каждого тестового метода.
     */
    @BeforeEach
    public void setup() {
        paymentRequest = new PaymentRequest(UUID.randomUUID(), fromUserId, recipientId, BigDecimal.valueOf(100), "DEBIT");
        userPayInfo = new UserPayInfo(fromUserId, accountId, BigDecimal.valueOf(500));
    }

    /**
     * Проверка баланса при наличии правильных данных о юзере и
     * достаточном кол-ве денег.
     */
    @Test
    public void testCheckTransfer_SufficientBalance() {
        /*
            Подготовка.
         */
        when(userServiceClient.getAccount(paymentRequest.getFromUserId())).thenReturn(ResponseEntity.ok(userPayInfo));
        /*
            Выполнение.
            Контролируем возможные исключения, поскольку тестируемый метод пробрасывает их далее.
            И если произойдет любое исключение, то тест провалится.
         */
        assertDoesNotThrow(() -> paymentService.checkTransfer(paymentRequest));

        // Проверяем, что вызвано сохранение платежа
        verify(paymentRepository).save(any(Payment.class));
    }

    /**
     * Проверка баланса при недостаточном кол-ве денег на счету.
     */
    @Test
    public void testCheckTransfer_InsufficientBalance() {
        /*
            Подготовка.
         */
        userPayInfo.setBalance(BigDecimal.valueOf(50));                 // устанавливаем недостаточный баланс
        when(userServiceClient.getAccount(paymentRequest.getFromUserId())).thenReturn(ResponseEntity.ok(userPayInfo));
        /*
            Выполнение и проверка.
            Ожидаем исключение NotEnoughAmountException()
         */
        assertThrows(NotEnoughAmountException.class, () -> paymentService.checkTransfer(paymentRequest));
    }

    /**
     * Проверка на отсутствие счета у пользователя.
     */
    @Test
    public void testCheckTransfer_UserNotFound() {
        /*
            Подготовка. Вернем отсутствие счёта.
         */
        when(userServiceClient.getAccount(paymentRequest.getFromUserId())).thenReturn(ResponseEntity.of(Optional.empty()));
        /*
            Выполнение и проверка.
            Ожидаем исключение NotFoundException()
         */
        assertThrows(NotFoundException.class, () -> paymentService.checkTransfer(paymentRequest));
    }
}
