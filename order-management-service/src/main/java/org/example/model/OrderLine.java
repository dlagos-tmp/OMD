package org.example.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "order_lines", indexes = {
    @Index(name = "idx_order_lines_order_id", columnList = "order_id")
})
@Data
@ToString(exclude = "order")
@NoArgsConstructor
@EqualsAndHashCode(exclude = "order") // <-- Prevents recursion
@JsonIgnoreProperties(value = {"order"}, allowSetters = true)//when serializing OrderLine, ignore the order field to prevent infinite recursion
public class OrderLine implements Serializable {
    private static final long serialVersionUID = 202508052L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;


    @Column(name = "order_id", nullable = false, insertable = false, updatable = false)
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

}
