package ru.effective_mobile.table_generator.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Entity
@Table(name = "flights")
@Data
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "flights_seq")
    @SequenceGenerator(name = "flights_seq", sequenceName = "flights_flight_id_seq", allocationSize = 1)
    @Column(name = "flight_id")
    private Integer flightId;

    @Column(name = "flight_no", nullable = false, columnDefinition = "char(6)")
    private String flightNo;

    @Column(name = "scheduled_departure", nullable = false)
    private OffsetDateTime scheduledDeparture;

    @Column(name = "scheduled_arrival", nullable = false)
    private OffsetDateTime scheduledArrival;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_airport", nullable = false, columnDefinition = "char(3)")
    private Airport departureAirport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrival_airport", nullable = false, columnDefinition = "char(3)")
    private Airport arrivalAirport;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_code", nullable = false, columnDefinition = "char(3)")
    private Aircraft aircraft;

    @Column(name = "actual_departure")
    private OffsetDateTime actualDeparture;

    @Column(name = "actual_arrival")
    private OffsetDateTime actualArrival;
}
