package ru.effective_mobile.table_generator.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "ticket_flights")
@Data
public class TicketFlight {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "ticketNo", column = @Column(name = "ticket_no", columnDefinition = "char(13)")),
            @AttributeOverride(name = "flightId", column = @Column(name = "flight_id"))
    })
    private TicketFlightId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_no", insertable = false, updatable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", insertable = false, updatable = false)
    private Flight flight;

    @Column(name = "fare_conditions", nullable = false, length = 10)
    private String fareConditions;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
}
