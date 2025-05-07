package com.gestion.filmotheque.service;

import com.gestion.filmotheque.entities.Reservation;
import com.gestion.filmotheque.entities.Screening;
import com.gestion.filmotheque.entities.User;
import com.gestion.filmotheque.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public List<Reservation> getReservationsByUser(User user) {
        return reservationRepository.findByUser(user);
    }

    public List<Reservation> getReservationsByScreening(Screening screening) {
        return reservationRepository.findByScreening(screening);
    }

    public List<Reservation> getReservationsByUserAndStatus(User user, Reservation.ReservationStatus status) {
        return reservationRepository.findByUserAndStatus(user, status);
    }

    public int getReservedSeatsByScreening(Screening screening) {
        Integer count = reservationRepository.countReservedSeatsByScreening(screening);
        return count != null ? count : 0;
    }

    public boolean areSeatsAvailable(Screening screening, int requestedSeats) {
        int reservedSeats = getReservedSeatsByScreening(screening);
        return (screening.getRoom().getCapacity() - reservedSeats) >= requestedSeats;
    }

    public Reservation confirmReservation(Long id) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
            return reservationRepository.save(reservation);
        }
        return null;
    }

    public Reservation cancelReservation(Long id) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
            return reservationRepository.save(reservation);
        }
        return null;
    }
}