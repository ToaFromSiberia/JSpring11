package mr.demonid.web.client.service;

import feign.FeignException;
import lombok.AllArgsConstructor;
import mr.demonid.web.client.dto.ProductInfo;
import mr.demonid.web.client.links.CatalogServiceClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CatalogService {

    private CatalogServiceClient catalogServiceClient;

    /**
     * Возвращает список всех товаров.
     */
    public List<ProductInfo> getProducts() {
        try {
            return catalogServiceClient.getAllProducts().getBody();
        } catch (FeignException e) {
            System.out.println("Что-то пошло не так: " + e.contentUTF8());
            return new ArrayList<>();
        }
    }

    /**
     * Возвращает информацию по конкретному товару.
     * @param productId Уникальный идентификатор товара.
     * @return null в случае ошибки.
     */
    public ProductInfo getProductById(Long productId) {
        try {
            return catalogServiceClient.getProductById(productId).getBody();
        } catch (FeignException e) {
            System.out.println("Что-то пошло не так: " + e.contentUTF8());
            return new ProductInfo(productId, "Unknown", BigDecimal.ZERO, 0, "Ошибка: " + e.contentUTF8(), "");
        }
    }

}
