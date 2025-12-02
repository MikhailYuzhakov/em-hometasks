package ru.effective_mobile.table_generator.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Embeddable
public class TicketFlightId implements Serializable {
    private String ticketNo;
    private Integer flightId;
}
