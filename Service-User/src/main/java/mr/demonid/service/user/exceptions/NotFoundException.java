package mr.demonid.service.user.exceptions;

public class NotFoundException extends UserException {

    @Override
    public String getMessage() {
        return "Пользователь не найден.";
    }
}
