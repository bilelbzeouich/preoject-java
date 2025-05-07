package com.gestion.filmotheque.controller;

import com.gestion.filmotheque.entities.Room;
import com.gestion.filmotheque.service.RoomService;
import com.gestion.filmotheque.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;
    private final UserService userService;

    @Autowired
    public RoomController(RoomService roomService, UserService userService) {
        this.roomService = roomService;
        this.userService = userService;
    }

    // List all rooms - Admin only
    @GetMapping("/all")
    public String getAllRooms(Model model) {
        List<Room> rooms = roomService.getAllRooms();
        model.addAttribute("rooms", rooms);
        model.addAttribute("isAdmin", userService.isAdmin());
        return "room/room-list";
    }

    // Form to create a new room - Admin only
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("room", new Room());
        model.addAttribute("isAdmin", userService.isAdmin());
        return "room/room-form";
    }

    // Form to edit a room - Admin only
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Room> roomOpt = roomService.getRoomById(id);
        if (roomOpt.isPresent()) {
            model.addAttribute("room", roomOpt.get());
            model.addAttribute("isAdmin", userService.isAdmin());
            return "room/room-form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Salle introuvable.");
            return "redirect:/room/all";
        }
    }

    // Save or update a room - Admin only
    @PostMapping("/save")
    public String saveRoom(@ModelAttribute Room room, RedirectAttributes redirectAttributes) {
        try {
            roomService.saveRoom(room);
            redirectAttributes.addFlashAttribute("success", "Salle enregistrée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de l'enregistrement de la salle: " + e.getMessage());
        }
        return "redirect:/room/all";
    }

    // Delete a room - Admin only
    @GetMapping("/delete/{id}")
    public String deleteRoom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roomService.deleteRoom(id);
            redirectAttributes.addFlashAttribute("success", "Salle supprimée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de la suppression de la salle: " + e.getMessage());
        }
        return "redirect:/room/all";
    }

    // View room details - Admin only
    @GetMapping("/view/{id}")
    public String viewRoom(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Room> roomOpt = roomService.getRoomById(id);
        if (roomOpt.isPresent()) {
            model.addAttribute("room", roomOpt.get());
            model.addAttribute("isAdmin", userService.isAdmin());
            return "room/room-details";
        } else {
            redirectAttributes.addFlashAttribute("error", "Salle introuvable.");
            return "redirect:/room/all";
        }
    }
}