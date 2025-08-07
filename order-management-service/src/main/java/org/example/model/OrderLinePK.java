package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLinePK implements Serializable {
    private static final long serialVersionUID = -5847823799889990946L;

    @Column(name = "id")
    @Id
    private Long id;

    @Column(name = "order_id")
    @Id
    private Long orderId;

}

