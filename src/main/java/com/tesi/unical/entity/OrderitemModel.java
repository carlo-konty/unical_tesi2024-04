package com.tesi.unical.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "migration.orderitems")
@Builder
@Data
public class OrderitemModel {

    @Id
    @Column(name = "orderitemid")
    private Long id;

    @Column(name = "orderid")
    private Long orderid;

    @Column(name = "productid")
    private Long productid;

    @Column(name = "quantity")
    private int quantity;
}
