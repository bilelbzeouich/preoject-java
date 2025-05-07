package com.gestion.filmotheque.repository;

import com.gestion.filmotheque.entities.Film;
import com.gestion.filmotheque.entities.Room;
import com.gestion.filmotheque.entities.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScreeningRepository extends JpaRepository<Screening, Long> {
    // Find screenings by film
    List<Screening> findByFilm(Film film);

    // Find screenings by room
    List<Screening> findByRoom(Room room);

    // Find future screenings
    List<Screening> findByStartTimeAfter(LocalDateTime dateTime);

    // Find screenings by film with start time after given date time
    List<Screening> findByFilmAndStartTimeAfter(Film film, LocalDateTime dateTime);

    // Find screenings by room that overlap with a given time range
    @Query("SELECT s FROM Screening s WHERE s.room = :room AND " +
            "((s.startTime BETWEEN :startTime AND :endTime) OR " +
            "(s.endTime BETWEEN :startTime AND :endTime) OR " +
            "(s.startTime <= :startTime AND s.endTime >= :endTime))")
    List<Screening> findOverlappingScreenings(Room room, LocalDateTime startTime, LocalDateTime endTime);
}