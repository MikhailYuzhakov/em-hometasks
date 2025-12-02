package ru.effective_mobile.table_generator.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "seats")
@Data
public class Seat {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "aircraftCode", column = @Column(name = "aircraft_code", columnDefinition = "char(3)")),
            @AttributeOverride(name = "seatNo", column = @Column(name = "seat_no", length = 4))
    })
    private SeatId id;

    @Column(name = "fare_conditions", nullable = false, length = 10)
    private String fareConditions; // Economy, Comfort, Business

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_code", insertable = false, updatable = false)
    private Aircraft aircraft;
}
