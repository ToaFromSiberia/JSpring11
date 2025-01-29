package mr.demonid.service.payment.exceptions;

import java.util.UUID;

public class NotEnoughAmountException extends PaymentException{

    public NotEnoughAmountException(UUID orderId) {
        super(orderId);
    }

    @Override
    public String getMessage() {
        return "Недостаточно средств для перевода.";
    }
}
