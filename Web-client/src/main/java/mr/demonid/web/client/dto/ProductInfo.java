package mr.demonid.web.client.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductInfo {
    private Long id;
    private String name;
    private BigDecimal price;
    private int stock;
    private String description;
    private String imageBase64;
}
