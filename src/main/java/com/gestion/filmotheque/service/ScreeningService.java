package com.gestion.filmotheque.service;

import com.gestion.filmotheque.entities.Film;
import com.gestion.filmotheque.entities.Room;
import com.gestion.filmotheque.entities.Screening;
import com.gestion.filmotheque.repository.ScreeningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScreeningService {
    private final ScreeningRepository screeningRepository;

    @Autowired
    public ScreeningService(ScreeningRepository screeningRepository) {
        this.screeningRepository = screeningRepository;
    }

    public List<Screening> getAllScreenings() {
        return screeningRepository.findAll();
    }

    public Optional<Screening> getScreeningById(Long id) {
        return screeningRepository.findById(id);
    }

    public Screening saveScreening(Screening screening) {
        return screeningRepository.save(screening);
    }

    public void deleteScreening(Long id) {
        screeningRepository.deleteById(id);
    }

    public List<Screening> getScreeningsByFilm(Film film) {
        return screeningRepository.findByFilm(film);
    }

    public List<Screening> getScreeningsByRoom(Room room) {
        return screeningRepository.findByRoom(room);
    }

    public List<Screening> getFutureScreenings() {
        return screeningRepository.findByStartTimeAfter(LocalDateTime.now());
    }

    public List<Screening> getFutureScreeningsByFilm(Film film) {
        return screeningRepository.findByFilmAndStartTimeAfter(film, LocalDateTime.now());
    }

    public boolean isTimeSlotAvailable(Room room, LocalDateTime startTime, LocalDateTime endTime) {
        List<Screening> overlappingScreenings = screeningRepository.findOverlappingScreenings(room, startTime, endTime);
        return overlappingScreenings.isEmpty();
    }
}