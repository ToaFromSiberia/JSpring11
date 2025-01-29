package mr.demonid.service.payment.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class PaymentException extends RuntimeException {

    private final UUID orderId;

    public PaymentException(UUID orderId) {
        this.orderId = orderId;
    }

}
