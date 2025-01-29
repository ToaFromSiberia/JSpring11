package mr.demonid.web.client.links;

import mr.demonid.web.client.dto.ProductInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Обращение к микросервису Catalog-service.
 * Поскольку он за API Gateway, то в @FeignClient так же
 * указываем путь к микросервису в API Gateway, через url.
 */
@FeignClient(name = "CATALOG-SERVICE", url = "http://localhost:8090/CATALOG-SERVICE")      // имя сервиса, под которым он зарегистрирован в Eureka
public interface CatalogServiceClient {

    @GetMapping("/api/catalog/get-all")
    ResponseEntity<List<ProductInfo>> getAllProducts();

    @GetMapping("/api/catalog/get-product/{id}")
    ResponseEntity<ProductInfo> getProductById(@PathVariable Long id);
}

