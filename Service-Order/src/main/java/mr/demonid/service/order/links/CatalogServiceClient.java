package mr.demonid.service.order.links;

import mr.demonid.service.order.dto.ProductReservationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "CATALOG-SERVICE")      // имя сервиса, под которым он зарегистрирован в Eureka
public interface CatalogServiceClient {

    @PostMapping("/api/catalog/reserve")
    ResponseEntity<String> reserve(@RequestBody ProductReservationRequest request);

    @PostMapping("/api/catalog/cancel")
    ResponseEntity<String> unblock(@RequestBody UUID orderId);

    @PostMapping("/api/catalog/approved")
    ResponseEntity<String> approve(@RequestBody UUID orderId);
}

