package mr.demonid.service.payment.exceptions;

import java.util.UUID;

public class UnknownPaymentException extends PaymentException {

    private String message;

    public UnknownPaymentException(UUID orderId) {
        super(orderId);
        this.message = "Unknown payment exception";
    }

    public UnknownPaymentException(UUID orderId, String message) {
        super(orderId);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
