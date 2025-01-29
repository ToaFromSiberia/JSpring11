package mr.demonid.service.catalog.services;

import mr.demonid.service.catalog.domain.Product;
import mr.demonid.service.catalog.dto.ProductReservationRequest;
import mr.demonid.service.catalog.exceptions.CatalogException;
import mr.demonid.service.catalog.exceptions.NotAvailableException;
import mr.demonid.service.catalog.exceptions.NotFoundException;
import mr.demonid.service.catalog.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Интеграционный тест.
 * Проверка проводится в контексте программы, где реальный
 * сервис тестируется в работе с замоканными зависимостями.
 */
@SpringBootTest(classes = ProductServiceIntegrationTest.TestConfig.class)
@ActiveProfiles(profiles = "test")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductServiceIntegrationTest {

    private static final long productId = 1L;
    private static final long userId = 1L;
    private static final int reserveQuantity = 5;


    private ProductService productService;
    private ProductRepository productRepository;
    private BlockedProductService blockedProductService;

    ProductReservationRequest request;
    Product product;


    /**
     * Конфигурация для теста. Заменяем часть зависимостей
     * моками или инициализируем реальными объектами.
     */
    @Configuration
    static class TestConfig {

        @Bean
        public ProductRepository productRepository() {
            return mock(ProductRepository.class);
        }

        @Bean
        public BlockedProductService blockedProductService() {
            return mock(BlockedProductService.class);
        }

        /*
         * Используем реальный сервис, поскольку его и будем тестировать.
         */
        @Bean
        public ProductService productService(ProductRepository productRepository, BlockedProductService blockedProductService) {
            return new ProductService(productRepository, blockedProductService);
        }
    }


    /**
     * Подготавливаем данные для каждого тестового метода.
     */
    @BeforeEach
    public void setup(@Autowired ProductService productService, @Autowired ProductRepository productRepository, @Autowired BlockedProductService blockedProductService) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.blockedProductService = blockedProductService;

        request = new ProductReservationRequest(UUID.randomUUID(), userId, productId, reserveQuantity, BigDecimal.valueOf(800));
        product = new Product();
        product.setId(productId);
        product.setName("Test Product");
        product.setStock(reserveQuantity);
        product.setPrice(BigDecimal.valueOf(800));
    }

    /**
     * Тест резервирования товара при его достаточном наличии.
     */
    @Test
    void testReserve_Success() throws CatalogException {
        /*
            Подготовка.
            Создадим запрос на резервирование и соответствующий продукт.
         */
        product.setStock(reserveQuantity+5);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        /*
            Выполнение
         */
        productService.reserve(request);
        /*
            Проверка
         */
        // Проверяем, что товар был зарезервирован, т.е. его остаток на складе
        assertEquals(5, product.getStock(), "Запас должен быть уменьшен на количество резервирования");
        // проверяем что методы вызывались с ожидаемыми параметрами
        verify(productRepository).save(product);
        verify(blockedProductService).reserve(request.getOrderId(), productId, reserveQuantity);
    }


    /**
     * Проверяем поведение при недостаточном количестве товара на складе.
     */
    @Test
    void testReserve_NotEnoughStock() {
        /*
            Подготовка.
            Создадим запрос на резервирование и соответствующий продукт.
         */
        product.setStock(reserveQuantity-1);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        /*
            Выполняем.
            Должно произойти исключение NotAvailableException.
         */
        assertThrows(NotAvailableException.class, () -> productService.reserve(request));
        /*
            Проверяем отсутствие вызова методов после исключения.
         */
        verifyNoInteractions(blockedProductService);                    // что не вызывался любой метод blockedProductService
        verify(productRepository, never()).save(any(Product.class));    // что не вызывался productRepository.save()
    }


    /**
     * Проверяем поведение при отсутствии запрашиваемого товара (рассинхронизация баз данных)
     */
    @Test
    void testReserve_ProductNotFound() {
        /*
            Подготовка.
         */
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.empty());   // вернет отсутствие товара в БД
        /*
            Выполняем.
            Должно произойти исключение NotFoundException
         */
        assertThrows(NotFoundException.class, () -> productService.reserve(request));
        /*
            Проверяем отсутствие вызова методов после исключения.
         */
        verifyNoInteractions(blockedProductService);                    // что не вызывался любой метод blockedProductService
        verify(productRepository, never()).save(any(Product.class));    // что не вызывался productRepository.save()
    }


}
