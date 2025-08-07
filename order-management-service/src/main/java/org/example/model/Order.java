package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@ToString(exclude = "orderLines")
@EqualsAndHashCode(exclude = "orderLines") // <-- Prevents recursion
@JsonIgnoreProperties(value = {"orderId"}, allowGetters = true)
public class Order implements Serializable {
    private static final long serialVersionUID = 202508051L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long orderId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String status = "unprocessed";

    @CreationTimestamp
    private LocalDateTime orderDate;

    @OneToMany(fetch = FetchType.EAGER , mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    @JsonManagedReference
    private List<OrderLine> orderLines = new ArrayList<>();

    //todo remove
    public void addOrderLine(OrderLine orderLine) {
        orderLines.add(orderLine);
    }

    public void removeOrderLine(OrderLine orderLine) {
        orderLines.remove(orderLine);
    }
}
