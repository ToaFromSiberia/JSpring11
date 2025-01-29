package mr.demonid.service.payment.services;

import feign.FeignException;
import lombok.AllArgsConstructor;
import mr.demonid.service.payment.domain.Payment;
import mr.demonid.service.payment.dto.PaymentRequest;
import mr.demonid.service.payment.dto.UserPayInfo;
import mr.demonid.service.payment.exceptions.*;
import mr.demonid.service.payment.links.UserServiceClient;
import mr.demonid.service.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PaymentService {

    private UserServiceClient userServiceClient;

    private PaymentRepository paymentRepository;

    /**
     * Проверка возможности трансфера средств.
     */
    public void checkTransfer(PaymentRequest request) throws PaymentException {
        try {
            // регистируем запрос на перевод средств
            Payment payment = new Payment(request.getOrderId(),
                    request.getFromUserId(),
                    request.getRecipientId(),
                    request.getTransferAmount(),
                    request.getType(),
                    LocalDateTime.now(),
                    "Pending");
            paymentRepository.save(payment);

            // запрашиваем данные о клиенте
            UserPayInfo user = userServiceClient.getAccount(request.getFromUserId()).getBody();
            BigDecimal userBalance = user.getBalance();
            if (userBalance.compareTo(request.getTransferAmount()) < 0) {
                throw new NotEnoughAmountException(request.getOrderId());
            }
        } catch (NullPointerException e) {
            // user == null
            throw new NotFoundException(request.getOrderId());
        } catch (FeignException e) {
            // просто прокидываем ошибку дальше
            throw new ThrowedPaymentException(request.getOrderId(), e.contentUTF8());
        } catch (PaymentException e){
            throw e;
        } catch (Exception e) {
            throw new UnknownPaymentException(request.getOrderId(), e.getMessage());
        }
    }

    public void transfer(PaymentRequest request) throws PaymentException {
        try {
            userServiceClient.transaction(request);
            // обновляем статус операции на завершенный
            Optional<Payment> payment = paymentRepository.findById(request.getOrderId());
            if (payment.isPresent()) {
                payment.get().setStatus("Approved");
                paymentRepository.save(payment.get());
            }
        } catch (FeignException e) {
            // просто прокидываем ошибку дальше
            throw new ThrowedPaymentException(request.getOrderId(), e.contentUTF8());
        }
    }
}
