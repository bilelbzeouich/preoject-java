package com.gestion.filmotheque.controller;

import com.gestion.filmotheque.entities.Film;
import com.gestion.filmotheque.entities.Room;
import com.gestion.filmotheque.entities.Screening;
import com.gestion.filmotheque.service.FilmService;
import com.gestion.filmotheque.service.ReservationService;
import com.gestion.filmotheque.service.RoomService;
import com.gestion.filmotheque.service.ScreeningService;
import com.gestion.filmotheque.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/screening")
public class ScreeningController {

    private final ScreeningService screeningService;
    private final FilmService filmService;
    private final RoomService roomService;
    private final UserService userService;
    private final ReservationService reservationService;

    @Autowired
    public ScreeningController(ScreeningService screeningService, FilmService filmService,
            RoomService roomService, UserService userService,
            ReservationService reservationService) {
        this.screeningService = screeningService;
        this.filmService = filmService;
        this.roomService = roomService;
        this.userService = userService;
        this.reservationService = reservationService;
    }

    // List all screenings - Admin only
    @GetMapping("/all")
    public String getAllScreenings(Model model) {
        List<Screening> screenings = screeningService.getAllScreenings();

        // Calculate available seats for each screening
        screenings.forEach(screening -> {
            int reservedSeats = reservationService.getReservedSeatsByScreening(screening);
            screening.setAvailableSeats(screening.calculateAvailableSeats(reservedSeats));
        });

        model.addAttribute("screenings", screenings);
        model.addAttribute("isAdmin", userService.isAdmin());
        return "screening/screening-list";
    }

    // Form to create a new screening - Admin only
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("screening", new Screening());
        model.addAttribute("films", filmService.getAllFilms());
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("isAdmin", userService.isAdmin());
        return "screening/screening-form";
    }

    // Form to edit a screening - Admin only
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Screening> screeningOpt = screeningService.getScreeningById(id);
        if (screeningOpt.isPresent()) {
            model.addAttribute("screening", screeningOpt.get());
            model.addAttribute("films", filmService.getAllFilms());
            model.addAttribute("rooms", roomService.getAllRooms());
            model.addAttribute("isAdmin", userService.isAdmin());
            return "screening/screening-form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Séance introuvable.");
            return "redirect:/screening/all";
        }
    }

    // Save or update a screening - Admin only
    @PostMapping("/save")
    public String saveScreening(
            @RequestParam("filmId") int filmId,
            @RequestParam("roomId") Long roomId,
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam("price") double price,
            RedirectAttributes redirectAttributes) {

        try {
            Optional<Film> filmOpt = filmService.getFilmById(filmId);
            Optional<Room> roomOpt = roomService.getRoomById(roomId);

            if (filmOpt.isPresent() && roomOpt.isPresent()) {
                Film film = filmOpt.get();
                Room room = roomOpt.get();

                // Check if the time slot is available
                if (!screeningService.isTimeSlotAvailable(room, startTime, endTime)) {
                    redirectAttributes.addFlashAttribute("error",
                            "Le créneau horaire n'est pas disponible pour cette salle.");
                    return "redirect:/screening/new";
                }

                Screening screening = new Screening();
                screening.setFilm(film);
                screening.setRoom(room);
                screening.setStartTime(startTime);
                screening.setEndTime(endTime);
                screening.setPrice(price);

                screeningService.saveScreening(screening);
                redirectAttributes.addFlashAttribute("success", "Séance enregistrée avec succès.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Film ou salle introuvable.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de l'enregistrement de la séance: " + e.getMessage());
        }
        return "redirect:/screening/all";
    }

    // Delete a screening - Admin only
    @GetMapping("/delete/{id}")
    public String deleteScreening(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            screeningService.deleteScreening(id);
            redirectAttributes.addFlashAttribute("success", "Séance supprimée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de la suppression de la séance: " + e.getMessage());
        }
        return "redirect:/screening/all";
    }

    // View screening details - Admin only
    @GetMapping("/view/{id}")
    public String viewScreening(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Screening> screeningOpt = screeningService.getScreeningById(id);
        if (screeningOpt.isPresent()) {
            Screening screening = screeningOpt.get();
            int reservedSeats = reservationService.getReservedSeatsByScreening(screening);
            screening.setAvailableSeats(screening.calculateAvailableSeats(reservedSeats));

            model.addAttribute("screening", screening);
            model.addAttribute("isAdmin", userService.isAdmin());
            return "screening/screening-details";
        } else {
            redirectAttributes.addFlashAttribute("error", "Séance introuvable.");
            return "redirect:/screening/all";
        }
    }

    // List screenings for a specific film
    @GetMapping("/film/{filmId}")
    public String getScreeningsByFilm(@PathVariable int filmId, Model model) {
        Optional<Film> filmOpt = filmService.getFilmById(filmId);
        if (filmOpt.isPresent()) {
            Film film = filmOpt.get();
            List<Screening> screenings = screeningService.getFutureScreeningsByFilm(film);

            // Calculate available seats for each screening
            screenings.forEach(screening -> {
                int reservedSeats = reservationService.getReservedSeatsByScreening(screening);
                screening.setAvailableSeats(screening.calculateAvailableSeats(reservedSeats));
            });

            // Group screenings by date
            Map<LocalDate, List<Screening>> screeningsByDate = screenings.stream()
                    .collect(Collectors.groupingBy(s -> s.getStartTime().toLocalDate()));

            model.addAttribute("film", film);
            model.addAttribute("screenings", screenings);
            model.addAttribute("screeningsByDate", screeningsByDate);
            model.addAttribute("isAdmin", userService.isAdmin());
            return "screening/film-screenings";
        } else {
            return "redirect:/film/user/catalog";
        }
    }
}