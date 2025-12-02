package ru.effective_mobile.table_generator.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "bookings")
@Data
public class Booking {

    @Id
    @Column(name = "book_ref", columnDefinition = "char(6)")
    private String bookRef;

    @Column(name = "book_date", nullable = false)
    private OffsetDateTime bookDate;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
}
