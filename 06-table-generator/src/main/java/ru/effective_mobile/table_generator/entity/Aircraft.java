package ru.effective_mobile.table_generator.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "aircrafts_data")
@Data
public class Aircraft {

    @Id
    @Column(name = "aircraft_code", columnDefinition = "char(3)")
    private String aircraftCode;

    @Column(name = "model", columnDefinition = "jsonb", nullable = false)
    private String model; // JSON храним как строку

    @Column(name = "range", nullable = false)
    private Integer range;
}
