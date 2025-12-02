package ru.effective_mobile.table_generator.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "boarding_passes")
@Data
public class BoardingPass {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "ticketNo", column = @Column(name = "ticket_no", columnDefinition = "char(13)")),
            @AttributeOverride(name = "flightId", column = @Column(name = "flight_id"))
    })
    private TicketFlightId id;

    // По сути OneToOne с ticket_flights, но для простоты структуры мапим по полям
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "ticket_no", referencedColumnName = "ticket_no", insertable = false, updatable = false),
            @JoinColumn(name = "flight_id", referencedColumnName = "flight_id", insertable = false, updatable = false)
    })
    private TicketFlight ticketFlight;

    @Column(name = "boarding_no", nullable = false)
    private Integer boardingNo;

    @Column(name = "seat_no", nullable = false, length = 4)
    private String seatNo;
}
