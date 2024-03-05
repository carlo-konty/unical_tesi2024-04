package com.tesi.unical.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "migration.orders")
@Builder
@Data
public class OrderModel {

    @Id
    @Column(name = "orderid")
    private Long id;

    @Column(name = "orderdate")
    private Date orderdate;

    @Column(name = "totalamount")
    private double totalamount;

    @Column(name = "customerid")
    private Long customerid;
}
