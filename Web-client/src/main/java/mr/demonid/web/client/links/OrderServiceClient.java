package mr.demonid.web.client.links;

import mr.demonid.web.client.dto.ProductReservationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

/**
 * Обращение к микросервису Order-service.
 * Поскольку он за API Gateway, то в @FeignClient так же
 * указываем путь к микросервису в API Gateway, через url.
 */
@FeignClient(name = "ORDER-SERVICE", url = "http://localhost:8090/ORDER-SERVICE")
public interface OrderServiceClient {

    @PostMapping("/api/orders")
    ResponseEntity<UUID> createOrder(@RequestBody ProductReservationRequest order);

}
