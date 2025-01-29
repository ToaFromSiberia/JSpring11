package mr.demonid.service.user.exceptions;

public class BadAccountException extends UserException {

    @Override
    public String getMessage() {
        return "Неверный счет пользователя.";
    }
}
