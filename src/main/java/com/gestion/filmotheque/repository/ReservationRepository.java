package com.gestion.filmotheque.repository;

import com.gestion.filmotheque.entities.Reservation;
import com.gestion.filmotheque.entities.Screening;
import com.gestion.filmotheque.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // Find reservations by user
    List<Reservation> findByUser(User user);

    // Find reservations by screening
    List<Reservation> findByScreening(Screening screening);

    // Find reservations by user and status
    List<Reservation> findByUserAndStatus(User user, Reservation.ReservationStatus status);

    // Count total seats reserved for a screening
    @Query("SELECT SUM(r.numberOfSeats) FROM Reservation r WHERE r.screening = :screening AND r.status != 'CANCELLED'")
    Integer countReservedSeatsByScreening(Screening screening);
}