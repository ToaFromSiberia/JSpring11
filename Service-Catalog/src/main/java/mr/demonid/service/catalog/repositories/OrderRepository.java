package mr.demonid.service.catalog.repositories;

import mr.demonid.service.catalog.domain.BlockedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Резервирование товаров.
 */
@Repository
public interface OrderRepository extends JpaRepository<BlockedProduct, UUID> {

}
