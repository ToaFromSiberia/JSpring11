package mr.demonid.service.catalog.exceptions;

/**
 * Исключение на случай если товара нет в БД.
 */
public class NotFoundException extends CatalogException {

    @Override
    public String getMessage() {
        return "Товар не найден.";
    }
}
