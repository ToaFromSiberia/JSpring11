package mr.demonid.service.catalog.controllers;

import lombok.AllArgsConstructor;
import mr.demonid.service.catalog.domain.Product;
import mr.demonid.service.catalog.dto.ProductInfo;
import mr.demonid.service.catalog.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@AllArgsConstructor
public class ProductController {

    private ProductService productService;

    /**
     * Возвращает список всех доступных товаров.
     */
    @GetMapping("/get-all")
    public ResponseEntity<List<ProductInfo>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        System.out.println("all products: " + products);

        List<ProductInfo> res = products.stream().map(e -> new ProductInfo(e.getId(), e.getName(), e.getPrice(), e.getStock(), e.getDescription(), productService.encodeImageToBase64(e.getImageFile()))).toList();

        return ResponseEntity.ok(res);
    }

    @GetMapping("/get-product/{id}")
    public ResponseEntity<ProductInfo> getProductById(@PathVariable Long id) {
        Product e = productService.getProductById(id);
        if (e == null) {
            return ResponseEntity.notFound().build();
        }
        ProductInfo res = new ProductInfo(e.getId(), e.getName(), e.getPrice(), e.getStock(), e.getDescription(), productService.encodeImageToBase64(e.getImageFile()));
        return ResponseEntity.ok(res);
    }
}
