package mr.demonid.service.catalog.services;

import lombok.AllArgsConstructor;
import mr.demonid.service.catalog.domain.BlockedProduct;
import mr.demonid.service.catalog.domain.Product;
import mr.demonid.service.catalog.dto.ProductReservationRequest;
import mr.demonid.service.catalog.exceptions.CatalogException;
import mr.demonid.service.catalog.exceptions.NotAvailableException;
import mr.demonid.service.catalog.exceptions.NotFoundException;
import mr.demonid.service.catalog.repositories.ProductRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductService {

    private ProductRepository productRepository;
    private BlockedProductService blockedProductService;

    public void reserve(ProductReservationRequest request) throws CatalogException {
        Product product = productRepository.findById(request.getProductId()).orElse(null);
        if (product == null) {
            throw new NotFoundException();
        }
        if (product.getStock() < request.getQuantity()) {
            throw new NotAvailableException();
        }
        // резервируем товар
        product.setStock(product.getStock() - request.getQuantity());
        productRepository.save(product);
        blockedProductService.reserve(request.getOrderId(), product.getId(), request.getQuantity());
    }

    public void cancelReserved(UUID orderId) {
        BlockedProduct blockedProduct = blockedProductService.unblock(orderId);
        if (blockedProduct != null) {
            Product product = productRepository.findById(blockedProduct.getProductId()).orElse(null);
            if (product != null) {
                // возвращаем товар на место
                product.setStock(product.getStock() + blockedProduct.getQuantity());
                productRepository.save(product);
            }
        }
    }

    public void approvedReservation(UUID orderId) {
        BlockedProduct blockedProduct = blockedProductService.unblock(orderId);
        if (blockedProduct != null) {
            // да собственно больше ничего и не нужно делать, разве что в историю отправить.
        }
    }

    /**
     * Возвращает список всех товаров.
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Возвращает информацию по конкретному товару.
     * @param id Идентификатор товара.
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }


    /**
     * Конвертирует файл в строку Base64.
     */
    public String encodeImageToBase64(String fileName) {
        try {
            // ClassPathResource строит путь из src/main/resource, независимо от того, упакован файл в JAR, или выполняется из IDEA.
            ClassPathResource imgFile = new ClassPathResource("pics/" + fileName);
            Path imagePath = imgFile.getFile().toPath();
            System.out.println("load: " + imagePath);
            // Читаем байты и кодируем в Base64
            byte[] imageBytes = Files.readAllBytes(imagePath);
            return Base64.getEncoder().encodeToString(imageBytes);

        } catch (IOException e) {
            return "";
        }
    }
}
