package mr.demonid.service.payment.exceptions;

import java.util.UUID;

public class NotFoundException extends PaymentException {

    public NotFoundException(UUID orderId) {
        super(orderId);
    }

    @Override
    public String getMessage() {
        return "Пользователь не найден.";
    }
}
