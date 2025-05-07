package com.gestion.filmotheque.controller;

import com.gestion.filmotheque.entities.Reservation;
import com.gestion.filmotheque.entities.Screening;
import com.gestion.filmotheque.entities.User;
import com.gestion.filmotheque.repository.UserRepository;
import com.gestion.filmotheque.service.ReservationService;
import com.gestion.filmotheque.service.ScreeningService;
import com.gestion.filmotheque.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;
    private final ScreeningService screeningService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public ReservationController(ReservationService reservationService, ScreeningService screeningService,
            UserService userService, UserRepository userRepository) {
        this.reservationService = reservationService;
        this.screeningService = screeningService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    // List all reservations - Admin only
    @GetMapping("/all")
    public String getAllReservations(Model model) {
        List<Reservation> reservations = reservationService.getAllReservations();
        model.addAttribute("reservations", reservations);
        model.addAttribute("isAdmin", userService.isAdmin());
        return "reservation/admin-reservation-list";
    }

    // Get user's reservations - Authenticated user only
    @GetMapping("/my")
    public String getMyReservations(Model model) {
        UserDetails userDetails = userService.getCurrentUser();
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Need to retrieve User entity from UserDetails
        // This implementation depends on your UserService implementation
        User user = getUserFromUserDetails(userDetails);
        if (user == null) {
            return "redirect:/login";
        }

        List<Reservation> reservations = reservationService.getReservationsByUser(user);
        model.addAttribute("reservations", reservations);
        model.addAttribute("isAdmin", userService.isAdmin());
        return "reservation/user-reservation-list";
    }

    // Show reservation form for a specific screening
    @GetMapping("/new/{screeningId}")
    public String showReservationForm(@PathVariable Long screeningId, Model model,
            RedirectAttributes redirectAttributes) {
        UserDetails userDetails = userService.getCurrentUser();
        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<Screening> screeningOpt = screeningService.getScreeningById(screeningId);
        if (screeningOpt.isPresent()) {
            Screening screening = screeningOpt.get();

            // Calculate available seats
            int reservedSeats = reservationService.getReservedSeatsByScreening(screening);
            int availableSeats = screening.calculateAvailableSeats(reservedSeats);

            model.addAttribute("screening", screening);
            model.addAttribute("availableSeats", availableSeats);
            model.addAttribute("isAdmin", userService.isAdmin());
            return "reservation/reservation-form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Séance introuvable.");
            return "redirect:/film/user/catalog";
        }
    }

    // Create a new reservation
    @PostMapping("/create")
    public String createReservation(
            @RequestParam("screeningId") Long screeningId,
            @RequestParam("numberOfSeats") int numberOfSeats,
            @RequestParam("phoneNumber") String phoneNumber,
            RedirectAttributes redirectAttributes) {

        UserDetails userDetails = userService.getCurrentUser();
        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = getUserFromUserDetails(userDetails);
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Screening> screeningOpt = screeningService.getScreeningById(screeningId);
        if (!screeningOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Séance introuvable.");
            return "redirect:/film/user/catalog";
        }

        Screening screening = screeningOpt.get();

        // Check if enough seats are available
        if (!reservationService.areSeatsAvailable(screening, numberOfSeats)) {
            redirectAttributes.addFlashAttribute("error", "Il n'y a pas assez de places disponibles.");
            return "redirect:/reservation/new/" + screeningId;
        }

        // Create and save reservation
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setScreening(screening);
        reservation.setNumberOfSeats(numberOfSeats);
        reservation.setPhoneNumber(phoneNumber);
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.calculateTotalPrice(); // This will set the total price

        reservationService.saveReservation(reservation);

        redirectAttributes.addFlashAttribute("success", "Réservation créée avec succès. En attente de confirmation.");
        return "redirect:/reservation/my";
    }

    // Cancel a reservation - User can cancel their own reservations
    @GetMapping("/cancel/{id}")
    public String cancelReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        UserDetails userDetails = userService.getCurrentUser();
        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<Reservation> reservationOpt = reservationService.getReservationById(id);
        if (!reservationOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Réservation introuvable.");
            return "redirect:/reservation/my";
        }

        Reservation reservation = reservationOpt.get();

        // Check if user is authorized to cancel this reservation
        User user = getUserFromUserDetails(userDetails);
        if (user == null
                || (!userService.isAdmin() && !reservation.getUser().getUsername().equals(user.getUsername()))) {
            redirectAttributes.addFlashAttribute("error", "Vous n'êtes pas autorisé à annuler cette réservation.");
            return "redirect:/reservation/my";
        }

        reservationService.cancelReservation(id);
        redirectAttributes.addFlashAttribute("success", "Réservation annulée avec succès.");

        if (userService.isAdmin()) {
            return "redirect:/reservation/all";
        } else {
            return "redirect:/reservation/my";
        }
    }

    // Confirm a reservation - Admin only
    @GetMapping("/confirm/{id}")
    public String confirmReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin()) {
            redirectAttributes.addFlashAttribute("error",
                    "Seuls les administrateurs peuvent confirmer les réservations.");
            return "redirect:/reservation/my";
        }

        Optional<Reservation> reservationOpt = reservationService.getReservationById(id);
        if (!reservationOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Réservation introuvable.");
            return "redirect:/reservation/all";
        }

        reservationService.confirmReservation(id);
        redirectAttributes.addFlashAttribute("success", "Réservation confirmée avec succès.");
        return "redirect:/reservation/all";
    }

    // View reservation details
    @GetMapping("/view/{id}")
    public String viewReservation(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        UserDetails userDetails = userService.getCurrentUser();
        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<Reservation> reservationOpt = reservationService.getReservationById(id);
        if (!reservationOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Réservation introuvable.");
            return "redirect:/reservation/my";
        }

        Reservation reservation = reservationOpt.get();

        // Check if user is authorized to view this reservation
        User user = getUserFromUserDetails(userDetails);
        if (user == null
                || (!userService.isAdmin() && !reservation.getUser().getUsername().equals(user.getUsername()))) {
            redirectAttributes.addFlashAttribute("error", "Vous n'êtes pas autorisé à voir cette réservation.");
            return "redirect:/reservation/my";
        }

        model.addAttribute("reservation", reservation);
        model.addAttribute("isAdmin", userService.isAdmin());
        return "reservation/reservation-view";
    }

    // Helper method to get User entity from UserDetails
    private User getUserFromUserDetails(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }

        Optional<User> userOpt = userRepository.findByUsername(userDetails.getUsername());
        return userOpt.orElse(null);
    }
}