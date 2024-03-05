package com.tesi.unical.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;


@Entity
@Table(name = "migration.products")
@Builder
@Data
public class ProductModel {

    @Id
    @Column(name = "productid")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private double price;

}
