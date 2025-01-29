package mr.demonid.service.user.exceptions;

public class NotEnoughAmountException extends UserException{

    @Override
    public String getMessage() {
        return "Недостаточно средств для перевода.";
    }
}
