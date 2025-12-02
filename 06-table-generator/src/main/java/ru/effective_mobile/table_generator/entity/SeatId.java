package ru.effective_mobile.table_generator.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Embeddable
public class SeatId implements Serializable {
    private String aircraftCode;
    private String seatNo;
}