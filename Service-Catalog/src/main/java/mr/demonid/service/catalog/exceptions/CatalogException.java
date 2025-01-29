package mr.demonid.service.catalog.exceptions;

/**
 * Базовый класс исключений для модуля.
 * Пришлось сделать как RuntimeException, иначе требуется
 * делать проброс вплоть до методов контроллера.
 */
public abstract class CatalogException extends RuntimeException {

}
