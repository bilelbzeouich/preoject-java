package com.gestion.filmotheque.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    @Column(nullable = false)
    private int numberOfSeats;

    @Column(nullable = false)
    private LocalDateTime reservationDate = LocalDateTime.now();

    @Column(length = 15)
    private String phoneNumber;

    // Status: PENDING, CONFIRMED, CANCELLED
    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.PENDING;

    // Total price calculated based on screening price * number of seats
    @Column(nullable = false)
    private double totalPrice;

    // Helper method to calculate total price
    @PrePersist
    @PreUpdate
    public void calculateTotalPrice() {
        if (screening != null) {
            this.totalPrice = screening.getPrice() * numberOfSeats;
        }
    }

    // Enum for reservation status
    public enum ReservationStatus {
        PENDING, CONFIRMED, CANCELLED
    }
}