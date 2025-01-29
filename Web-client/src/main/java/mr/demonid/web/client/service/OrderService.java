package mr.demonid.web.client.service;

import feign.FeignException;
import lombok.AllArgsConstructor;
import mr.demonid.web.client.dto.ProductReservationRequest;
import mr.demonid.web.client.links.OrderServiceClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {

    OrderServiceClient orderServiceClient;

    public UUID addOrder(long productId, long userId, int quantity, BigDecimal price) throws FeignException {
        ProductReservationRequest request = new ProductReservationRequest(null, userId, 1, productId, quantity, price);
        return orderServiceClient.createOrder(request).getBody();
    }

}
