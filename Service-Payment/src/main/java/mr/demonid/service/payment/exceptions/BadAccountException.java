package mr.demonid.service.payment.exceptions;

import java.util.UUID;

public class BadAccountException extends PaymentException {

    public BadAccountException(UUID orderId) {
        super(orderId);
    }

    @Override
    public String getMessage() {
        return "Неверный счет пользователя.";
    }
}
