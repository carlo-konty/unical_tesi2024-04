package com.tesi.unical.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "migration.customers")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class UserModel {

    @Id
    @Column(name = "customerId")
    private Long customerId;

    @Column(name = "name")
    private String customerName;

    @Column(name = "email")
    private String customerEmail;

}
