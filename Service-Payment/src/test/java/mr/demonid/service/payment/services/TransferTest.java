package mr.demonid.service.payment.services;

import feign.FeignException;
import feign.Request;
import mr.demonid.service.payment.domain.Payment;
import mr.demonid.service.payment.dto.PaymentRequest;
import mr.demonid.service.payment.exceptions.PaymentException;
import mr.demonid.service.payment.exceptions.ThrowedPaymentException;
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
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferTest {

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

    /**
     * Подготавливаем данные для каждого тестового метода.
     */
    @BeforeEach
    public void setup() {
        paymentRequest = new PaymentRequest(UUID.randomUUID(), fromUserId, recipientId, BigDecimal.valueOf(100), "DEBIT");
    }

    /**
     * Проверяем трансфер средств с одного счета на другой, при верных данных.
     */
    @Test
    public void testTransfer_SuccessfulTransaction() throws PaymentException {
        /*
            Подготовка.
         */
        Payment payment = new Payment(paymentRequest.getOrderId(), paymentRequest.getFromUserId(),
                paymentRequest.getRecipientId(), paymentRequest.getTransferAmount(),
                paymentRequest.getType(), LocalDateTime.now(), "Pending");
        when(paymentRepository.findById(paymentRequest.getOrderId())).thenReturn(Optional.of(payment));
        when(userServiceClient.transaction(paymentRequest)).thenReturn(ResponseEntity.ok().build());
        /*
            Выполняем и проверяем.
            Не должно быть никаких исключений.
         */
        assertDoesNotThrow(() -> paymentService.transfer(paymentRequest));
        assertEquals("Approved", payment.getStatus());              // статус должен обновиться до "Approved"
        verify(paymentRepository).save(payment);                    // и запись в БД тоже должна быть
    }

    /**
     * Проверяем реакцию на FeignException.
     */
    @Test
    public void testTransfer_FailureDueToFeignException() {
        /*
            Подготовка.
            Вызов метода userServiceClient.transaction(paymentRequest) будет вызывать
            исключение FeignException.BadRequest().
         */
        Map<String, Collection<String>> headers = new HashMap<>();
        headers.put("Content-Type", Collections.singletonList("application/json"));
        doThrow(new FeignException.BadRequest("Bad Request",
                Request.create(Request.HttpMethod.POST, "/api/user/account/transaction", headers, null, StandardCharsets.UTF_8, null),
                null,
                headers))
                .when(userServiceClient).transaction(paymentRequest);
        /*
            Выполняем и проверяем.
            В ответ на FeignException проверяемый метод должен выбросить ThrowedPaymentException()
         */
        assertThrows(ThrowedPaymentException.class, () -> paymentService.transfer(paymentRequest));
    }

}
