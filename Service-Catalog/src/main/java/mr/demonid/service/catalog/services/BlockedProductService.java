package mr.demonid.service.catalog.services;

import lombok.AllArgsConstructor;
import mr.demonid.service.catalog.domain.BlockedProduct;
import mr.demonid.service.catalog.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class BlockedProductService {

    private OrderRepository orderRepository;

    public void reserve(UUID orderId, long productId, int quantity) {
        orderRepository.save(new BlockedProduct(orderId, productId, quantity));
    }

    public BlockedProduct unblock(UUID orderId) {
        BlockedProduct blockedProduct = orderRepository.findById(orderId).orElse(null);
        orderRepository.deleteById(orderId);
        return blockedProduct;
    }
}
