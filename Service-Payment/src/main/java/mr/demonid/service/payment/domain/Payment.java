package mr.demonid.service.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "payments")
public class Payment {
    @Id
    UUID orderId;
    private Long fromUserId;
    private Long recipientId;
    private BigDecimal transferAmount;
    private String type;            // "DEBIT", "CREDIT" и тд.
    private LocalDateTime paymentDate;
    private String status;          // approved, cancelled
}
