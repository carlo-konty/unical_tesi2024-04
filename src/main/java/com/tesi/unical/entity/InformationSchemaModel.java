package com.tesi.unical.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "information_schema.referential_constraint")
@AllArgsConstructor
@ToString
@Getter
@Setter
public class InformationSchemaModel {

    @Column(name = "CONSTRAINT_SCHEMA")
    private String constraintSchema;

    @Id
    @Column(name = "CONSTRAINT_NAME")
    private String constraintName;

}
