package mr.demonid.service.catalog.services;

import mr.demonid.service.catalog.domain.BlockedProduct;
import mr.demonid.service.catalog.domain.Product;
import mr.demonid.service.catalog.dto.ProductReservationRequest;
import mr.demonid.service.catalog.exceptions.NotAvailableException;
import mr.demonid.service.catalog.exceptions.NotFoundException;
import mr.demonid.service.catalog.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    private static final long userId = 1L;
    private static final long productId = 5L;


    @Mock
    private ProductRepository productRepository;

    @Mock
    private BlockedProductService blockedProductService;

    @InjectMocks
    private ProductService productService;


    ProductReservationRequest request;
    Product product;

    /**
     * Подготавливаем данные для каждого тестового метода.
     */
    @BeforeEach
    public void setup() {
        request = new ProductReservationRequest(UUID.randomUUID(), userId, productId, 5, BigDecimal.valueOf(800));
        product = new Product();
        product.setId(productId);
        product.setName("Test Product");
        product.setStock(10);
        product.setPrice(BigDecimal.valueOf(800));
    }


    /**
     * Тестируем резервацию при наличии товара.
     */
    @Test
    void testReserve_SuccessfulReservation() {
        /*
            Подготовка.
            Создадим запрос на резервирование и соответствующий продукт.
         */
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(product));
//        when(productRepository.save(any(Product.class))).thenAnswer(e -> e.getArgument(0));
        /*
            Выполнение.
            Используем assertDoesNotThrow() для перехвата возможных исключений.
            И если исключение произойдет, то тест провалится.
         */
        assertDoesNotThrow(() -> productService.reserve(request));
        /*
            Проверка
         */
        assertEquals(5, product.getStock());        // ожидаем уменьшения количества на складе
        // проверяем, что методы save() и reserve() вызваны с ожидаемыми параметрами
        verify(productRepository).save(product);
        verify(blockedProductService).reserve(request.getOrderId(), product.getId(), request.getQuantity());
    }


    /**
     * Проверяем как отреагирует reserve() на отсутствие товара в БД.
     */
    @Test
    void testReserve_ProductNotFound() {
        /*
            Подготовка
         */
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.empty());
        /*
            Выполнение и проверка
         */
        assertThrows(NotFoundException.class, () -> productService.reserve(request));
        verifyNoInteractions(blockedProductService);        // убедимся, что blockedProductService не вызывался
    }


    /**
     * Проверяем как reserve() отреагирует на запрос большего кол-ва товаров,
     * нежели есть на складе.
     */
    @Test
    void testReserve_InsufficientStock() {
        /*
            Подготовка
         */
        request.setQuantity(product.getStock()+1);         // запросим на штуку больше чем есть на складе
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(product));
        /*
            Выполнение и проверка
         */
        assertThrows(NotAvailableException.class, () -> productService.reserve(request));
        verifyNoInteractions(blockedProductService);        // убедимся, что блокировка не вызвана
    }

    /**
     * Проверка работы отмены резерва.
     */
    @Test
    void testCancelReserved_UnblockAndRestoreStock() {
        /*
            Подготовка.
            Создадим BlockedProduct, которым мы должны вернуть в Product.
            Соответственно настроим unblock() и findById().
         */
        UUID orderId = UUID.randomUUID();
        int stock = product.getStock();
        BlockedProduct blockedProduct = new BlockedProduct(orderId, productId, 5);
        when(blockedProductService.unblock(orderId)).thenReturn(blockedProduct);
        when(productRepository.findById(blockedProduct.getProductId())).thenReturn(Optional.of(product));
        /*
            Выполнение
         */
        productService.cancelReserved(orderId);
        /*
            Проверка
         */
        assertEquals(stock+5, product.getStock());          // ожидаем восстановления количества на складе (stock+резерв)
        verify(productRepository).save(product);            // и вызов save с восстановленным товаром
    }

}
