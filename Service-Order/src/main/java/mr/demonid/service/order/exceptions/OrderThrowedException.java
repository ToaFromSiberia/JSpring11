package mr.demonid.service.order.exceptions;

public class OrderThrowedException extends OrderException {

    private String message;

    public OrderThrowedException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
