package ru.effective_mobile.table_generator.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "airports_data")
@Data
public class Airport {

    @Id
    @Column(name = "airport_code", columnDefinition = "char(3)")
    private String airportCode;

    @Column(name = "airport_name", columnDefinition = "jsonb", nullable = false)
    private String airportName;

    @Column(name = "city", columnDefinition = "jsonb", nullable = false)
    private String city;

    // Postgres тип point. Hibernate не умеет мапить его в объект сам.
    // Для создания таблицы используем String и definition.
    @Column(name = "coordinates", columnDefinition = "point", nullable = false)
    private String coordinates; // Например: (37.617, 55.755)

    @Column(name = "timezone", nullable = false)
    private String timezone;
}
