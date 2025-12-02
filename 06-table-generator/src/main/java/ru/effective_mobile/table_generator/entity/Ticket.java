package ru.effective_mobile.table_generator.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tickets")
@Data
public class Ticket {

    @Id
    @Column(name = "ticket_no", columnDefinition = "char(13)")
    private String ticketNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_ref", nullable = false)
    private Booking booking;

    @Column(name = "passenger_id", nullable = false, length = 20)
    private String passengerId;

    @Column(name = "passenger_name", nullable = false)
    private String passengerName;

    @Column(name = "contact_data", columnDefinition = "jsonb")
    private String contactData;
}
