package mr.demonid.service.payment.exceptions;

import java.util.UUID;

public class ThrowedPaymentException extends PaymentException {

    private final String message;

    public ThrowedPaymentException(UUID orderId, String message) {
        super(orderId);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
